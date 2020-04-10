package com.bahinskyi.onlineshop.service;

import com.bahinskyi.onlineshop.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAll();

    void add(Product product);

    void edit(Product product);

    Product getById(int id);

    void delete(int id);
}
