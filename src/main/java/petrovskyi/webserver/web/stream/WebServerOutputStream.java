package petrovskyi.webserver.web.stream;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class WebServerOutputStream extends ServletOutputStream {
    private final String END_LINE = "\r\n";
    private final String GOOD_HEADER = "HTTP/1.1 200 OK";
    private OutputStream outputStream;

    public WebServerOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
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
        outputStream.write(i);
    }

    public void startGoodOutputStream() {
        try {
            addHeader(GOOD_HEADER + END_LINE + END_LINE);
        } catch (IOException e) {
            log.error("Error while starting good output stream", e);
        }
    }

    public void addHeader(String header) throws IOException {
        outputStream.write(header.getBytes());
    }

    public void close() throws IOException {
        outputStream.close();
    }

    public void flush() throws IOException {
        outputStream.flush();
    }

}
