package xyz.defe.cache.server;

public class ServerTest {

    public static void main(String[] args) {
        try {
            Server server = new Server(9120);
            new Thread(() -> server.start()).start();
//            Thread.sleep(3000);
//            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
