package game;

import action.*;

public class ComputerPlayer extends Player{
    private GameState currState;
    public ComputerPlayer(LocalGame game, String name, int index) {
        super(game, name, index);
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
                        this.sendGameAction(new BombAction(this, chooseBomb()));
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
    private RSP chooseRSP(){
        //return a random choice
        return randChoice(RSP.values());
        //return RSP.ROCK;
    }

    /**
     * decide what play call to make
     * @return the play we want to do
     */
    private Play callPlay(){
        //return a random play
        return randChoice(Play.values());
        //return Play.values()[0];
    }

    /**
     * decide whether to kick an extra point or 2 point play
     * @return extraKick or twoPointConversion
     */
    private PATAction.PATType choosePAT(){
        return PATAction.PATType.extraKick;
    }

    /**
     * decide what kind of kickoff to kick
     * @return regular kickoff or onside kick
     */
    private KickoffAction.KickoffType chooseKickoff(){
        return KickoffAction.KickoffType.regular;
    }

    /**
     * decide whether to run the ball out of the endzone, or touchback the ball
     * @return regular return or touchback
     */
    private KickReturnAction.KickReturnType chooseTouchback(){
        return KickReturnAction.KickReturnType.touchback;
    }

    /**
     * decide whether to sack the offence or to go for an interception
     * @return sack or interception
     */
    private DefenceAction.Choice chooseSack(){

        return DefenceAction.Choice.sack;
    }

    /**
     * decide wether to stop rolling in the bomb phase
     * @return true if we want to hold our cards
     */
    private boolean chooseBomb(){
        //if we're able to stop, stop
        if(currState.sumDice()%2 == 1){
            return true;
        }

        return false;
    }

    private <T> T randChoice(T[] values){
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
}
