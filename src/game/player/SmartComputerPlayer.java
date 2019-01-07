package game.player;

import action.*;
import game.LocalGame;
import game.Play;
import game.RSP;

public class SmartComputerPlayer extends ComputerPlayer {
    public SmartComputerPlayer(LocalGame game, String name, int index) {
        super(game, name, index);
    }

    @Override
    protected RSP chooseRSP() {
        //choose a random rsp
        return randChoice(RSP.values());
    }

    @Override
    protected Play callPlay() {
        return null;
    }

    @Override
    protected PATAction.PATType choosePAT() {
        return PATAction.PATType.extraKick;
    }

    @Override
    protected KickoffAction.KickoffType chooseKickoff() {
        return KickoffAction.KickoffType.regular;
    }

    @Override
    protected KickReturnAction.KickReturnType chooseTouchback() {
        return KickReturnAction.KickReturnType.touchback;
    }

    @Override
    protected DefenceAction.Choice chooseSack() {
        return DefenceAction.Choice.sack;
    }

    @Override
    protected boolean chooseBombDone() {
        return true;
    }

    @Override
    protected FakeAction.KickType chooseFake() {
        return FakeAction.KickType.normal;
    }

    @Override
    protected int choosePuntDice() {
        return recommendedPuntDice();
    }
}
