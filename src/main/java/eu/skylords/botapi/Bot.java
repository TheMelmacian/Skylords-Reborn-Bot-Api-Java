package eu.skylords.botapi;

import eu.skylords.botapi.Types.AiForMap;
import eu.skylords.botapi.Types.ApiHello;
import eu.skylords.botapi.Types.GameStartState;
import eu.skylords.botapi.Types.GameState;
import eu.skylords.botapi.Types.Prepare;
import eu.skylords.botapi.Types.Command;

import java.util.List;

public interface Bot {

    /** The name of the Bot. */
    String getName();

    /**
     * After the version check of the Bot API this method is called on the {@link BotServer#hello(ApiHello) hello endpoint}.
     * Used by the game to initialize the communication with the bot.
     * The bot responds with his name and the decks he is able to play on the selected map.
     * If the bot doesn't support the selected map he has to respond with an empty deck list.
     * @param hello The first request from the game to initialize the communication with the bot.
     * @return Response with the bots name and the decks he can play with.
     */
    AiForMap sayHello(ApiHello hello);

    /**
     * Called by the game before a match starts to inform the bot about the selected deck and the map,
     * so bot can prepare accordingly.
     * @param prepare Contains the selected deck and the map the match is played on.
     */
    void prepareForBattle(Prepare prepare);

    /**
     * Signals the start of the match with the initial state of the match.
     * @param gameStartState The initial state of the match.
     */
    void matchStart(GameStartState gameStartState);

    /**
     * Called on every tick with the current state of the match.
     * Respond with a list of commands the bot should perform.
     * @param tick The state of the current match on the actual tick.
     * @return A list of commands the bot should perform.
     */
    List<Command> onTick(GameState tick);

    /**
     * Called on {@link BotServer server} startup.
     * Can be used for any necessary initialization.
     */
    default void initialize() {
        // mo initialization necessary
    }
}
