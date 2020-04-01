package petrovskyi.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String servletPath = request.getServletPath();
        if ("/login".equals(servletPath) || "/logout".equals(servletPath) || servletPath.startsWith("/assets")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            HttpSession httpSession = request.getSession();
            String name = (String) httpSession.getAttribute("name");

            if (name == null) {
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
