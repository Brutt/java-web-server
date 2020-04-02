package petrovskyi.webserver.webapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebXmlObjectDefinition {
    private String name;
    private String className;
}
