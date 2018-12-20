package game;

public abstract class Player {
    protected LocalGame game;
    protected String name;
    protected int playerIndex;

    public Player(LocalGame game, String name, int index){
        this.game= game;
        this.name= name;
        this.playerIndex= index;
    }

    public abstract void receiveInfo(GameInfo info);

    public String getName(){
        return name;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }
}
