package action;

import game.player.Player;

public class RollAction extends GameAction{
    private int numDice;

    public RollAction(Player player, int numDice) {
        super(player);
        this.numDice = numDice;
    }

    public int getNumDice() {
        return numDice;
    }
}
