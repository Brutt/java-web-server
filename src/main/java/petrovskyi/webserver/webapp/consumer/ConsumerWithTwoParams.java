package petrovskyi.webserver.webapp.consumer;

@FunctionalInterface
public interface ConsumerWithTwoParams<One, Two> {
    void accept(One one, Two two);
}
