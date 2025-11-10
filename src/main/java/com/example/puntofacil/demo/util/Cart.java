package com.example.puntofacil.demo.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {

    // clave productoId -> CartItem
    private final Map<Long, CartItem> items = new LinkedHashMap<>();

    public void add(CartItem item) {
        Long id = item.getProductoId();
        if (items.containsKey(id)) {
            // sumar cantidades
            CartItem existing = items.get(id);
            existing.setCantidad(existing.getCantidad().add(item.getCantidad()));
        } else {
            items.put(id, item);
        }
    }

    public void updateQuantity(Long productoId, BigDecimal cantidad) {
        if (items.containsKey(productoId)) {
            if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                items.remove(productoId);
            } else {
                items.get(productoId).setCantidad(cantidad);
            }
        }
    }

    public void remove(Long productoId) {
        items.remove(productoId);
    }

    public void clear() {
        items.clear();
    }

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public BigDecimal getTotal() {
        return items.values().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
