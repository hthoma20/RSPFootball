package action;

import game.player.Player;

public class GameAction {
    private Player player;

    public GameAction(Player player){
        this.player= player;
    }

    public Player getPlayer() {
        return player;
    }
}
