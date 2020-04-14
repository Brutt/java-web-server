package petrovskyi.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ResultServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();

        String message = "";

        HttpSession httpSession = req.getSession();
        Object messagesObject = httpSession.getAttribute("messages");
        if (messagesObject != null) {
            List<String> messages = (List<String>) messagesObject;
            message = String.join("</h2>\n" + "<h2>", messages);
        }

        writer.write("<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "    <title>Upload Result</title>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<h2>" + (message.isEmpty() ? "Empty" : message) + "</h2>\n" +
                "</body>\n" +
                "</html>");
    }

}
