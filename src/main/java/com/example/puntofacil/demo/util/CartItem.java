package com.example.puntofacil.demo.util;

import java.math.BigDecimal;

public class CartItem {
    private Long productoId;
    private String nombre;
    private String unidadMedida;
    private BigDecimal precioUnitario; // precio_base del producto (por kg, por unidad, etc)
    private BigDecimal cantidad; // en la misma unidad que producto.stock (ej: kg)

    public CartItem() {}

    public CartItem(Long productoId, String nombre, String unidadMedida, BigDecimal precioUnitario, BigDecimal cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(cantidad);
    }
}
