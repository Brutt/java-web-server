package com.bahinskyi.onlineshop.web.filter;

import com.bahinskyi.onlineshop.entity.User;
import com.bahinskyi.onlineshop.entity.UserRole;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminSecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        User user = (User) request.getAttribute("user");

        if (user.getUserRole().equals(UserRole.ADMIN)) {
            chain.doFilter(request, response);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {

    }
}
