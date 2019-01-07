package action;

import game.player.Player;

public class KickoffAction extends GameAction {
    private KickoffType type;

    public KickoffAction(Player player, KickoffType type) {
        super(player);
        this.type = type;
    }

    public KickoffType getType() {
        return type;
    }

    public enum KickoffType{
        regular,
        onside
    }
}
