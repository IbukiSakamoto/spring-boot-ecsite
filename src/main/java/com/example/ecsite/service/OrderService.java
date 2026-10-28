package com.example.ecsite.service;

import com.example.ecsite.model.Cart;
import com.example.ecsite.model.CartItem;
import com.example.ecsite.model.Order;
import com.example.ecsite.model.OrderItem;
import com.example.ecsite.model.Product;
import com.example.ecsite.repository.OrderRepository;
import com.example.ecsite.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order processOrder(Cart cart, String customerName) {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("カートに商品が入っていません。");
        }

        String name = (customerName != null && !customerName.trim().isEmpty())
                ? customerName.trim()
                : "ゲスト";

        // 1. 在庫確認と減算
        for (CartItem cartItem : cart.getItems()) {
            Long productId = cartItem.getProduct().getId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("商品が見つかりません (ID: " + productId + ")"));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new OutOfStockException("商品「" + product.getName() + "」の在庫が不足しています。(現在庫: " + product.getStock() + "個)");
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // 2. 注文情報作成
        Order order = new Order(name, cart.getTotalAmount());
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            OrderItem item = new OrderItem(
                    order,
                    product,
                    product.getName(),
                    product.getPrice(),
                    cartItem.getQuantity(),
                    cartItem.getSubtotal()
            );
            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);

        // 3. カートをクリア
        cart.clear();

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}
