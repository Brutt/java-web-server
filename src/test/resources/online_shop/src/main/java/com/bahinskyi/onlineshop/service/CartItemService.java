package com.bahinskyi.onlineshop.service;

import com.bahinskyi.onlineshop.entity.CartItem;
import com.bahinskyi.onlineshop.security.entity.Session;

import java.util.List;

public interface CartItemService {
    void addCartItem(int id, Session session);

    void deleteCartItem(int id, Session session);

    double totalSum(List<CartItem> cartItemList);
}
