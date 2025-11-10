package com.example.puntofacil.demo.entity;

import java.math.BigDecimal;

public class CarritoItem {

    private Producto producto;
    private int cantidad;

    public CarritoItem(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getSubtotal() {
        return producto.getPrecioBase().multiply(BigDecimal.valueOf(cantidad));
    }
}
