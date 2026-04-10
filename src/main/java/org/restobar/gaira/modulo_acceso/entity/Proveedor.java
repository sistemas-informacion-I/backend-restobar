package org.restobar.gaira.acceso.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "proveedor", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Long idProveedor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", unique = true)
    private Usuario usuario;

    @NotBlank(message = "Empresa no puede estar vacía")
    @Column(name = "empresa", nullable = false, length = 200)
    private String empresa;

    @NotBlank(message = "NIT no puede estar vacío")
    @Pattern(regexp = "^\\d{1,13}$", message = "NIT debe contener solo dígitos")
    @Column(name = "nit", nullable = false, unique = true, length = 20)
    private String nit;

    @Column(name = "nombre_contacto", length = 150)
    private String nombreContacto;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Email(message = "Correo de contacto debe ser válido")
    @Column(name = "correo_contacto", length = 150)
    private String correoContacto;

    @Column(name = "categoria_producto", length = 50)
    private String categoriaProducto;
}
