package petrovskyi.webserver.application.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServlet;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ApplicationInfo {
    private String name;
    private Map<String, HttpServlet> urlToServlet;
}
