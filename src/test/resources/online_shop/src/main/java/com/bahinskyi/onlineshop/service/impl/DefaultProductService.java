package com.bahinskyi.onlineshop.service.impl;

import com.bahinskyi.onlineshop.dao.ProductDao;
import com.bahinskyi.onlineshop.entity.Product;
import com.bahinskyi.onlineshop.service.ProductService;
import com.bahinskyi.onlineshop.web.ServiceLocator;

import java.util.List;

public class DefaultProductService implements ProductService {
    private ProductDao productDao = ServiceLocator.getService(ProductDao.class);

    @Override
    public List<Product> getAll() {
        return productDao.getAll();
    }

    @Override
    public void add(Product product) {
        productDao.addProduct(product);
    }

    @Override
    public void edit(Product product) {
        productDao.editProduct(product);
    }

    @Override
    public Product getById(int id) {
        return productDao.getProductById(id);
    }

    @Override
    public void delete(int id) {
        productDao.deleteProduct(id);
    }

}
