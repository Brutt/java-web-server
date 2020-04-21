package petrovskyi.webserver.webapp.webxml.exception;

public class WebXmlNotFoundException extends RuntimeException {
    public WebXmlNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
