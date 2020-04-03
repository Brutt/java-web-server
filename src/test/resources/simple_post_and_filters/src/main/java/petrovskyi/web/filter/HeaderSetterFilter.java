package petrovskyi.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeaderSetterFilter implements Filter {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOG.info("run doFilter()");
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        LOG.info("doFilter() | ask for request: {}", httpServletRequest.getRequestURI());

        setHeaders(httpServletRequest, httpServletResponse);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }

    private void setHeaders(HttpServletRequest request, HttpServletResponse response) {
        LOG.info("run setHeaders()");

        if (!request.getRequestURI().contains("/assets")) {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
        } else if (request.getRequestURI().contains("/css")) {
            response.setContentType("text/css");
        } else if (request.getRequestURI().contains("/js")) {
            response.setContentType("application/javascript");
        } else if (request.getRequestURI().contains(".svg")) {
            response.setContentType("image/svg+xml");
        } else if (request.getRequestURI().contains(".ico")) {
            response.setContentType("image/x-icon");
        }
    }

}