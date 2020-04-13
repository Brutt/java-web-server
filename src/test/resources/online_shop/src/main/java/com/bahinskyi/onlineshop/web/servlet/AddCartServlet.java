package com.bahinskyi.onlineshop.web.servlet;

import com.bahinskyi.onlineshop.security.entity.Session;
import com.bahinskyi.onlineshop.service.CartItemService;
import com.bahinskyi.onlineshop.web.ServiceLocator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddCartServlet extends HttpServlet {
    private CartItemService cartItemService = ServiceLocator.getService(CartItemService.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String uri = request.getRequestURI();
            String[] partsOfUri = uri.split("/");
            int id = Integer.parseInt(partsOfUri[partsOfUri.length - 1]);

            Session session = (Session) request.getAttribute("session");

            cartItemService.addCartItem(id, session);

            response.sendRedirect(request.getContextPath() + "/products");
        } catch (IOException e) {
            throw new RuntimeException("AddProductServlet doPost error", e);
        }
    }
}
