package pe.edu.nova.java.starters.boot.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Lo que contesta un servicio que solo declara el meta-starter y no configura nada. El starter de
 * enmascaramiento viene incluido porque, sin configuración, no cambia el comportamiento del
 * servicio (ADR-052): solo enmascara lo que se anota. Un {@code name} o un {@code email} sin
 * anotación se contesta tal cual.
 */
@SpringBootTest(classes = MetaStarterTestApplication.class)
@AutoConfigureMockMvc
class MetaStarterMaskingTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void aProductNameIsNotMaskedByDefault() throws Exception {
        mvc.perform(get("/catalog/products/1"))
                .andExpect(status().isOk())
                // El sobre del estándar de API, para que se vea que los dos starters actúan juntos.
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Taza"))
                .andExpect(jsonPath("$.data.category").value("Hogar"));
    }

    @Test
    void aDtoWithoutAnnotationsIsAnsweredInTheClear() throws Exception {
        mvc.perform(get("/catalog/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Ceramicas del Sur"))
                .andExpect(jsonPath("$.data.email").value("ventas@ceramicasdelsur.pe"));
    }

    @Test
    void aFieldThatCarriesMaskedIsStillMasked() throws Exception {
        mvc.perform(get("/catalog/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Juan Perez"))
                .andExpect(jsonPath("$.data.email").value("j*********@acme.pe"));
    }
}
