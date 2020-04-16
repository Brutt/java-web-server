package petrovskyi.webserver.server.factory;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;

@Slf4j
public class WebServerThreadFactory implements ThreadFactory {
    private final Thread.UncaughtExceptionHandler handler;
    private int counter = 0;
    private String prefix;
    private boolean isDaemon;

    public WebServerThreadFactory(Thread.UncaughtExceptionHandler handler, String prefix, boolean isDaemon) {
        this.handler = handler;
        this.prefix = prefix;
        this.isDaemon = isDaemon;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        counter++;
        String name = prefix + "-" + counter;

        Thread thread = new Thread(runnable, name);
        thread.setDaemon(isDaemon);
        thread.setUncaughtExceptionHandler(handler);

        log.trace("Thread {} was created", thread);

        return thread;
    }
}