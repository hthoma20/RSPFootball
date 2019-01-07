package action;

import game.player.Player;

public class FakeAction extends GameAction {
    private KickType type;

    public FakeAction(Player player, KickType type) {
        super(player);
        this.type = type;
    }

    public KickType getType() {
        return type;
    }

    public enum KickType{
        normal,
        fake
    }
}
