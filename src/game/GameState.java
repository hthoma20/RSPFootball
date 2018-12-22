package game;

import action.KickoffAction;
import action.PATAction;

import java.util.Scanner;

public class GameState extends GameInfo{
    static Scanner in = new Scanner(System.in);

    private int[] scores;
    private RSP[] rsps;

    //yard line of ball
    private int ballPos;
    //yard line of firstDown
    private int firstDown;
    //the direction of play
    private boolean movingRight;

    //player index of posession
    //whose ball is it?
    private int possession;

    //what down is it
    private int down;

    //game clock
    private int quarter;
    private int playClock;

    //the position of the game
    private GamePos gamePos;
    //the previously called play
    private Play play;
    //the previous roll
    private int[] roll= null;

    /**
     * create a game state representing the beginning of
     * a game
     */
    public GameState(){
        this.scores= new int[2];
        this.rsps= new RSP[2];
        rsps[0]= null;
        rsps[1]= null;

        this.ballPos= 30;
        this.firstDown= 40;

        this.movingRight= true;

        this.possession= 1;

        this.down= 1;

        this.quarter= 1;
        this.playClock= 1;

        this.gamePos= GamePos.playCall;
    }

    //make a deep copy of this gamestate
    public GameState copy(){
        GameState copy= new GameState();

        //copy the scores
        copy.scores= new int[this.scores.length];
        for(int i=0; i < copy.scores.length; i++){
            copy.scores[i]= this.scores[i];
        }

        //copy the rsps
        copy.rsps= new RSP[this.rsps.length];
        for(int i=0; i < copy.rsps.length; i++){
            copy.rsps[i]= this.rsps[i];
        }

        copy.ballPos= this.ballPos;
        copy.firstDown= this.firstDown;

        copy.movingRight= this.movingRight;

        copy.possession= this.possession;

        copy.down= this.down;

        copy.playClock= this.playClock;
        copy.quarter= this.quarter;

        copy.gamePos= this.gamePos;

        copy.play= this.play;

        //copy roll
        if(this.roll == null){
            copy.roll= null;
        }
        else {
            copy.roll = new int[this.roll.length];
            for (int i = 0; i < copy.roll.length; i++) {
                copy.roll[i] = this.roll[i];
            }
        }

        return copy;
    }

    /**
     * update the state to reflect that a play was called
     * by the current player
     * @param play the play that was called
     */
    public void playCalled(Play play){
        this.play= play;
        this.gamePos= GamePos.rsp;
    }

    /**
     * registers that a player threw rsp
     * @param player the player that threw it
     * @param rsp what they threw
     */
    public void rspThrown(int player, RSP rsp){
        rsps[player]= rsp;
    }

    /**
     * using the values in the rsp array, compute which
     * player wins the rsp
     * sets both player's rsp value to null
     * @return the index of the winning player
     *          -1 if the rsp ties
     */
    public int rsp(){
        int winner= -1;

        if(rsps[0] == RSP.ROCK){
            if(rsps[1] == RSP.PAPER){
                winner= 1;
            }
            else if(rsps[1] == RSP.SCISSORS){
                winner= 0;
            }
        }
        else if(rsps[0] == RSP.SCISSORS){
            if(rsps[1] == RSP.PAPER){
                winner= 0;
            }
            else if(rsps[1] == RSP.ROCK){
                winner= 1;
            }
        }
        else{ //if rsps[0] == RSP.PAPER
            if(rsps[1] == RSP.ROCK){
                winner= 0;
            }
            else if(rsps[1] == RSP.SCISSORS){
                winner= 1;
            }
        }

        //reset player's rsp
        for(int i=0; i < rsps.length; i++){
            rsps[i]= null;
        }
        return winner;
    }

    /**
     * adjust state to reflect that the play is over
     */
    public void advancePlay(){
        gamePos= GamePos.playCall;

        this.playClock++;
        if(playClock >= 21){
            advanceQuarter();
        }

        //check for first down
        if(ballPos >= firstDown){
            down= 1;
            firstDown= ballPos + 10;
        }
        //otherwise advance clock normally
        else {
            this.down++;
            if (down >= 5) {
                switchPossession();
                this.gamePos= GamePos.playCall;
            }
        }
    }

