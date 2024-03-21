package eu.skylords.botapi;

import eu.skylords.botapi.Types.*;

public class Helpers {
    private Helpers() {
        // prevent class initialization
    }

    public static CardId Card(CardTemplate ct, Upgrade u) {
        return new CardId(ct.getId() + u.getValue());
    }

    public static CardId Card(CardTemplate ct) {
        return new CardId(ct.getId());
    }

    public static Position2D To2D(Position position) {
        return new Position2D(position.getX(), position.getZ());
    }
}
