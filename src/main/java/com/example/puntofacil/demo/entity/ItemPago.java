package com.example.puntofacil.demo.entity;

import java.math.BigDecimal;

public class ItemPago {
    private String title;
    private Integer quantity;
    private BigDecimal unitPrice;

    // Constructor vacío
    public ItemPago() {}

    // Constructor con parámetros
    public ItemPago(String title, Integer quantity, BigDecimal unitPrice) {
        this.title = title;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // ========== GETTERS Y SETTERS ==========
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    // Método útil para calcular subtotal
    public BigDecimal getSubtotal() {
        if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "ItemPago{" +
                "title='" + title + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
