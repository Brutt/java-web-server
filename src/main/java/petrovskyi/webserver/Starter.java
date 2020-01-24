package petrovskyi.webserver;

import petrovskyi.webserver.server.Server;
import sun.misc.Signal;

class Starter {
    public static void main(String[] args) {
        Server server = new Server();

        Signal.handle(new Signal("TERM"), signal -> {
            System.out.println(signal.getName() + " (" + signal.getNumber() + ")");
            try {
                server.stop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
        });

        server.setPort(3000);
        server.start();
    }
}