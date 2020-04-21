package petrovskyi.webserver.web.filter.chain;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.Queue;

public class WebServerFilterChain implements FilterChain {
    private Queue<Filter> filters;
    private HttpServlet httpServlet;

    public WebServerFilterChain(Queue<Filter> filters, HttpServlet httpServlet) {
        this.filters = filters;
        this.httpServlet = httpServlet;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        Filter filter = filters.poll();
        if (filter == null) {
            httpServlet.service(request, response);

            return;
        }

        filter.doFilter(request, response, this);
    }
}
