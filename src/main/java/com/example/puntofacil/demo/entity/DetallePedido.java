package com.example.puntofacil.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalles_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private BigDecimal cantidadUnidad;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    // ===== CONSTRUCTORES =====
    public DetallePedido() {}

    public DetallePedido(Producto producto, BigDecimal cantidad, BigDecimal precioUnitario) {
        this.producto = producto;
        this.cantidadUnidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // ===== GETTERS & SETTERS =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getCantidadUnidad() {
        return cantidadUnidad;
    }

    public void setCantidadUnidad(BigDecimal cantidadUnidad) {
        this.cantidadUnidad = cantidadUnidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    // ===== MÉTODOS AUXILIARES =====
    public BigDecimal getSubtotal() {
        if (cantidadUnidad == null || precioUnitario == null) return BigDecimal.ZERO;
        return cantidadUnidad.multiply(precioUnitario);
    }
}
