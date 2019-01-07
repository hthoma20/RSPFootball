package game.player;

import action.*;
import game.*;
import gui.GameFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HumanPlayer extends Player implements ActionListener {
    private GameFrame frame;

    public HumanPlayer(LocalGame game, String name, int index) {
        super(game, name, index);

        this.frame= new GameFrame(this);
    }

    @Override
    public void receiveInfo(GameInfo info) {
        if(info instanceof GameState){
            GameState state= (GameState)info;

            frame.updateFrame(state);
        }
        if(info instanceof MessageInfo){
            frame.newMessage(((MessageInfo) info).getMessage());
        }
        if(info instanceof GameOverInfo){
            frame.newMessage("Game Over");
            frame.newMessage(((GameOverInfo) info).getWinnerMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command= e.getActionCommand();
        int colonIndex= command.indexOf(':');

        if(colonIndex == -1){
            System.out.println("Human Player recieved bad action format");
            return;
        }

        //parse off the action type and choice
        String action= command.substring(0, colonIndex);
        String param= command.substring(colonIndex+1);

        System.out.println(action + " " + param);

        if(action.equals("playButton")){
            //get the play off the back of the command
            Play play= Play.valueOf(param);
            this.sendGameAction(new PlaycallAction(this, play));
        }
        else if(action.equals("patButton")){
            PATAction.PATType type= PATAction.PATType.valueOf(param);
            this.sendGameAction(new PATAction(this, type));
        }
        else if(action.equals("rspButton")){
            RSP rsp= RSP.valueOf(param);
            this.sendGameAction(new RSPAction(this, rsp));
        }
        else if(action.equals("rollButton")){
            //parse off the number of dice to roll
            int numDice= Integer.parseInt(param);
            this.sendGameAction(new RollAction(this, numDice));
        }
        else if(action.equals("kickoffButton")){
            KickoffAction.KickoffType type= KickoffAction.KickoffType.valueOf(param);
            this.sendGameAction(new KickoffAction(this, type));
        }
        else if(action.equals("touchbackButton")){
            KickReturnAction.KickReturnType type= KickReturnAction.KickReturnType.valueOf(param);
            this.sendGameAction(new KickReturnAction(this, type));
        }
        else if(action.equals("defenceButton")){
            DefenceAction.Choice choice= DefenceAction.Choice.valueOf(param);
            this.sendGameAction(new DefenceAction(this, choice));
        }
        else if(action.equals("bombButton")){
            boolean done= false;
            if(param.equals("done")){
                done= true;
            }
            this.sendGameAction(new BombAction(this, done));
        }
        else if(action.equals("fakeButton")){
            FakeAction.KickType type= FakeAction.KickType.valueOf(param);
            this.sendGameAction(new FakeAction(this, type));
        }
        else{
            System.out.println("Human Player recieved unknown action: " + command);
        }
    }
}
