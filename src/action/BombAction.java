package action;

import game.Player;

public class BombAction extends GameAction {
    private boolean done;
    public BombAction(Player player, boolean done) {
        super(player);
        this.done= done;
    }

    public boolean isDone() {
        return done;
    }
}
