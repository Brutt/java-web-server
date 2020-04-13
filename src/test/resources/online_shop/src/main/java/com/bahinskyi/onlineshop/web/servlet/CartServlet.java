package com.bahinskyi.onlineshop.web.servlet;

import com.bahinskyi.onlineshop.entity.CartItem;
import com.bahinskyi.onlineshop.entity.User;
import com.bahinskyi.onlineshop.security.entity.Session;
import com.bahinskyi.onlineshop.service.CartItemService;
import com.bahinskyi.onlineshop.web.ServiceLocator;
import com.bahinskyi.onlineshop.web.templater.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartServlet extends HttpServlet {
    private static final String CART_TEMPLATE = "cart.html";
    private PageGenerator pageGenerator = PageGenerator.instance();
    private CartItemService cartItemService = ServiceLocator.getService(CartItemService.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Map<String, Object> paramsMap = new HashMap<>();

            User user = (User) request.getAttribute("user");
            if (user != null) {
                paramsMap.put("login", user.getLogin());
                paramsMap.put("userRole", user.getUserRole().getUserRoleName());
            }

            Session session = (Session) request.getAttribute("session");
            List<CartItem> cartItems = session.getCartItems();
            paramsMap.put("cartItems", cartItems);
            paramsMap.put("totalCount", cartItemService.totalSum(cartItems));

            pageGenerator.process(request, response, CART_TEMPLATE, paramsMap);
        } catch (IOException e) {
            throw new RuntimeException("AllProductsServlet error", e);
        }

    }
}
