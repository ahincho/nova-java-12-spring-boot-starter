package pe.edu.nova.java.starters.boot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Lo que un servicio recibe con solo declarar el meta-starter.
 *
 * <p>Cada clase existe desde la versión que el meta-starter tiene que traer, y no antes. Si una
 * dependencia volviera a resolverse a una versión vieja, como pasó con la 1.0.4, que repartía los
 * starters 2.x, la clase falta y la prueba falla.</p>
 */
class MetaStarterClasspathTest {

    @ParameterizedTest(name = "{0}")
    @CsvSource({
        "the layered errors of ADR-031, pe.edu.nova.java.libs.api.standard.error.DomainError",
        "the API standard starter, pe.edu.nova.java.starters.apistandard.autoconfigure.ApiStandardAutoConfiguration",
        "the mask starter, pe.edu.nova.java.starters.mask.autoconfigure.MaskAutoConfiguration",
        "the observability starter 3.0.0,"
                + " pe.edu.nova.java.starters.observability.config.NovaObservabilityEnvironmentPostProcessor",
        "the secret store imports, pe.edu.nova.java.libs.secrets.SecretImports",
        "the secrets starter, pe.edu.nova.java.starters.secrets.NovaSecretsEnvironmentPostProcessor",
    })
    void bringsInWhatAServiceExpects(String what, String className) {
        assertDoesNotThrow(() -> Class.forName(className), what + " is missing from the classpath");
    }
}
