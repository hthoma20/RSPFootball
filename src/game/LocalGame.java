package game;

import action.*;

import java.util.HashMap;

public class LocalGame {
    //a list of game pos where we need an rsp
    public static final GamePos[] rspPos= {GamePos.rsp, GamePos.shortRun, GamePos.longRunFumble, GamePos.shortPass};

    private GameState state;
    private Player[] players;

    public LocalGame(){
        this.state= new GameState();

        this.players= new Player[2];
        players[0]= new HumanPlayer(this, "Human", 0);
        players[1]= new ComputerPlayer(this, "Bot", 1);

        sendInfo(state);
    }

    /**
     * checks whether, given the position of the game,
     * this is a valid action to send
     * @param action the action that is being made
     * @return whether is is legal
     */
    private boolean isValidAction(GameAction action){
        if(action instanceof RSPAction){
            return isValidRSPAction((RSPAction)action);
        }
        if(action instanceof PlaycallAction){
            return isValidPlayCall((PlaycallAction)action);
        }
        if(action instanceof PATAction){
            return isValidPATAction((PATAction)action);
        }
        if(action instanceof RollAction){
            return isValidRollAction((RollAction)action);
        }
        if(action instanceof KickoffAction){
            return isValidKickoffAction((KickoffAction)action);
        }
        if(action instanceof KickReturnAction){
            return isValidKickReturnAction((KickReturnAction)action);
        }
        if(action instanceof DefenceAction){
            return isValidDefenceAction((DefenceAction)action);
        }
        if (action instanceof BombAction) {
            return isValidBombAction((BombAction)action);
        }

        return false;
    }

    private boolean isValidPlayCall(PlaycallAction action){
        //if we're not waiting for a playcall
        if(state.getGamePos() != GamePos.playCall &&
            state.getGamePos() != GamePos.twoPointConversion){
            return false;
        }

        //if it's not this player's turn
        int player= playerIndex(action.getPlayer());
        return player == state.getPossession();
    }

    private boolean isValidRSPAction(RSPAction action){
        int player= playerIndex(action.getPlayer());

        return state.waitingForRSP(player);
    }

    private boolean isValidPATAction(PATAction action){
        //if we're not waiting for a pat call
        if(state.getGamePos() != GamePos.touchdown){
            return false;
        }

        int player= playerIndex(action.getPlayer());
        return player == state.getPossession();
    }

    private boolean isValidRollAction(RollAction action){
        //check that the state is waiting for this player to roll the correct number of dice
        int waitingFor= state.waitingForRoll(playerIndex(action.getPlayer()));
        return waitingFor == action.getNumDice();
    }

    private boolean isValidKickoffAction(KickoffAction action){
        if(state.getGamePos() != GamePos.kickoff){
            return false;
        }

        //only the kicking team can choose to kick off
        return action.getPlayer() == players[state.getPossession()];
    }

    private boolean isValidKickReturnAction(KickReturnAction action){
        //the game must be waiting for a touchback desicion
        if(state.getGamePos() != GamePos.touchback){
            return false;
        }

        //only the returning team can make this call
        return action.getPlayer() == players[state.getPossession()];
    }

    private boolean isValidDefenceAction(DefenceAction action){
        //the game must be in defence choice pos
        if(state.getGamePos() != GamePos.defenceChoice){
            return false;
        }

        //only the defening player makes defence choices
        return action.getPlayer() != players[state.getPossession()];
    }

    private boolean isValidBombAction(BombAction action){
        //game pos must be bomb
        if(state.getGamePos() != GamePos.bomb){
            return false;
        }
        //only the offence may choose
        if(players[state.getPossession()] != action.getPlayer()){
            return false;
        }
        if(action.isDone()) {
            //you can only be done if the rolls are odd
            return state.sumDice() % 2 == 1;
        }
        //you can always roll
        return true;
    }

    /**
     * recieve an action from a player
     * @param action the action recieved
     */
    public void sendAction(GameAction action){
        if(!isValidAction(action)){
            System.out.println(action.getPlayer().getName() + " tried illegal action: " + action.getClass());
            return;
        }

        //if the action goes well, send new state
        if(receiveAction(action)){
            sendInfo(state.copy());
        }
    }

    /**
     * recieve and handle a game action
     * assume it is a valid action
     * @param action the action a player sent
     * @return whether the action  was successful
     */
    private boolean receiveAction(GameAction action){
        if(action instanceof RSPAction){
            return receiveRSP((RSPAction)action);
        }
        if(action instanceof PlaycallAction){
            return receivePlaycall((PlaycallAction)action);
        }
        if(action instanceof PATAction){
            state.extraPoint(((PATAction) action).getType());
            return true;
        }
        if(action instanceof RollAction){
            return receiveRoll(((RollAction)action).getNumDice());
        }
        if(action instanceof KickoffAction){
            state.kickoffType(((KickoffAction)action).getType());
            return true;
        }
        if(action instanceof KickReturnAction){
            receiveKickReturnType(((KickReturnAction) action).getType());
            return true;
        }
        if(action instanceof DefenceAction){
            receiveDefenceAction(((DefenceAction) action).getChoice());
            return true;
        }
        if(action instanceof BombAction){
            receiveBombAction(((BombAction) action).isDone());
            return true;
        }
        return false;
    }

