package petrovskyi.webserver.webapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class WebXmlDefinition {
    private Map<String, List<String>> urlToServletClassName;
    private Map<String, List<String>> urlToFiltersClassName;
}