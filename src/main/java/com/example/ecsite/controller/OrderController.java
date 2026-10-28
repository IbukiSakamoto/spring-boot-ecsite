package com.example.ecsite.controller;

import com.example.ecsite.model.Cart;
import com.example.ecsite.model.Order;
import com.example.ecsite.service.OrderService;
import com.example.ecsite.service.OutOfStockException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final Cart cart;

    public OrderController(OrderService orderService, Cart cart) {
        this.orderService = orderService;
        this.cart = cart;
    }

    @PostMapping("/checkout")
    public String checkout(
            @RequestParam(name = "customerName", required = false) String customerName,
            RedirectAttributes redirectAttributes) {

        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "カートに商品が入っていません。");
            return "redirect:/cart";
        }

        try {
            Order order = orderService.processOrder(cart, customerName);
            return "redirect:/order/complete/" + order.getId();
        } catch (OutOfStockException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/complete/{orderId}")
    public String orderComplete(@PathVariable("orderId") Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/products";
        }

        model.addAttribute("order", order);
        return "order/complete";
    }
}
