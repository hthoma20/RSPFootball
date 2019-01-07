package action;

import game.player.Player;
import game.Play;

public class PlaycallAction extends GameAction{
    private Play play;

    public PlaycallAction(Player player, Play play){
        super(player);
        this.play= play;
    }

    public Play getPlay() {
        return play;
    }
}