    private boolean receiveRSP(RSPAction action){
        int player= playerIndex(action.getPlayer());

        state.rspThrown(player, action.getRSP());

        //if we are still waiting on the other player
        //do nothing else
        if(state.waitingForRSP((player+1)%2)){
            return true;
        }

        //otherwise, it's game time

        //get the winner of rsp
        int winner= state.rsp();

        switch(state.getGamePos()){
            case rsp:
                rspComplete(winner);
                break;
            case shortRun:
                //if the offense won the rsp
                if(winner == state.getPossession()){
                    state.runBall(5);
                }
                else{
                    state.advancePlay();
                }
                break;
            case longRunFumble:
                state.advancePlay();
                //if the defence won the rsp
                if(winner != state.getPossession()){
                    state.switchPossession();
                }
                break;
            case shortPass:
                //if the offense won the rsp
                if(winner == state.getPossession()){
                    state.runBall(10);
                }
                else{
                    state.advancePlay();
                }
                break;
        }

        return true;
    }

    /**
     * handles an rsp completion given that the
     * game position was waiting for rsp
     *
     * @param winner the index of the player who won the rsp, -1 if tie
     */
    private void rspComplete(int winner){
        //if rsp tied
        if(winner == -1){
            state.advancePlay();
            return;
        }

        //otherwise do the correct play
        Play currentPlay= state.getPlay();
        switch(currentPlay){
            case shortRun:
                state.shortRun(winner);
                break;
            case longRun:
                state.longRun(winner);
                break;
            case shortPass:
                state.shortPass(winner);
                break;
            case longPass:
                state.longPass(winner);
                break;
            case bomb:
                state.bomb(winner);
                break;
        }
    }

    private boolean receivePlaycall(PlaycallAction action){
        state.playCalled(action.getPlay());
        return true;
    }

    private boolean receiveRoll(int numDice){
        state.rollDice(numDice);
        int sum= state.sumDice();

        //whether this roll results in a touchdown
        boolean touchdown= false;

        switch(state.getGamePos()){
            case extraPoint:
                if(sum >= 4) {
                    //give the kicker a point
                    state.extraKick();
                }
                //now kickoff
                state.kickOff();
                break;

            case regularKick:
                state.ballKicked(sum*5);
                break;

            case onsideKick:
                //the kick was good if the roll was 5 or less
                state.onsideKick(sum <= 5);
                break;

            case defenceRoll:
                defenceRolled(sum);
                break;

            case kickReturn:
                state.kickReturned(sum*5);
                break;

            case longRun:
                touchdown= state.runBall(sum*5);
                if(sum == 1){
                    state.fumbleRun();
                }
                else{
                    //if we didn't score, worry about downs
                    state.advancePlay(!touchdown);
                }
                break;

            case longPass:
                touchdown= state.throwBall(sum*5 + 10);
                state.advancePlay(!touchdown);
                break;

            case interception:
                switch(state.getPlay()){
                    case longPass:
                        state.interception(sum*5 + 10);
                        break;
                    case bomb:
                        state.interception(sum*5);
                        break;
                }
        }

        return true;
    }

    /**
     * handle a roll from the defence
     * @param sum the sum of the dice that were rolled
     */
    private void defenceRolled(int sum){
        //whether this roll results in a safety
        boolean safety= false;
        switch(state.getPlay()){
            case shortRun:
                //if the defence rolled a 5 or 6, there is a sack
                if(sum >= 5){
                    safety= state.sackBall(5);
                }

                state.advancePlay(!safety);
                break;
            case longRun:
                int sackYards= 5;
                if(sum == 6){
                    sackYards= 10;
                }
                safety= state.sackBall(sackYards);
                state.advancePlay(!safety);
                break;
            case shortPass:
                if(sum == 6){
                    state.interception(10);
                }
                else{
                    state.advancePlay();
                }
                break;
            case longPass:
                if(sum >= 5){
                    state.interception();
                }
                else{
                    state.advancePlay();
                }
            case bomb:
                if(sum%2 == 0){
                    state.interception();
                }
                else{
                    state.advancePlay();
                }
        }
    }

    private void receiveKickReturnType(KickReturnAction.KickReturnType type){
        switch(type){
            case regular:
                state.regularReturn();
                break;
            case touchback:
                state.touchback();
                break;
        }
    }

    private void receiveDefenceAction(DefenceAction.Choice choice){
        switch(choice){
            case sack:
                int sackYards= 0;
                switch(state.getPlay()){
                    case shortPass:
                        sackYards= 5;
                        break;
                    case longPass:
                        sackYards= 10;
                        break;
                    case bomb:
                        sackYards= 15;
                        break;
                }

                boolean safety= state.sackBall(sackYards);
                state.advancePlay(!safety);
                break;

            case intercept:
                state.interceptAttempt();
                break;
        }
    }

    private void receiveBombAction(boolean isDone){
        if(isDone){
            state.bombThrown();
        }
        else{
            state.bombRoll();
            //check if that was last throw
            if(state.getRoll().length == 3){
                state.bombThrown();
            }
        }
    }

    /**
     * send the given game info to all players
     * @param info the info to send -- often the current game state
     */
    private void sendInfo(GameInfo info){
        for(Player player : players){
            player.sendInfo(info);
        }
    }

    /**
     *
     * @param player the player to locate
     * @return the index of this player
     */
    public int playerIndex(Player player){
        for(int i=0; i < players.length; i++){
            if(players[i] == player){
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args){
        new LocalGame();
    }
}
