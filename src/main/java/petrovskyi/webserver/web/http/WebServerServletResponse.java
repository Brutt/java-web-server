package petrovskyi.webserver.web.http;

import petrovskyi.webserver.web.http.adapter.HttpServletResponseAdapter;
import petrovskyi.webserver.web.stream.WebServerOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class WebServerServletResponse extends HttpServletResponseAdapter implements AutoCloseable{
    private final String CHARACTER_ENCODING = "UTF-8";
    private WebServerOutputStream webServerOutputStream;
    private PrintWriter printWriter;


    public WebServerServletResponse(WebServerOutputStream webServerOutputStream) {
        this.webServerOutputStream = webServerOutputStream;
    }

    @Override
    public String getCharacterEncoding() {
        return CHARACTER_ENCODING;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        printWriter = new PrintWriter(new OutputStreamWriter(webServerOutputStream, this.getCharacterEncoding()));

        return printWriter;
    }

    public void close() {
        printWriter.close();
    }

}
