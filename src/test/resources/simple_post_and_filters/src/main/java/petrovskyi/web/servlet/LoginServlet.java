package petrovskyi.web.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.slf4j.spi.DefaultLoggingEventBuilder;
import org.slf4j.spi.LoggingEventBuilder;
import petrovskyi.security.SecurityService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginServlet extends HttpServlet {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private SecurityService securityService = new SecurityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoggingEventBuilder loggingEventBuilder = new DefaultLoggingEventBuilder(LOG, Level.ERROR);
        loggingEventBuilder.log(" =========== Hello, World! ===========");

        PrintWriter writer = response.getWriter();
        writer.write("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <form action=\"login\" method=\"post\">\n" +
                "        Name:<input type=\"text\" name=\"name\"/><br/>\n" +
                "        Password:<input type=\"password\" name=\"password\"/><br/>\n" +
                "        <input type=\"submit\" value=\"login\"/>\n" +
                "    </form>\n" +
                "</body>\n" +
                "</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("run doPost()");
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        LOG.info("doPost({})", name);
        LOG.debug("doPost({}, {})", name, password);

        boolean loginResult = securityService.login(name, password);
        if (loginResult) {
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("name", name);
            response.sendRedirect(request.getContextPath() + "/hello");
        } else {
            HttpSession httpSession = request.getSession();
            httpSession.removeAttribute("name");
            response.sendRedirect(request.getContextPath() + "/login");
        }

    }
}