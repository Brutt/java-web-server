package petrovskyi.web.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;

public class AssetsServlet extends HttpServlet {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        LOG.info("doGet()");
        int startPos = request.getRequestURI().indexOf("/assets/");
        String requestResource = request.getRequestURI().substring(startPos + "/assets/".length());

        LOG.info("doGet() | request: {}", requestResource);

        try (BufferedInputStream bufferedInputStream =
                     new BufferedInputStream(getClass().getClassLoader()
                             .getResourceAsStream(requestResource))) {
            ServletOutputStream outputStream = response.getOutputStream();

            byte[] buf = new byte[2048];
            int n;
            while ((n = bufferedInputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, n);
            }

        } catch (Exception e) {
            LOG.error("Exception {} while were trying to get resource by URI {}", e, requestResource);
            throw new RuntimeException("Exception while were trying to get resource by URI:" + requestResource, e);
        }
    }
}