package petrovskyi.webserver.webapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StartupArchiveAndFolder {
    private List<String> archives;
    private List<String> folders;
}
