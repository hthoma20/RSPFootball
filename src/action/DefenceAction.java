package action;

import game.Player;

public class DefenceAction extends GameAction {
    private Choice choice;

    public DefenceAction(Player player, Choice choice) {
        super(player);
        this.choice = choice;
    }

    public Choice getChoice() {
        return choice;
    }

    public enum Choice{
        sack,
        intercept
    }
}
