package com.example.ecsite.controller;

import com.example.ecsite.model.Cart;
import com.example.ecsite.model.CartItem;
import com.example.ecsite.model.Product;
import com.example.ecsite.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final Cart cart;
    private final ProductService productService;

    public CartController(Cart cart, ProductService productService) {
        this.cart = cart;
        this.productService = productService;
    }

    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("totalAmount", cart.getTotalAmount());
        model.addAttribute("totalQuantity", cart.getTotalQuantity());
        return "cart/view";
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam("productId") Long productId,
            @RequestParam(name = "quantity", defaultValue = "1") int quantity,
            RedirectAttributes redirectAttributes) {

        if (quantity <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "数量は1以上を指定してください。");
            return "redirect:/products";
        }

        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定された商品が存在しません。");
            return "redirect:/products";
        }

        Product product = productOpt.get();
        CartItem existingItem = cart.getItem(productId);
        int currentInCart = existingItem != null ? existingItem.getQuantity() : 0;
        int requestedTotal = currentInCart + quantity;

        if (product.getStock() < requestedTotal) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "「" + product.getName() + "」の在庫が不足しています。(在庫数: " + product.getStock() + "個、現在カート内: " + currentInCart + "個)");
            return "redirect:/products";
        }

        cart.addItem(product, quantity);
        redirectAttributes.addFlashAttribute("successMessage",
                "「" + product.getName() + "」をカートに追加しました。");

        return "redirect:/products";
    }

    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") int quantity,
            RedirectAttributes redirectAttributes) {

        if (quantity <= 0) {
            cart.removeItem(productId);
            redirectAttributes.addFlashAttribute("successMessage", "商品をカートから削除しました。");
            return "redirect:/cart";
        }

        Optional<Product> productOpt = productService.getProductById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (product.getStock() < quantity) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "「" + product.getName() + "」の在庫が不足しています。(最大注文可能数: " + product.getStock() + "個)");
                return "redirect:/cart";
            }
            cart.updateQuantity(productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "数量を更新しました。");
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(
            @RequestParam("productId") Long productId,
            RedirectAttributes redirectAttributes) {

        cart.removeItem(productId);
        redirectAttributes.addFlashAttribute("successMessage", "商品をカートから削除しました。");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        cart.clear();
        redirectAttributes.addFlashAttribute("successMessage", "カートを空にしました。");
        return "redirect:/cart";
    }
}
