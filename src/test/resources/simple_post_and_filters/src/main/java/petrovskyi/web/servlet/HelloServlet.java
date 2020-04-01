package petrovskyi.web.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        HttpSession httpSession = request.getSession();
        String name = (String) httpSession.getAttribute("name");

        out.write("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <link rel=\"stylesheet\" href=\"assets/css/style.css\">" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<H3>Welcome " + name + "</H3>\n" +
                "    <form action=\"logout\" method=\"post\">\n" +
                "        <input type=\"submit\" value=\"logout\"/>\n" +
                "    </form>\n" +
                "<br>" +
                "<img src=\"assets/user.svg\">" +
                "<img src=\"assets/intro.jpg\">" +
                "<br>" +
                "<br>" +
                "<a href=\"car/json\">car json</a>" +
                "</body>\n" +
                "</html>");
    }
}
