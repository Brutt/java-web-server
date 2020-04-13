package petrovskyi.webserver.web.http.response;

import petrovskyi.webserver.web.stream.WebServerOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class WebServerServletResponse extends HttpServletResponseAdapter implements AutoCloseable {
    private String characterEncoding = "UTF-8";
    private WebServerOutputStream webServerOutputStream;
    private PrintWriter printWriter;


    public WebServerServletResponse(WebServerOutputStream webServerOutputStream) {
        this.webServerOutputStream = webServerOutputStream;
    }

    @Override
    public void addCookie(Cookie cookie) {
        webServerOutputStream.addCookie(cookie);
    }

    @Override
    public void sendRedirect(String s) {
        webServerOutputStream.sendRedirect(s);
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return webServerOutputStream;
    }

    @Override
    public String encodeURL(String s) {
        return s;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        printWriter = new PrintWriter(new OutputStreamWriter(webServerOutputStream, this.getCharacterEncoding()));

        return printWriter;
    }

    @Override
    public void setCharacterEncoding(String s) {
        characterEncoding = s;
    }

    @Override
    public void setContentType(String s) {
        webServerOutputStream.setContentType(s);
    }

    public void close() throws IOException {
        if (printWriter != null) {
            printWriter.close();
        } else {
            webServerOutputStream.close();
        }
    }

}
