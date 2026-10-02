package pe.edu.nova.java.starters.boot.env;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;

/**
 * Cómo se registra el post-procesador. Spring Boot 4 lee la clave
 * {@code org.springframework.boot.EnvironmentPostProcessor}. La de Spring Boot 3,
 * {@code org.springframework.boot.env.EnvironmentPostProcessor}, sigue funcionando por ahora pero
 * está deprecada para retirarse, y el día que se retire el componente dejaría de correr sin que
 * nada falle.
 */
class NovaEnvironmentPostProcessorRegistrationTest {

    private static final String SPRING_BOOT_4_KEY = "org.springframework.boot.EnvironmentPostProcessor";

    private static final String SPRING_BOOT_3_KEY = "org.springframework.boot.env.EnvironmentPostProcessor";

    private static final String PROCESSOR = NovaEnvironmentPostProcessor.class.getName();

    @Test
    void theProcessorIsRegisteredWithTheSpringBoot4Key() throws IOException {
        assertThat(factoriesOfThisStarter().getProperty(SPRING_BOOT_4_KEY)).contains(PROCESSOR);
    }

    @Test
    void theProcessorIsNotRegisteredWithTheDeprecatedKeyToo() throws IOException {
        // Con las dos claves Spring Boot lo instanciaría dos veces y la línea saldría dos veces.
        assertThat(factoriesOfThisStarter().getProperty(SPRING_BOOT_3_KEY)).isNull();
    }

    /** El {@code spring.factories} de este starter, y no el de otro jar que también lo tenga. */
    private static Properties factoriesOfThisStarter() throws IOException {
        ClassLoader classLoader = NovaEnvironmentPostProcessorRegistrationTest.class.getClassLoader();
        List<URL> candidates = Collections.list(classLoader.getResources("META-INF/spring.factories"));
        for (URL url : candidates) {
            Properties factories = new Properties();
            try (InputStream in = url.openStream()) {
                factories.load(in);
            }
            if (factories.values().stream().anyMatch(value -> value.toString().contains(PROCESSOR))) {
                return factories;
            }
        }
        throw new AssertionError("Ningún spring.factories registra a " + PROCESSOR + " en " + candidates);
    }
}
