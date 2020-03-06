package petrovskyi.webserver.web.stream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;

public class WebServerOutputStream extends ServletOutputStream {
    private static final String END_LINE = "\r\n";
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
        outputStream.write(("HTTP/1.1 200 OK" + END_LINE + END_LINE).getBytes());
        //outputStream.flush();
    }

}
