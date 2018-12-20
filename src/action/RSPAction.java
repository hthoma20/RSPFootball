package action;

import game.Player;
import game.RSP;

public class RSPAction extends GameAction{
    private RSP rsp;

    public RSPAction(Player player, RSP rsp) {
        super(player);
        this.rsp = rsp;
    }

    public RSP getRSP() {
        return rsp;
    }
}
