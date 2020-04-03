package petrovskyi.webserver.web.stream;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;


@Slf4j
public class WebServerOutputStream extends ServletOutputStream {
    //
    // https://tools.ietf.org/html/rfc2616#section-2.2
    // HTTP/1.1 defines the sequence CR LF as the end-of-line marker for all protocol elements except the entity-body
    //
    private final String END_LINE = "\r\n";
    private final String GOOD_HEADER = "HTTP/1.1 200 OK";
    private final OutputStream outputStream;
    private String contentType;
    private boolean isHeaderAdded = false;
    private boolean isStatusCodeSet = false;
    private String sessionId;

    public WebServerOutputStream(OutputStream outputStream, String sessionId) {
        this.outputStream = outputStream;
        this.sessionId = sessionId;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }

    @Override
    public void write(int i) throws IOException {
        if (!isStatusCodeSet) {
            startGoodOutputStream();
            addHeader("Set-Cookie: SESSIONID=" + sessionId + "; Path=/");
        }

        if (!isHeaderAdded) {
            addHeader(contentType);

            endHeaders();
        }
        isHeaderAdded = true;

        outputStream.write(i);
    }

    public void startGoodOutputStream() {
        try {
            addHeader(GOOD_HEADER);
            isStatusCodeSet = true;
        } catch (IOException e) {
            log.error("Error while starting good output stream", e);
        }
    }

    public void setContentType(String s) {
        contentType = "Content-Type: " + s;
    }

    public void endHeaders() {
        try {
            outputStream.write(END_LINE.getBytes());
        } catch (IOException e) {
            log.error("Error ending headers part", e);
        }
    }

    public void addHeader(String header) throws IOException {
        outputStream.write((header + END_LINE).getBytes());
    }

    public void close() throws IOException {
        try (outputStream) {
            flush();
        }
    }

    public void flush() throws IOException {
        outputStream.flush();
    }

    public void sendRedirect(String s) {
        try {
            addHeader("HTTP/1.1 302 Found");
            addHeader("Location: " + s);
            addHeader("Set-Cookie: SESSIONID=" + sessionId + "; Path=/");
            isStatusCodeSet = true;
        } catch (IOException e) {
            log.error("Error while redirecting", e);
        }
    }

}
