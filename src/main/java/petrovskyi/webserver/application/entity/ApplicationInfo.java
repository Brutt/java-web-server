package petrovskyi.webserver.application.entity;

import lombok.Data;

import javax.servlet.http.HttpServlet;
import java.util.Map;

@Data
public class ApplicationInfo {
    private Map<String, HttpServlet> urlToServlet;
}
