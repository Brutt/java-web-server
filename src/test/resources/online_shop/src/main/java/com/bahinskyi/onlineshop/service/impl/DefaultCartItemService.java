package com.bahinskyi.onlineshop.service.impl;

import com.bahinskyi.onlineshop.entity.CartItem;
import com.bahinskyi.onlineshop.entity.Product;
import com.bahinskyi.onlineshop.security.entity.Session;
import com.bahinskyi.onlineshop.service.CartItemService;
import com.bahinskyi.onlineshop.service.ProductService;
import com.bahinskyi.onlineshop.web.ServiceLocator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultCartItemService implements CartItemService {
    private ProductService productService = ServiceLocator.getService(ProductService.class);


    @Override
    public void addCartItem(int id, Session session) {
        Product product = productService.getById(id);
        List<CartItem> cartItemList = session.getCartItems();
        if (cartItemList != null) {
            boolean isFind = false;
            for (CartItem cartItem : cartItemList) {
                if (cartItem.getProduct().equals(product)) {
                    cartItem.setCountProducts(cartItem.getCountProducts() + 1);
                    cartItem.setTotal(cartItem.getTotal() + product.getPrice());
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                CartItem cartItem = getNewCartItem(product);
                cartItemList.add(cartItem);
            }
        } else {
            CartItem newCartItem = getNewCartItem(product);
            List<CartItem> cartItems = new ArrayList<>();
            cartItems.add(newCartItem);
            session.setCartItems(cartItems);
        }
    }

    @Override
    public void deleteCartItem(int id, Session session) {
        Product product = productService.getById(id);
        List<CartItem> cartItemList = session.getCartItems();
        Iterator<CartItem> itemIterator = cartItemList.iterator();
        while (itemIterator.hasNext()) {
            CartItem cartItem = itemIterator.next();
            if(cartItem.getProduct().equals(product)) {
                itemIterator.remove();
                break;
            }
        }
    }

    @Override
    public double totalSum(List<CartItem> cartItemList) {
        double total = 0;
        if (cartItemList != null) {
            for (CartItem cartItem : cartItemList) {
                total += cartItem.getTotal();
            }
        }
        return total;
    }

    private CartItem getNewCartItem(Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setCountProducts(1);
        cartItem.setTotal(product.getPrice());
        return cartItem;
    }
}
