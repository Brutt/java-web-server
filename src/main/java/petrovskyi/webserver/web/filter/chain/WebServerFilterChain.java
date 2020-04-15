package petrovskyi.webserver.web.filter.chain;

import javax.servlet.*;
import java.io.IOException;
import java.util.Queue;

public class WebServerFilterChain implements FilterChain {
    private Queue<Filter> filters;

    public WebServerFilterChain(Queue<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        Filter filter = filters.poll();
        if (filter == null) {
            return;
        }

        filter.doFilter(request, response, this);
    }
}
