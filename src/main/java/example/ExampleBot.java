package example;

import eu.skylords.botapi.*;
import eu.skylords.botapi.Types.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** adapted from the C# example bot at <a href="https://gitlab.com/skylords-reborn/skylords-reborn-bot-api-c-sharp/-/blob/main/Example/Example.cs">gitlab.com/skylords-reborn/skylords-reborn-bot-api-c-sharp</a> */
public class ExampleBot implements Bot {

    private final String name = "JavaExampleBot";
    private final List<MapInfo> supportedMaps = List.of();
    private Deck[] decks = new Deck[]{};

    Deck selectedDeck;
    private byte myTeam;
    List<EntityId> oponents;
    Position2D myStartPosition;
    EntityId myId;

    public ExampleBot() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AiForMap sayHello(Types.ApiHello hello) {
        System.out.printf("Bot[%s]: Game sent a friendly 'Hello'%n", name);

        if (isMapSupported(hello.getMap())) {
            switch (hello.getMap().getMap()) {
                case Maps.LajeshSpectator -> decks = new Deck[]{TUTORIAL_DECK};
                case Maps.YrmiaSpectator, Maps.FyreSpectator -> decks = new Deck[]{TAINTED_FLORA, TUTORIAL_DECK};
                default -> decks = new Deck[]{TAINTED_FLORA};
            }
        }

        return new AiForMap(name, decks);
    }

