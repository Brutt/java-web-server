package petrovskyi.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

public class CarJsonFilter implements Filter {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOG.info("Car Json filter");

        filterChain.doFilter(servletRequest, servletResponse);
    }

}