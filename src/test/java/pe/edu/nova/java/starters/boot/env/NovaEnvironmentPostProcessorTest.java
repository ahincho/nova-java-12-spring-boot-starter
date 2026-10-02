package pe.edu.nova.java.starters.boot.env;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.mock.env.MockEnvironment;
import pe.edu.nova.java.starters.boot.RecordingLog;

/**
 * Qué valida el post-procesador y qué dice cuando todo está bien. Se invoca directamente, con un log
 * que anota lo que recibe, así que no depende de cómo imprima la consola de quien corre las pruebas.
 */
class NovaEnvironmentPostProcessorTest {

    private final List<String> log = new ArrayList<>();

    private final NovaEnvironmentPostProcessor processor =
            new NovaEnvironmentPostProcessor(destination -> RecordingLog.into(log));

    @Test
    void itSaysWhichVersionsItFoundWhenBothAreSupported() {
        processor.postProcessEnvironment(new MockEnvironment(), new SpringApplication());

        assertThat(log)
                .containsExactly("info: [Nova Platform] Validación exitosa — Java: " + Runtime.version().feature()
                        + ", Spring Boot: " + SpringBootVersion.getVersion());
    }

    @ParameterizedTest
    @ValueSource(ints = {25, 26, 30})
    void javaFromTheMinimumOnIsAccepted(int version) {
        assertThatCode(() -> processor.validarVersionJava(version)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {17, 21, 24})
    void javaBelowTheMinimumStopsTheStartupAndSaysWhichVersionItFound(int version) {
        assertThatThrownBy(() -> processor.validarVersionJava(version))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Se requiere Java 25 o superior")
                .hasMessageContaining("Versión detectada: " + version);
    }

    @ParameterizedTest
    @ValueSource(strings = {"4.0.0", "4.0.8", "4.1.0", "4.2.3-SNAPSHOT"})
    void anyMinorOfSpringBoot4IsAccepted(String version) {
        assertThatCode(() -> processor.validarVersionSpringBoot(version)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"3.5.6", "5.0.0"})
    void anotherMajorOfSpringBootStopsTheStartup(String version) {
        assertThatThrownBy(() -> processor.validarVersionSpringBoot(version))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Se requiere Spring Boot 4.x")
                .hasMessageContaining("Versión detectada: " + version);
    }

    @Test
    void aSpringBootVersionThatCannotBeReadStopsTheStartup() {
        assertThatThrownBy(() -> processor.validarVersionSpringBoot(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se pudo determinar la versión de Spring Boot");
    }

    @Test
    void aSpringBootVersionThatIsNotANumberStopsTheStartup() {
        assertThatThrownBy(() -> processor.validarVersionSpringBoot("cuatro.cero"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Se requiere Spring Boot 4.x");
    }
}
