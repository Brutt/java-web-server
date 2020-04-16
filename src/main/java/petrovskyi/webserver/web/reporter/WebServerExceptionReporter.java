package petrovskyi.webserver.web.reporter;

import lombok.extern.slf4j.Slf4j;
import petrovskyi.webserver.web.stream.WebServerOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

@Slf4j
public class WebServerExceptionReporter {

    public static void reportException(Socket socket, Throwable throwable) {
        try (WebServerOutputStream webServerOutputStream = new WebServerOutputStream(socket.getOutputStream(), null)) {
            String errorHtml = "<h3>" + throwable +
                    "<p>at: " + Arrays.toString(throwable.getStackTrace()) + "</p></h3>";

            webServerOutputStream.setContentType("text/html; charset=utf-8");
            webServerOutputStream.setContentLength(errorHtml.length());
            webServerOutputStream.sendErrorMessage(errorHtml);
        } catch (IOException e) {
            log.error("Error while trying to send error report {}", throwable, e);
            throw new RuntimeException("Error while trying to send error report " + throwable, e);
        }
    }
}