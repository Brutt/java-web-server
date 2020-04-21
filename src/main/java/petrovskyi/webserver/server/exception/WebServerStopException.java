package petrovskyi.webserver.server.exception;

public class WebServerStopException extends RuntimeException {
    public WebServerStopException(String message, Throwable cause) {
        super(message, cause);
    }
}
