package petrovskyi.webserver.application.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ApplicationInfo {
    private String name;
    private Map<String, HttpServlet> urlToServlet;
    private Map<String, List<Filter>> urlToFilters;
    private URL[] urls;
}
