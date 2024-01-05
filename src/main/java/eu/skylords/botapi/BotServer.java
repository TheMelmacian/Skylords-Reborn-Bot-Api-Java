package eu.skylords.botapi;

import eu.skylords.botapi.Bot;
import eu.skylords.botapi.Types.*;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Path("")
public class BotServer {

    /** The currently running server */
    private HttpServer server;

    /** The port on which the bot server runs and can be reached by the game */
    private final int port;

    /** The bot that is running on this server */
    private final Bot bot;
    /** Name of the bot */
    private final String name;

    /**
     * Create a new BotServer.
     * @param bot The API bot that should run on the server.
     * @param port The port the BotServer listens to requests from the game.
     */
    public BotServer(Bot bot, int port) {
        this.bot = bot;
        this.port = port;
        this.name = bot.getName();
    }

    /** Starts the server on the provided port */
    public void startServer() {
        String baseUri = "http://localhost:" + port + "/";
        System.out.println("Creating new Skylords Reborn Bot API Server...");
        try {
            final ResourceConfig rc = new ResourceConfig().register(this);
            server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), rc);

            System.out.printf("Skylords Reborn Bot API Server '%s' successfully started%n", name);
            System.out.printf("Listening on %s%n", baseUri);

            this.bot.initialize();

        } catch (Throwable t) {
            System.out.printf("Couldn't start Bot '%s' on %s; cause: %s%n", name, baseUri, t);
            t.printStackTrace();
        }
    }

    public void shutdown() {
        System.out.println("Shutting down...");
        server.shutdown();
        System.out.println("bye");
    }

    // ----------------------------------------------------------------------------------------------------------------
    // REST Endpoints:

    @POST
    @Path("/hello")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello(ApiHello hello) {
        if (ApiVersion.VERSION != hello.getVersion()) {
            System.out.printf("Incompatible API versions: Bot uses version %d; game requires version %d", ApiVersion.VERSION, hello.getVersion());
            return Response.status(422).build(); // 422 = Unprocessable Entity
        }
        AiForMap aiForMap = bot.sayHello(hello);
        return Response.ok(aiForMap).build();
    }

    @POST
    @Path("/prepare")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response prepare(Prepare prepare) {
        bot.prepareForBattle(prepare);
        return Response.ok().build();
    }

    @POST
    @Path("/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response start(GameStartState gameStartState) {
        bot.matchStart(gameStartState);
        return Response.ok().build();
    }

    @POST
    @Path("/tick")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<CommandHolder> tick(GameState gameState) {
        return bot.onTick(gameState)
                .stream()
                .map(CommandHolder::new)
                .collect(Collectors.toList());
    }

    public int getPort() {
        return port;
    }
    public Bot getBot() {
        return bot;
    }
}
