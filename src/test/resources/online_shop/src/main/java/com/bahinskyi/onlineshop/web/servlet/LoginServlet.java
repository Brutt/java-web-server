package com.bahinskyi.onlineshop.web.servlet;

import com.bahinskyi.onlineshop.security.entity.Session;
import com.bahinskyi.onlineshop.exception.LoginPasswordInvalidException;
import com.bahinskyi.onlineshop.security.SecurityService;
import com.bahinskyi.onlineshop.web.ServiceLocator;
import com.bahinskyi.onlineshop.web.templater.PageGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginServlet extends HttpServlet {
    private static final String ALL_PRODUCTS_TEMPLATE_HTML = "login.html";
    private PageGenerator pageGenerator = PageGenerator.instance();
    private SecurityService securityService = ServiceLocator.getService(SecurityService.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        try {
            Map<String, Object> paramsMap = new HashMap<>();

            pageGenerator.process(ALL_PRODUCTS_TEMPLATE_HTML, paramsMap, response.getWriter());

        } catch (IOException e) {
            throw new RuntimeException("AllProductsServlet error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String login = request.getParameter("login");
            String password = request.getParameter("password");
            try {
                Session session = securityService.login(login, password);
                Cookie cookie = new Cookie("user-token", session.getToken());


                response.addCookie(cookie);
                response.sendRedirect(request.getContextPath() + "/products");
            } catch (LoginPasswordInvalidException e) {
                Map<String, Object> paramsMap = new HashMap<>();
                paramsMap.put("invalid", "yes");
                pageGenerator.process(ALL_PRODUCTS_TEMPLATE_HTML, paramsMap, response.getWriter());

                response.sendRedirect(request.getContextPath() + "/login");
            }
        } catch (IOException e) {
            throw new RuntimeException("LoginServlet doPost error", e);
        }
    }
}