    private void advanceQuarter(){
        this.quarter++;
    }

    /**
     * switch the possession after first moving the ball the given
     * number of yards in the current offence's direction
     * @param yards the number of yards to move first
     */
    public void switchPossession(int yards){
        this.possession++;
        this.possession%= 2;

        //switch direction of play
        ballPos= 100 - ballPos;
        firstDown= ballPos+10;
        movingRight= !movingRight;

        //reset the down clock
        down= 1;
    }


    public void switchPossession(){
        switchPossession(0);
    }

    /**
     * do the things that should happen on short run
     * @param rspWinner the player index who won the rsp
     */
    public void shortRun(int rspWinner){
        if(rspWinner == possession) {
            gamePos= GamePos.shortRun;
            runBall(5);
        }
        else{
            gamePos= GamePos.defenceRoll;
        }
    }

    /**
     * do the things that should happen on long run
     * @param rspWinner the player index who won the rsp
     */
    public void longRun(int rspWinner){
        if(rspWinner == possession){
            gamePos= GamePos.longRun;
        }
        else{
            gamePos= GamePos.defenceRoll;
        }
    }

    /**
     * do the things that should happen on short pass
     * @param rspWinner the player index who won the rsp
     */
    public void shortPass(int rspWinner){
        if(rspWinner == possession){
            gamePos= GamePos.shortPass;
            throwBall(10);
        }
        else{
            gamePos= GamePos.defenceChoice;
        }
    }

    /**
     * do the things that should happen on long pass
     * @param rspWinner the player index who won the rsp
     */
    public void longPass(int rspWinner){
        if(rspWinner == possession){
            gamePos= GamePos.longPass;
        }
        else{
            gamePos= GamePos.defenceChoice;
        }
    }

    /**
     * indicate that a long run has been fumbled
     */
    public void fumbleRun(){
        gamePos= GamePos.longRunFumble;
    }

    /**
     * indicate that the defence is going for an interception
     */
    public void interceptAttempt(){
        gamePos= GamePos.defenceRoll;
    }

    /**
     * indicate that the ball was intercepted
     * @param yards the yardage of the throw before the interception
     */
    public void interception(int yards){
        ballPos+= yards;

        advancePlay();

        if(ballPos >= 110){
            return; //no catch
        }

        if(ballPos >= 100){
            gamePos= GamePos.touchback;
        }
        else{
            switchPossession();
            gamePos= GamePos.kickReturn;
        }
    }

    /**
     * indicate that an interception has been made,
     * but we are waiting for a die roll for yardage
     */
    public void interception(){
        gamePos= GamePos.interception;
    }

    /**
     * determines whether we are waiting for the given
     * player to give an rsp
     * @param player the player
     * @return whether we need an rsp from this player
     */
    public boolean waitingForRSP(int player){
        boolean inRspPos= false;
        for(GamePos pos : LocalGame.rspPos){
            if(gamePos == pos){
                inRspPos= true;
                break;
            }
        }
        if(!inRspPos){
            return false;
        }

        return rsps[player] == null;
    }

    /**
     * determines whether we are waiting for the given player
     * to roll the dice
     *
     * @param player the player
     * @return the number of dice we are wating for the player to roll
     *          or -1 if we are not waiting for a roll
     */
    public int waitingForRoll(int player){
        //cases where the offence rolls
        if(player == possession){
            switch(gamePos){
                case regularKick:
                    return 3;

                case onsideKick:
                    return 2;

                case extraPoint:
                case longRun:
                case kickReturn:
                case longPass:
                    return 1;

                case interception:
                    switch(play){
                        case longPass:
                            return 1;
                    }
            }
        }
        //cases where the defence rolls
        else{
            switch(gamePos){
                case defenceRoll:
                    return 1;
            }
        }

        return -1;
    }

    private void touchDown(){
        scores[possession]+= 6;
        gamePos= GamePos.touchdown;
    }

    /**
     * advance the ball as if it had been run
     * @param yards the number of yards to move
     */
    public void runBall(int yards){
        this.ballPos+= yards;

        if(ballPos >= 100){
            touchDown();
        }
    }

