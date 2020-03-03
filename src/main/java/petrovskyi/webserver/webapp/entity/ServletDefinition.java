package petrovskyi.webserver.webapp.entity;

import lombok.Data;

import java.util.List;

@Data
public class ServletDefinition {
    String name;
    List<String> urls;
    String className;
}
