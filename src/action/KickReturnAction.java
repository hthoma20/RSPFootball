package action;

import game.player.Player;

public class KickReturnAction extends GameAction {
    private KickReturnType type;

    public KickReturnAction(Player player, KickReturnType type) {
        super(player);
        this.type = type;
    }

    public KickReturnType getType() {
        return type;
    }

    public enum KickReturnType{
        touchback,
        regular
    }
}
