package com.bahinskyi.onlineshop.web.servlet;

import com.bahinskyi.onlineshop.service.ProductService;
import com.bahinskyi.onlineshop.web.ServiceLocator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteProductServlet extends HttpServlet {
    private ProductService productService = ServiceLocator.getService(ProductService.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String uri = request.getRequestURI();
            String[] partsOfUri = uri.split("/");
            int id = Integer.parseInt(partsOfUri[partsOfUri.length - 1]);

            productService.delete(id);
            response.sendRedirect(request.getContextPath() + "/products");

        } catch (IOException e) {
            throw new RuntimeException("AllProductsServlet error", e);
        }
    }
}
