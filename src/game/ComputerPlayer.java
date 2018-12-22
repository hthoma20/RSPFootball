package game;

import action.*;

public class ComputerPlayer extends Player{
    public ComputerPlayer(LocalGame game, String name, int index) {
        super(game, name, index);
    }

    @Override
    public void receiveInfo(GameInfo info) {
        if(info instanceof GameState){
            GameState state= (GameState)info;

            //check if we must rsp
            if(state.waitingForRSP(this.playerIndex)){
                this.sendGameAction(new RSPAction(this, chooseRSP()));
            }

            //check if we must roll
            int waitingForRoll= state.waitingForRoll(this.playerIndex);
            if(waitingForRoll != -1){
                this.sendGameAction(new RollAction(this, waitingForRoll));
            }

            //check if we must make some decision
            boolean isMyPlay= state.getPossession() == this.playerIndex;
            if(isMyPlay){
                switch(state.getGamePos()){
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
                }
            }
            else{
                switch(state.getGamePos()){
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
        return RSP.ROCK;
    }

    /**
     * decide what play call to make
     * @return the play we want to do
     */
    private Play callPlay(){
        return Play.longPass;
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
}
