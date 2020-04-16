package com.bahinskyi.onlineshop.web.filter;

import com.bahinskyi.onlineshop.entity.User;
import com.bahinskyi.onlineshop.entity.UserRole;
import com.bahinskyi.onlineshop.security.SecurityService;
import com.bahinskyi.onlineshop.security.entity.Session;
import com.bahinskyi.onlineshop.web.ServiceLocator;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;

public class RoleFilter implements Filter {
    private SecurityService securityService = ServiceLocator.getService(SecurityService.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        Cookie[] cookies = httpServletRequest.getCookies();

        boolean isNotGuest = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user-token")) {
                    String token = cookie.getValue();
                    Session session = securityService.getUserSession(token);

                    if (session != null) {
                        if (EnumSet.of(UserRole.ADMIN, UserRole.USER).contains(session.getUser().getUserRole())) {
                            isNotGuest = true;
                            request.setAttribute("session", session);
                            request.setAttribute("user", session.getUser());
                        } else {
                            Cookie userTokenCookieRemove = new Cookie("user-token", "");
                            userTokenCookieRemove.setMaxAge(0);
                            httpServletResponse.addCookie(userTokenCookieRemove);
                        }
                    }
                    break;
                }
            }
        }
        if (!isNotGuest) {
            User user = new User();
            user.setLogin("GUEST");
            user.setUserRole(UserRole.GUEST);

            request.setAttribute("user", user);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
