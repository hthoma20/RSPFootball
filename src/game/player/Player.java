package game.player;

import action.GameAction;
import game.GameInfo;
import game.LocalGame;

public abstract class Player {
    protected LocalGame game;
    protected String name;
    protected int playerIndex;

    public Player(LocalGame game, String name, int index){
        this.game= game;
        this.name= name;
        this.playerIndex= index;
    }

    /**
     * recieve and handle info,
     * use this method to send info to a player
     * it calles recieveinfo for the specific player
     * @param info the info the player recieves
     */
    public final void sendInfo(GameInfo info){
        receiveInfo(info);
    }

    /**
     * sends an action to the local game
     * @param action the action to send
     */
    protected void sendGameAction(GameAction action){
        //send the action on a new thread
        Thread thread= new Thread(() -> this.game.sendAction(action));
        thread.start();
    }

    protected abstract void receiveInfo(GameInfo info);

    public String getName(){
        return name;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }
}
