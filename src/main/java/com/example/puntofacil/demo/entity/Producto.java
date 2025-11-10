
package com.example.puntofacil.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_producto", nullable = false, unique = true)
    private Integer codigoProducto;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "codigo_barra", unique = true)
    private String codigoBarra;

    @Column(name = "precio_base", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioBase;

    @Column(name = "unidad_medida", length = 10)
    private String unidadMedida; // "UNIDAD", "KG", "GR", "LT", "ML"

    @Column(precision = 10, scale = 3)
    private BigDecimal stock;

    // ✅ Campo para categoría (ya lo tenías)
    @Column(name = "categoria_id")
    private Long categoriaId = 1L; // Valor por defecto

    // ✅ NUEVO: nombre de archivo de la imagen del producto
    // Se guarda solo el nombre, NO el binario.
    @Column(name = "imagen", length = 255)
    private String imagen;

    public Producto() {}

    public Producto(Integer codigoProducto, String nombre, String codigoBarra,
                    BigDecimal precioBase, String unidadMedida, BigDecimal stock) {
        this.codigoProducto = codigoProducto;
        this.nombre = nombre;
        this.codigoBarra = codigoBarra;
        this.precioBase = precioBase;
        this.unidadMedida = unidadMedida;
        this.stock = stock;
        this.categoriaId = 1L; // Valor por defecto en constructor
    }

    // =======================
    // Getters y Setters
    // =======================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(Integer codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoBarra() { return codigoBarra; }
    public void setCodigoBarra(String codigoBarra) { this.codigoBarra = codigoBarra; }

    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public BigDecimal getStock() { return stock; }
    public void setStock(BigDecimal stock) { this.stock = stock; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    // ✅ NUEVOS getter/setter para imagen
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
