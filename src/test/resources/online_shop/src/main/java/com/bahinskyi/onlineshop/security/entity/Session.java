package com.bahinskyi.onlineshop.security.entity;

import com.bahinskyi.onlineshop.entity.CartItem;
import com.bahinskyi.onlineshop.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class Session {
    private String token;
    private User user;
    private LocalDateTime expireDate;
    private List<CartItem> cartItems;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
