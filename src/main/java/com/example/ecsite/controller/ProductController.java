package com.example.ecsite.controller;

import com.example.ecsite.model.Cart;
import com.example.ecsite.model.Product;
import com.example.ecsite.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    private final Cart cart;

    public ProductController(ProductService productService, Cart cart) {
        this.productService = productService;
        this.cart = cart;
    }

    @GetMapping({"/", "/products"})
    public String listProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sort", required = false, defaultValue = "default") String sort,
            Model model) {

        List<Product> products = productService.getProducts(keyword, sort);

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("currentSort", sort);
        model.addAttribute("cartItemCount", cart.getTotalQuantity());

        return "products/list";
    }
}
