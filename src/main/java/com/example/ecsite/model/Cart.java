package com.example.ecsite.model;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@SessionScope
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Long, CartItem> items = new LinkedHashMap<>();

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public CartItem getItem(Long productId) {
        return items.get(productId);
    }

    public void addItem(Product product, int quantity) {
        if (product == null || product.getId() == null || quantity <= 0) {
            return;
        }
        CartItem existing = items.get(product.getId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.put(product.getId(), new CartItem(product, quantity));
        }
    }

    public void updateQuantity(Long productId, int quantity) {
        if (items.containsKey(productId)) {
            if (quantity <= 0) {
                items.remove(productId);
            } else {
                items.get(productId).setQuantity(quantity);
            }
        }
    }

    public void removeItem(Long productId) {
        items.remove(productId);
    }

    public void clear() {
        items.clear();
    }

    public int getTotalAmount() {
        return items.values().stream()
                .mapToInt(CartItem::getSubtotal)
                .sum();
    }

    public int getTotalQuantity() {
        return items.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
