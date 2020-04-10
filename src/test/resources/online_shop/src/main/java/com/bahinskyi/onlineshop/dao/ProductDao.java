package com.bahinskyi.onlineshop.dao;

import com.bahinskyi.onlineshop.entity.Product;

import java.util.List;

public interface ProductDao {
    List<Product> getAll();

    void addProduct(Product product);

    void editProduct(Product product);

    Product getProductById(int id);

    void deleteProduct(int id);
}
