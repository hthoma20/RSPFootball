package game;

import action.*;

import java.util.HashMap;

public class LocalGame {
    //a list of game pos where we need an rsp
    public static final GamePos[] rspPos= {GamePos.rsp, GamePos.shortRun, GamePos.longRunFumble};

    private GameState state;
    private Player[] players;

    //a map from a given state to the roll specifications that should be true
    //for a valid action
    private HashMap<GamePos, RollRule> rollRules;

    public LocalGame(){
        this.state= new GameState();

        this.players= new Player[2];
        players[0]= new HumanPlayer(this, "Human", 0);
        players[1]= new ComputerPlayer(this, "Bot", 1);

        setupRollRules();

        sendInfo(state);
    }

    private void setupRollRules(){
        this.rollRules= new HashMap<>();

        //if it's an extra point, the offence must have rolled 2 dice
        rollRules.put(GamePos.extraPoint, new RollRule(true, 2));
        //if it's a regular kick, the offence must have rolled 3 dice
        rollRules.put(GamePos.regularKick, new RollRule(true, 3));
        rollRules.put(GamePos.onsideKick, new RollRule(true, 2));
        //if it's a defence roll, the defecne must have rolled 1 die
        rollRules.put(GamePos.defenceRoll, new RollRule(false, 1));
        rollRules.put(GamePos.kickReturn, new RollRule(true, 1));
        rollRules.put(GamePos.longRun, new RollRule(true, 1));
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
        GamePos gamePos= state.getGamePos();

        //whether the offensive team is sending this action
        boolean offenceRolled= action.getPlayer() == players[state.getPossession()];

        RollRule rule= rollRules.get(gamePos);

        if(rule == null){
            return false;
        }

        return rule.isValid(offenceRolled, action.getNumDice());
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
        }
    }

    private boolean receivePlaycall(PlaycallAction action){
        state.playCalled(action.getPlay());
        return true;
    }

    private boolean receiveRoll(int numDice){
        state.rollDice(numDice);
        int sum= state.sumDice();

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
                state.runBall(sum*5);
                if(sum == 1){
                    state.fumbleRun();
                }
                else{
                    state.advancePlay();
                }
        }

        return true;
    }

    /**
     * handle a roll from the defence
     * @param sum the sum of the dice that were rolled
     */
    private void defenceRolled(int sum){
        switch(state.getPlay()){
            case shortRun:
                //if the defence rolled a 5 or 6, there is a sack
                if(sum >= 5){
                    state.sackBall(5);
                }
                state.advancePlay();
                break;
            case longRun:
                if(sum == 6){
                    state.sackBall(10);
                }
                else{
                    state.sackBall(5);
                }
                state.advancePlay();
                break;
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

    /**
     * send the given game info to all players
     * @param info the info to send -- often the current game state
     */
    private void sendInfo(GameInfo info){
        for(Player player : players){
            player.receiveInfo(info);
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

    /**
     * a rule that represents whether the player must be on offence
     * and how many dice they should have rolled
     * for this to be a valid roll action
     */
    private class RollRule{
        boolean offence;
        int numDice;

        public RollRule(boolean offence, int numDice) {
            this.offence = offence;
            this.numDice = numDice;
        }

        /**
         * checks whether the rule is followed given the parameters
         * @param offenceRolled whether the offence rolled the dice
         * @param diceRolled how many dice were rolled
         * @return whether the rule is followed
         */
        public boolean isValid(boolean offenceRolled, int diceRolled){
            return offenceRolled == this.offence && diceRolled == this.numDice;
        }
    }
}
