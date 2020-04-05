package petrovskyi.webserver.web.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import petrovskyi.webserver.web.http.HttpStatusCode;
import petrovskyi.webserver.web.http.session.WebServerSession;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;


@Slf4j
public class WebServerOutputStream extends ServletOutputStream {
    //
    // https://tools.ietf.org/html/rfc2616#section-2.2
    // HTTP/1.1 defines the sequence CR LF as the end-of-line marker for all protocol elements except the entity-body
    //
    private static final String END_LINE = "\r\n";
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private final OutputStream outputStream;
    private String contentType;
    private boolean isAllHeadersAdded = false;
    private boolean isStatusCodeSet = false;
    private HttpSession httpSession;
    private String appName;

    public volatile boolean onlyHeaders = false;

    public WebServerOutputStream(OutputStream outputStream, HttpSession httpSession) {
        this.outputStream = outputStream;
        this.httpSession = httpSession;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        throw new UnsupportedOperationException("This method is unsupported yet");
    }

    @Override
    public void write(int i) throws IOException {
        if (!isStatusCodeSet) {
            startSuccessfulResponse();
        }

        if (!isAllHeadersAdded) {
            addHeader(contentType);
            endHeaders();
        }

        outputStream.write(i);
    }

    private void startSuccessfulResponse() {
        log.debug("Start successful response output");
        setMainHeader(HttpStatusCode.OK);
        setCookie(WebServerSession.SESSIONID, httpSession.getId(), "/");
    }

    public void setContentType(String s) {
        contentType = "Content-Type: " + s;
    }

    private void endHeaders() {
        log.debug("Set end of headers block");
        try {
            outputStream.write(END_LINE.getBytes());
        } catch (IOException e) {
            log.error("Error setting end of headers block", e);
        }
        isAllHeadersAdded = true;
    }

    public void addHeader(String header) throws IOException {
        if (header == null) {
            return;
        }
        log.debug("Adding header {}", header);
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
        log.debug("Redirect to {}", s);
        try {
            setMainHeader(HttpStatusCode.REDIRECT_FOUND);
            addHeader("Location: " + s);
            setCookie(WebServerSession.SESSIONID, httpSession.getId(), "/");
            endHeaders();
            flush();
            onlyHeaders = true;
        } catch (IOException e) {
            log.error("Error while redirecting to {}", s, e);
        }
    }

    private void setCookie(String key, String value, String path) {
        String cookieName = key;
        try {
            if (WebServerSession.SESSIONID.equals(key)) {
                String suffix = DigestUtils.sha1Hex(appName);
                cookieName = key + "." + suffix;
            }
            addHeader("Set-Cookie: " + cookieName + "=" + value + "; Path=" + path);
        } catch (IOException e) {
            log.error("Error setting cookie {}={}, path={}", cookieName, value, path, e);
        }
    }

    private void setMainHeader(HttpStatusCode statusCode) {
        try {
            addHeader(HTTP_VERSION + statusCode.getFullName());
        } catch (IOException e) {
            log.error("Error setting main header with status code {}", statusCode, e);
        }
        isStatusCodeSet = true;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

}
