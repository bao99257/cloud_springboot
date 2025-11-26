package com.example.web_security.service;


import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.web_security.Repo.ProductRepository;
import com.example.web_security.model.Product;

import jakarta.servlet.http.HttpSession;

@Service
public class CartService {

    @Autowired
    private HttpSession session;

    @Autowired
    private ProductRepository productRepository;

    private Map<Long, Integer> getCart() {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    public void addProduct(Long id) {
        Map<Long, Integer> cart = getCart();
        cart.put(id, cart.getOrDefault(id, 0) + 1);
    }

    public void removeItem(Long id) {
        Map<Long, Integer> cart = getCart();
        cart.remove(id);
    }

    public void clearCart() {
        session.removeAttribute("cart");
    }

    public List<CartItem> getCartDetails() {
        List<CartItem> items = new ArrayList<>();
        Map<Long, Integer> cart = getCart();

        for (Long id : cart.keySet()) {
            Product p = productRepository.findById(id).orElse(null);
            if (p != null) {
                items.add(new CartItem(p, cart.get(id)));
            }
        }
        return items;
    }

    public double getTotalPrice() {
        return getCartDetails().stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();
    }
}