    /**
     * advance ball as if it has been thrown
     * @param yards the number of yards to move
     * @return whether the pass was caught (whether it didn't go out of bounds)
     */
    public boolean throwBall(int yards){
        //can't over throw endzone
        if(ballPos + yards >= 110){
            return false;
        }
        //otherwise, it's exactly like running
        runBall(yards);
        return true;
    }

    /**
     * advance the ball as if there was a sack
     * @param yards the number of yards to sack
     */
    public void sackBall(int yards){
        ballPos-= yards;
    }

    /**
     * get the state ready for an extra point
     * @param type the type of extra point to go for
     */
    public void extraPoint(PATAction.PATType type){
        switch(type){
            case extraKick:
                gamePos= GamePos.extraPoint;
                break;
            case twoPointConversion:
                gamePos= GamePos.twoPointConversion;
        }

        ballPos= 95;
    }

    /**
     * indicates that the type of kickoff has been chosen
     * @param type the type of kickoff that is going to happen
     */
    public void kickoffType(KickoffAction.KickoffType type){
        switch(type){
            case regular:
                gamePos= GamePos.regularKick;
                break;
            case onside:
                gamePos= GamePos.onsideKick;
                break;
        }
    }

    /**
     * update the state to reflect that the
     * offencive team has made an extra point
     */
    public void extraKick(){
        scores[possession]+= 1;
    }

    /**
     * gets the state ready for a kickoff
     */
    public void kickOff(){
        ballPos= 30;
        gamePos= GamePos.kickoff;
    }

    /**
     * indicates that the ball has been kicked
     * @param yards the number of yards the ball is kicked
     */
    public void ballKicked(int yards){
        ballPos+= yards;

        //check for touchback
        if(ballPos >= 110){
            touchback();
        }
        else if(ballPos >= 100){
            //wait for player to choose touchback or regular
            gamePos= GamePos.touchback;
        }
        else{
            regularReturn();
        }

        switchPossession();
    }

    /**
     * indicates that an onside kick occured
     * @param kickerRecover whether the kicking team recovered
     */
    public void onsideKick(boolean kickerRecover){
        ballPos= 45;
        gamePos= GamePos.playCall;

        if(!kickerRecover){
            switchPossession();
        }
    }

    /**
     * indicates that the returning team
     * is going to run the ball out of endzone
     */
    public void regularReturn(){
        gamePos= GamePos.kickReturn;
    }

    /**
     * indicate that a kick has been returned
     * @param yards the number of yards on the return
     */
    public void kickReturned(int yards){
        runBall(yards);
        firstDown= ballPos+10;
        gamePos= GamePos.playCall;
    }

    /**
     * indicates that a kick return is not going to happen
     * ball is placed on the 20
     */
    public void touchback(){
        ballPos= 20;
        firstDown= 30;
        gamePos= GamePos.playCall;
    }

    /**
     * rolls some dice and stores the roll
     * in the roll array
     * @param numDice the number of dice to roll
     */
    public void rollDice(int numDice){
        System.out.println("Roll " + numDice);
        this.roll= new int[numDice];
        for(int i=0; i<roll.length; i++){
            //roll[i]= (int)((Math.random()*6)+1);
            roll[i]= in.nextInt();
        }
    }

    /**
     * compute the sum of the dice that have been rolled
     * @return the sum of the dice
     */
    public int sumDice(){
        if(roll == null){
            return 0;
        }

        int sum= 0;
        for(int dieRoll : roll){
            sum+= dieRoll;
        }

        return sum;
    }

    //GETTERS

    public int getScore(int player) {
        return scores[player];
    }

    public int getBallPos() {
        return ballPos;
    }

    public int getFirstDown() {
        return firstDown;
    }

    public int getPossession() {
        return possession;
    }

    public int getQuarter() {
        return quarter;
    }

    public int getPlayClock() {
        return playClock;
    }

    public GamePos getGamePos() {
        return gamePos;
    }

    public Play getPlay() {
        return play;
    }

    public int getDown() {
        return down;
    }

    public int[] getRoll() {
        return roll;
    }

    public boolean isMovingRight() {
        return movingRight;
    }
}
