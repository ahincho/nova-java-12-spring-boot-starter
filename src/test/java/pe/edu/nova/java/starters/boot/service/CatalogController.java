package pe.edu.nova.java.starters.boot.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.nova.java.libs.mask.utils.MaskType;
import pe.edu.nova.java.libs.mask.utils.annotation.Masked;

/**
 * El controlador del servicio de las pruebas: devuelve objetos de dominio sin envolver, como lo hace
 * un controlador real, y deja que los starters del meta-starter armen la respuesta.
 */
@RestController
@RequestMapping("/catalog")
class CatalogController {

    /**
     * Un producto: su {@code name} es un nombre comercial, no un dato personal.
     *
     * @return el producto
     */
    @GetMapping("/products/1")
    public Product product() {
        return new Product(1, "Taza", "Hogar");
    }

    /**
     * Un proveedor, con un objeto sin ninguna anotación de enmascaramiento.
     *
     * @return el proveedor
     */
    @GetMapping("/suppliers/1")
    public Supplier supplier() {
        return new Supplier(1, "Ceramicas del Sur", "ventas@ceramicasdelsur.pe");
    }

    /**
     * Un cliente, con su correo marcado como dato personal.
     *
     * @return el cliente
     */
    @GetMapping("/customers/1")
    public Customer customer() {
        return new Customer(1, "Juan Perez", "juan.perez@acme.pe");
    }

    /**
     * Un producto.
     *
     * @param id el identificador
     * @param name el nombre comercial
     * @param category la categoría
     */
    public record Product(long id, String name, String category) {}

    /**
     * Un proveedor sin anotaciones.
     *
     * @param id el identificador
     * @param name la razón social
     * @param email el correo comercial
     */
    public record Supplier(long id, String name, String email) {}

    /**
     * Un cliente con el correo marcado.
     *
     * @param id el identificador
     * @param name el nombre, sin anotación
     * @param email el correo, marcado
     */
    public record Customer(long id, String name, @Masked(type = MaskType.EMAIL) String email) {}
}
