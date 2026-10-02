package pe.edu.nova.java.starters.boot.env;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ConfigurableApplicationContext;
import pe.edu.nova.java.starters.boot.NovaApplication;
import pe.edu.nova.java.starters.boot.service.MetaStarterTestApplication;

/**
 * El post-procesador corre cuando el servicio arranca. Se arranca un servicio de verdad, con
 * {@code NovaApplication.run}, y se mira lo que escribió en la consola: si Spring Boot no lo
 * encuentra, o si loguea por un camino que se pierde, la línea no aparece.
 */
@ExtendWith(OutputCaptureExtension.class)
class NovaEnvironmentPostProcessorStartupTest {

    @Test
    void theValidationRunsOnceWhenTheServiceStartsAndSaysWhichVersionsItFound(CapturedOutput output) {
        try (ConfigurableApplicationContext context =
                NovaApplication.run(MetaStarterTestApplication.class, "--spring.main.web-application-type=none")) {
            assertThat(context.isRunning()).isTrue();
        }

        assertThat(output.getAll())
                .containsOnlyOnce("[Nova Platform] Validación exitosa — Java: " + Runtime.version().feature()
                        + ", Spring Boot: " + SpringBootVersion.getVersion());
    }
}
