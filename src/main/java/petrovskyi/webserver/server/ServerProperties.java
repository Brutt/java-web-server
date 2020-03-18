package petrovskyi.webserver.server;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Setter
@Getter
public class ServerProperties {
    HashMap<String, String> properties;
}
