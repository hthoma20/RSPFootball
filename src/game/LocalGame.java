package game;

import action.*;
import game.player.HumanPlayer;
import game.player.Player;
import game.player.TestComputerPlayer;

import java.util.HashMap;

import static game.GameState.TIE;
import static game.Play.*;

public class LocalGame {
    //a list of game pos where we need an rsp
    public static final GamePos[] rspPos= {GamePos.rsp, GamePos.shortRun, GamePos.longRunFumble, GamePos.shortPass};

    private GameState state;
    private Player[] players;

    //a map from plays to their readable names
    private HashMap<Play, String> playStrings;

    public LocalGame(){
        String name0= "Human";
        String name1= "Bot";

        this.state= new GameState(0);

        this.players= new Player[2];
        players[0]= new HumanPlayer(this, name0, 0);
        players[1]= new TestComputerPlayer(this, name1, 1);

        setupPlayStrings();

        sendInfo(state);
        sendMessage("Match begins!");
        sendMessage(name0 + " will kick to " + name1);
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
        if(action instanceof FakeAction){
            return isValidFakeAction(((FakeAction) action));
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
        int player= playerIndex(action.getPlayer());
        //whether the acting player is on offence
        boolean onOffence= player == state.getPossession();
        int numDice= action.getNumDice();
        GamePos pos= state.getGamePos();

        //if were punting, the offence may roll 1,2,3 dice
        if(pos == GamePos.punt){
            if(!onOffence){
                return false;
            }
            if(1 > numDice || numDice > 3){
                return false;
            }
            return true;
        }

        //check that the state is waiting for this player to roll the correct number of dice
        int waitingFor= state.waitingForRoll(player);
        return waitingFor == numDice;
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

    private boolean isValidFakeAction(FakeAction action){
        //game must  be in fakeChoice position
        if(state.getGamePos() != GamePos.fakeChoice){
            return false;
        }

        //must be offence
        return players[state.getPossession()] == action.getPlayer();
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
            int winner= state.getWinner();
            if(winner != GameState.NONE){
                String winnerString= winner == TIE ? "TIE GAME" : players[winner].getName() + "WINS";
                sendInfo(new GameOverInfo(winnerString));
            }
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
            return receiveRoll((RollAction)action);
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
        if(action instanceof FakeAction){
            receiveFakeAction(((FakeAction) action).getType());
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
                    sendMessage("<O> win the RSP for another 5 yards");
                }
                else{
                    state.advancePlay();
                    sendMessage("<D> win the RSP to stop <O> at the " + ballYardLine() + " yard line");
                }
                break;
            case longRunFumble:
                state.advancePlay();
                //if the defence won the rsp
                if(winner != state.getPossession()){
                    state.switchPossession();
                }
                sendMessage("<O> win the RSP and recover the fumble");
                break;
            case shortPass:
                //if the offense won the rsp
                if(winner == state.getPossession()){
                    state.runBall(10);
                    sendMessage("<O> win the RSP to run another 10 yards");
                }
                else{
                    state.advancePlay();
                    sendMessage("<D> win the RSP to stop <O> at the " + ballYardLine() + " yard line");
                }
                break;
            case twoPointConversion:
                state.twoPointConversion(winner);
                if(winner == state.getPossession()){
                    sendMessage("<O> make the 2-point conversion");
                }
                else{
                    sendMessage("<D> stop the 2-point conversion");
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
        if(winner == -1){
            sendMessage("RSP tied");
        }
        else {
            sendMessage(players[winner].getName() + " win the RSP");
        }

        Play currentPlay= state.getPlay();

        //on punt and field goal, tie matters
        if(currentPlay == punt){
            state.punt(winner);
            return;
        }
        if(currentPlay == fieldGoal){
            state.fieldGoal(winner);
            return;
        }
        //on all others, tie results in advance down
        else if(winner == -1){
            state.advancePlay();
            return;
        }

        //otherwise do the correct play
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

        sendMessage("<O> called a " + playStrings.get(action.getPlay()));

        return true;
    }

    private boolean receiveRoll(RollAction action){
        int numDice= action.getNumDice();

        state.rollDice(numDice);
        int sum= state.sumDice();

        sendMessage(action.getPlayer().getName() + " rolled " + state.rollString());

        //whether this roll results in a touchdown
        boolean touchdown= false;

        switch(state.getGamePos()){
            case extraPoint:
                if(sum >= 4) {
                    //give the kicker a point
                    state.extraKick();
                    sendMessage("The extra point is good");
                }
                //now kickoff
                state.kickOff();
                break;

            case regularKick:
                state.ballKicked(sum*5);
                sendMessage("The kick off goes " + (sum*5) + " yards");
                break;

            case onsideKick:
                //the kick was good if the roll was 5 or less
                state.onsideKick(sum <= 5);
                sendMessage("<O> recover the onside at the 45");
                break;

            case defenceRoll:
                defenceRolled(sum);
                break;

            case kickReturn:
                state.kickReturned(sum*5);
                sendMessage("The kick is returned for " + (sum*5) + " yards");
                break;

            case longRun:
                touchdown= state.runBall(sum*5);
                sendMessage("<O> run for " + (sum*5) + " yards");
                if(sum == 1){
                    state.fumbleRun();
                    sendMessage("The ball is fumbled");
                }
                else{
                    //if we didn't score, worry about downs
                    state.advancePlay(!touchdown);
                }
                break;

            case longPass:
                touchdown= state.throwBall(sum*5 + 10);
                sendMessage("<O> throw for " + (sum*5 + 10) + " yards");
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
                if(state.getGamePos() == GamePos.playCall) {
                    sendMessage("The throw goes out of bounds");
                }
                else{
                    sendMessage("The pass is intercepted at the " + ballYardLine() + " yard line");
                }
                break;
            case punt:
                state.advancePlay(false);
                state.ballKicked(sum*5);
                    if(state.getGamePos() == GamePos.playCall){
                        sendMessage("Coffin corner!");
                    }
                    else{
                        sendMessage("Ball kicked to the " + ballYardLine() + " yard line");
                    }
                break;
            case fieldGoal:
                state.advancePlay(false);
                state.fieldGoalKicked(sum);
                if(state.getGamePos() == GamePos.kickoff){
                    sendMessage("The kick is good");
                }
                else{
                    sendMessage("<D> miss the field goal");
                }
                break;
            case fakeKick:
                int playYards= sum*5 - 10;
                //whether this play resulted in points
                boolean score= false;
                if(playYards < 0){
                    score= state.sackBall(-playYards);
                    sendMessage("<D> stop the fake kick");
                }
                else{
                    score= state.runBall(playYards);
                    sendMessage("<O> make a gain with the fake play");
                }
                state.advancePlay(!score);
                break;
        }

        if(touchdown){
            sendMessage("TOUCHDOWN <O>!");
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
                    sendMessage("<O> takes a five yard loss");
                    safety= state.sackBall(5);
                }

                state.advancePlay(!safety);
                break;
            case longRun:
                int sackYards= 5;
                if(sum == 6){
                    sackYards= 10;
                }
                sendMessage("<O> takes a " + sackYards + " loss");
                safety= state.sackBall(sackYards);
                state.advancePlay(!safety);
                break;
            case shortPass:
                if(sum == 6){
                    state.interception(10);
                    sendMessage("It looks like <D> might intercept");
                }
                else{
                    sendMessage("Pass incomplete");
                    state.advancePlay();
                }
                break;
            case longPass:
                if(sum >= 5){
                    sendMessage("It looks like <D> might intercept");
                    state.interception();
                }
                else{
                    sendMessage("Pass incomplete");
                    state.advancePlay();
                }
                break;
            case bomb:
                if(sum%2 == 0){
                    sendMessage("It looks like <D> might intercept");
                    state.interception();
                }
                else{
                    state.advancePlay();
                }
            case punt:
            case fieldGoal:
                if(sum == 1){
                    sendMessage("The kick is blocked");
                    state.kickBlocked();
                }
                else{
                    state.puntRoll();
                }
                break;
        }

        if(safety){
            sendMessage("<D> get a safety");
        }
    }

    private void receiveKickReturnType(KickReturnAction.KickReturnType type){
        switch(type){
            case regular:
                state.regularReturn();
                break;
            case touchback:
                sendMessage("<O> take a touch back");
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
                String message= "<D> make a " + sackYards + " yard sack";
                if(safety){
                    message+= " for a safety";
                }
                sendMessage(message);
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
            sendMessage("The roll is " + state.rollString());
            //check if that was last throw
            if(state.getRoll().length == 3){
                state.bombThrown();
            }
        }
    }

    private void receiveFakeAction(FakeAction.KickType type){
        switch(type){
            case normal:
                switch(state.getPlay()){
                    case punt:
                        state.puntRoll();
                        break;
                    case fieldGoal:
                        state.fieldGoalRoll();
                        break;
                }
                break;
            case fake:
                state.fakeKick();
                sendMessage("It looks like <O> is faking the kick");
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
     * convienience method to contruct and send a message info
     * with the given message
     * use a formatted string, all occurances of <O> in the message
     * will be replaced with the offence's name
     * all occurances of <D> will be replaces with defence name
     * @param message the message to send
     */
    private void sendMessage(String message){
        message= message.replace("<O>", players[state.getPossession()].getName());
        message= message.replace("<D>", players[(state.getPossession()+1)%2].getName());

        sendInfo(new MessageInfo(message));
    }

    /**
     * @return the current yard line as a football yard line
     */
    private int ballYardLine(){
        int yardLine= state.getBallPos();
        if(yardLine > 50){
            return yardLine - 50;
        }
        return yardLine;
    }

    private void setupPlayStrings(){
        playStrings= new HashMap<>();
        playStrings.put(shortRun, "Short Run");
        playStrings.put(longRun, "Long Run");
        playStrings.put(shortPass, "Short Pass");
        playStrings.put(longPass, "Long Pass");
        playStrings.put(bomb, "Bomb");
        playStrings.put(punt, "Punt");
        playStrings.put(fieldGoal, "Field Goal");
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
