package com.bahinskyi.onlineshop.web.servlet;

import com.bahinskyi.onlineshop.security.SecurityService;
import com.bahinskyi.onlineshop.security.entity.Session;
import com.bahinskyi.onlineshop.web.ServiceLocator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    private SecurityService securityService = ServiceLocator.getService(SecurityService.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("user-token")) {
                        String token = cookie.getValue();
                        Session session = securityService.getUserSession(token);

                        securityService.logout(session);

                        Cookie userTokenCookieRemove = new Cookie("user-token", "");
                        userTokenCookieRemove.setMaxAge(0);
                        response.addCookie(userTokenCookieRemove);
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (IOException e) {
            throw new RuntimeException("Logout error", e);
        }
    }
}
