package action;

import game.player.Player;

public class PATAction extends GameAction {
    private PATType type;

    public PATAction(Player player, PATType type) {
        super(player);
        this.type = type;
    }

    public PATType getType() {
        return type;
    }

    public enum PATType{
        extraKick,
        twoPointConversion
    }
}