    @Override
    public void prepareForBattle(Types.Prepare prepare) {
        System.out.printf("Bot[%s]: Preparing for battle...%n", name);
        selectedDeck = Arrays.stream(decks)
                .filter(d -> Objects.equals(d.getName(), prepare.getDeck()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Game selected unknown Deck " + prepare.getDeck()));
    }

    @Override
    public void matchStart(GameStartState state) {
        System.out.printf("Bot[%s]: The match started. I will give my best...%n", name);

        myId = state.getYourPlayerId();
        var entities = state.getEntities();

        System.out.printf("Bot[%s]: My player ID is: %d, I will play with deck: %s%n", name, myId.value(), selectedDeck.getName());


        Arrays.stream(state.getPlayers())
                .filter(p -> myId.equals(p.getEntity().getId()))
                .findFirst()
                .ifPresent(entity -> myTeam = entity.getEntity().getTeam());

        oponents = Arrays.stream(state.getPlayers())
                        .filter(p -> p.getEntity().getTeam() != myTeam)
                        .map(p -> p.getEntity().getId())
                        .collect(Collectors.toList());

        Arrays.stream(entities.getPowerSlots())
                .map(PowerSlot::getEntity)
                .filter(ps -> ps.getPlayerEntityId() != null && ps.getPlayerEntityId().equals(myId))
                .forEach(ps -> {
                    System.out.printf("Bot[%s]: I own a Power slot: %d at %s/%s%n", name, ps.getId().value(), ps.getPosition().getX(), ps.getPosition().getZ());
                    myStartPosition = Helpers.To2D(ps.getPosition());
                });

        Arrays.stream(entities.getTokenSlots())
                .map(TokenSlot::getEntity)
                .filter(ps -> ps.getPlayerEntityId() != null && ps.getPlayerEntityId().equals(myId))
                .forEach(ps -> System.out.printf("Bot[%s]: Power slot: %d at %s/%s%n", name, ps.getId().value(), ps.getPosition().getX(), ps.getPosition().getZ()));
    }

    @Override
    public List<Command> onTick(GameState state) {
        var currentTick = state.getCurrentTick();
        var entities = state.getEntities();

        var myArmy = Arrays.stream(entities.getSquads())
                .map(Squad::getEntity)
                .filter(e -> e.getPlayerEntityId().equals(myId))
                .map(Entity::getId)
                .collect(Collectors.toList());

        var target = Arrays.stream(entities.getTokenSlots())
                .map(TokenSlot::getEntity)
                .filter(e -> e.getPlayerEntityId() != null && oponents.contains(e.getPlayerEntityId()))
                .map(Entity::getId)
                .findAny()
                .orElse(new EntityId(0));

        var myPower = Arrays.stream(state.getPlayers())
                .filter(p -> p.getId().equals(myId))
                .map(PlayerEntity::getPower)
                .findFirst()
                .orElse(0f);


        System.out.printf("Bot[%s]: Tick: %d; target: %d; my power: %s; my army size: %d%n", name, currentTick.value(), target.value(), myPower, myArmy.size());

        var spawn = spawnUnit(myPower);
        var attack = attack(target, myArmy);
        List<Command> commands = new ArrayList<>();
        if (spawn != null) {
            commands.add(spawn);
        }
        if (attack != null) {
            commands.add(attack);
        }

        return commands;
    }

    @Override
    public void initialize() {
        System.out.printf("Bot[%s]: Hello. My name is %s and I'm a Skylords Reborn Ai Bot.%n", name, name);
        System.out.printf("Bot[%s]: I can play with the following decks: %s%n",
                name, Stream.of(TUTORIAL_DECK, TAINTED_FLORA).map(Deck::getName).collect(Collectors.toList()));
        if (this.supportedMaps == null || supportedMaps.isEmpty()) {
            System.out.printf("Bot[%s]: And I will play on any map%n", name);
        } else {
            System.out.printf("Bot[%s]: And I will play on the following maps: %s%n", name, this.supportedMaps.toString());
        }
    }

    // -----------------------------------------------------------------------------------

    private Command spawnUnit(float myPower) {

            if (selectedDeck == TUTORIAL_DECK) {
                if (myPower >= 50.0f) {
                    return new CommandProduceSquad((byte) 1, myStartPosition);
                } else {
                    return null;
                }
            } else if (selectedDeck == TAINTED_FLORA) {
                if (myPower >= 70.0f) {
                    return new CommandProduceSquad((byte) 2, myStartPosition);
                } else {
                    // Am I in trouble?
                    return null;
                }
            } else {
                throw new IllegalStateException("I do not play with any other deck");
            }
    }

    private Command attack(EntityId target, List<EntityId> squads) {
        if (target.value() != 0 && !squads.isEmpty()) {
            EntityId[] squadsArray = new EntityId[squads.size()];
            return new CommandGroupAttack(squads.toArray(squadsArray), target,false);
        } else {
            return null;
        }
    }

    /**
     * Checks if the provided map is supported by this bot.
     * <ul>
     *   <li>if no maps are present assume all maps are supported</li>
     *   <li>if provided MapInfo is in the list, map is supported</li>
     *   <li>if list contains community map and no specific CommunityMapDetails are set,
     * assume all maps of this type (1v1, 2v2, etc.) are supported.</li>
     * </ul>
     * @param mapInfo The map to check
     * @return true if the map is supported, false otherwise
     */
    private boolean isMapSupported(MapInfo mapInfo) {
        return Objects.isNull(supportedMaps) || supportedMaps.isEmpty() // if no maps are present assume all maps are supported
                || supportedMaps.stream().anyMatch(map -> map.equals(mapInfo) // if list contains equal mapinfo, map is supported
                || Objects.equals(map.getMap(), mapInfo.getMap()) && Objects.isNull(map.getCommunityMapDetails())); // if map is community map and no specific
    }

    // -----------------------------------------------------------------------------------
    // Decks:

    private static final Deck TUTORIAL_DECK = new Deck(
            "Tutorial",
            (byte) 3,
            new CardId[]{
                Helpers.Card(CardTemplate.MasterArchers, Upgrade.U3),
                Helpers.Card(CardTemplate.Northguards, Upgrade.U3),
                Helpers.Card(CardTemplate.Eruption, Upgrade.U3),
                Helpers.Card(CardTemplate.CannonTower, Upgrade.U3),
                Helpers.Card(CardTemplate.FireStalker, Upgrade.U3),
                Helpers.Card(CardTemplate.MagmaHurler, Upgrade.U3),
                Helpers.Card(CardTemplate.Tremor, Upgrade.U3),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard),
                Helpers.Card(CardTemplate.NotACard)
            }
    );

    private static final Deck TAINTED_FLORA = new Deck(
            "TaintedFlora",
            (byte) 0,
            new CardId[]{
                Helpers.Card(CardTemplate.Swiftclaw, Upgrade.U3),
                Helpers.Card(CardTemplate.DryadAFrost, Upgrade.U3),
                Helpers.Card(CardTemplate.Windweavers, Upgrade.U3),
                Helpers.Card(CardTemplate.Shaman, Upgrade.U3),
                Helpers.Card(CardTemplate.Spearmen, Upgrade.U3),
                Helpers.Card(CardTemplate.EnsnaringRoots, Upgrade.U3),
                Helpers.Card(CardTemplate.Hurricane, Upgrade.U3),
                Helpers.Card(CardTemplate.SurgeOfLight, Upgrade.U3),
                Helpers.Card(CardTemplate.NastySurprise, Upgrade.U3),
                Helpers.Card(CardTemplate.DarkelfAssassins, Upgrade.U3),
                Helpers.Card(CardTemplate.Nightcrawler, Upgrade.U3),
                Helpers.Card(CardTemplate.AmiiPaladins, Upgrade.U3),
                Helpers.Card(CardTemplate.AmiiPhantom, Upgrade.U3),
                Helpers.Card(CardTemplate.Burrower, Upgrade.U3),
                Helpers.Card(CardTemplate.ShadowPhoenix, Upgrade.U3),
                Helpers.Card(CardTemplate.AuraofCorruption, Upgrade.U3),
                Helpers.Card(CardTemplate.Tranquility, Upgrade.U3),
                Helpers.Card(CardTemplate.CurseofOink, Upgrade.U3),
                Helpers.Card(CardTemplate.CultistMaster, Upgrade.U3),
                Helpers.Card(CardTemplate.AshbonePyro, Upgrade.U3)
            }
    );
}
