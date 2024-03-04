package example;

import eu.skylords.botapi.Bot;
import eu.skylords.botapi.BotServer;

public class Main {
    public static void main(String[] args) {
        Bot bot = new ExampleBot();
        BotServer server = new BotServer(bot, 6565);
        server.startServer();

        // shutdown hook to correctly shutdown the server if process is terminated
        Thread shutdownHook = new Thread(server::shutdown);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
}