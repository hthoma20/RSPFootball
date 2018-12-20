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

            //check for things we have to do
            if(state.waitingForRSP(this.playerIndex)){
                game.sendAction(new RSPAction(this, chooseRSP()));
            }

            boolean isMyPlay= state.getPossession() == this.playerIndex;
            if(isMyPlay){
                switch(state.getGamePos()){
                    case playCall:
                        game.sendAction(new PlaycallAction(this, callPlay()));
                        break;
                    case touchdown:
                        game.sendAction(new PATAction(this, choosePAT()));
                        break;
                    case kickoff:
                        game.sendAction(new KickoffAction(this, chooseKickoff()));
                        break;
                    case touchback:
                        game.sendAction(new KickReturnAction(this, chooseTouchback()));
                        break;
                    case regularKick:
                        game.sendAction(new RollAction(this, 3));
                }
            }
            if(!isMyPlay){
                if(state.getGamePos() == GamePos.defenceRoll){
                    game.sendAction(new RollAction(this, 1));
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
        return Play.shortRun;
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
}
