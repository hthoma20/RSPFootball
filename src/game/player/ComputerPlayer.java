package game.player;

import action.*;
import game.*;

import java.util.HashMap;

public abstract class ComputerPlayer extends Player{
    protected GameState currState;

    //the expected yardage of a play, assuming the rsp is won
    private HashMap<Play, Double> expectedGain;

    //the expected loss of a play, assuming the rsp is lost and the
    //defence goes for a sack
    private HashMap<Play, Double> expectedLoss;

    public ComputerPlayer(LocalGame game, String name, int index) {
        super(game, name, index);

        findExpectations();
    }

    @Override
    public void receiveInfo(GameInfo info) {
        if(info instanceof GameState){
            currState= (GameState)info;

            //check if we must rsp
            if(currState.waitingForRSP(this.playerIndex)){
                this.sendGameAction(new RSPAction(this, chooseRSP()));
            }

            //check if we must roll
            int waitingForRoll= currState.waitingForRoll(this.playerIndex);
            if(waitingForRoll != -1){
                this.sendGameAction(new RollAction(this, waitingForRoll));
            }

            //check if we must make some decision
            boolean isMyPlay= currState.getPossession() == this.playerIndex;
            if(isMyPlay){
                switch(currState.getGamePos()){
                    case playCall:
                        this.sendGameAction(new PlaycallAction(this, callPlay()));
                        break;
                    case touchdown:
                        this.sendGameAction(new PATAction(this, choosePAT()));
                        break;
                    case kickoff:
                        this.sendGameAction(new KickoffAction(this, chooseKickoff()));
                        break;
                    case touchback:
                        this.sendGameAction(new KickReturnAction(this, chooseTouchback()));
                        break;
                    case bomb:
                        this.sendGameAction(new BombAction(this, chooseBombDone()));
                        break;
                    case fakeChoice:
                        this.sendGameAction(new FakeAction(this, chooseFake()));
                        break;
                    case punt:
                        this.sendGameAction(new RollAction(this, choosePuntDice()));
                        break;
                }
            }
            else{
                switch(currState.getGamePos()){
                    case defenceChoice:
                        this.sendGameAction(new DefenceAction(this, chooseSack()));
                }
            }
        }
    }

    /**
     * decide what to throw for rsp
     * @return rock, scissors, or paper
     */
    protected abstract RSP chooseRSP();

    /**
     * decide what play call to make
     * @return the play we want to do
     */
    protected abstract Play callPlay();

    /**
     * decide whether to kick an extra point or 2 point play
     * @return extraKick or twoPointConversion
     */
    protected abstract PATAction.PATType choosePAT();

    /**
     * decide what kind of kickoff to kick
     * @return regular kickoff or onside kick
     */
    protected abstract KickoffAction.KickoffType chooseKickoff();

    /**
     * decide whether to run the ball out of the endzone, or touchback the ball
     * @return regular return or touchback
     */
    protected abstract KickReturnAction.KickReturnType chooseTouchback();

    /**
     * decide whether to sack the offence or to go for an interception
     * @return sack or interception
     */
    protected abstract DefenceAction.Choice chooseSack();

    /**
     * decide wether to stop rolling in the bomb phase
     * @return true if we want to hold our cards
     */
    protected abstract boolean chooseBombDone();

    /**
     * decide to fake kick or real kick the ball
     * @return normal or fake
     */
    protected abstract FakeAction.KickType chooseFake();

    /**
     * decide how many dice to roll for the punt
     * @return 1,2, or 3
     */
    protected abstract int choosePuntDice();

    public int recommendedPuntDice(){
        //how many yards from the 95 yardline?
        int distance= 95 - currState.getBallPos();

        //expect each die to be a 3.5*5 = 17.5
        int numDice= (int)((float)distance/17.5);

        return Math.min(numDice, 3);
    }

    public <T> T randChoice(T[] values){
        int choice= (int)(Math.random()*values.length);
        return values[choice];
    }

    @Override
    public void sendGameAction(GameAction action){
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.sendGameAction(action);
    }

    /**
     * setup the expected gain and loss maps
     */
    private void findExpectations(){
        expectedGain= new HashMap<>();
        expectedGain.put(Play.shortRun, 7.5);
        expectedGain.put(Play.longRun, 0.0);
        expectedGain.put(Play.shortPass, 0.0);
        expectedGain.put(Play.longPass, 0.0);
    }

    public double getExpectedGain(Play play){
        Double gain= expectedGain.get(play);
        if(gain == null){
            return -1;
        }
        return gain;
    }
}
