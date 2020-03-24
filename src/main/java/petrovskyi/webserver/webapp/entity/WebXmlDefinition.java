package petrovskyi.webserver.webapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class WebXmlDefinition {
    private Map<String, String> urlToClassName;
}