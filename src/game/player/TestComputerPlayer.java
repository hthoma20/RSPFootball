package game.player;

import action.*;
import game.LocalGame;
import game.Play;
import game.RSP;

public class TestComputerPlayer extends ComputerPlayer {

    public TestComputerPlayer(LocalGame game, String name, int index) {
        super(game, name, index);
    }

    @Override
    protected RSP chooseRSP() {
        return RSP.ROCK;
    }

    @Override
    protected Play callPlay() {
        return Play.shortRun;
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
    protected int choosePuntDice(){
        return recommendedPuntDice();
    }
}
