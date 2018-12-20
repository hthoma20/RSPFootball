package game;

import action.*;
import game.Play;
import gui.GameFrame;

import javax.swing.*;
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command= e.getActionCommand();

        if(command.equals("shortRunButton")){
            this.game.sendAction(new PlaycallAction(this, Play.shortRun));
        }
        else if(command.equals("longRunButton")){
            this.game.sendAction(new PlaycallAction(this, Play.longRun));
        }
        else if(command.equals("extraKickButton")){
            this.game.sendAction(new PATAction(this, PATAction.PATType.extraKick));
        }
        else if(command.equals("twoPointButton")){
            this.game.sendAction(new PATAction(this, PATAction.PATType.twoPointConversion));
        }
        else if(command.equals("rockButton")){
            this.game.sendAction(new RSPAction(this, RSP.ROCK));
        }
        else if(command.equals("scissorsButton")){
            this.game.sendAction(new RSPAction(this, RSP.SCISSORS));
        }
        else if(command.equals("paperButton")){
            this.game.sendAction(new RSPAction(this, RSP.PAPER));
        }
        else if(command.startsWith("rollButton")){
            //roll commands are of form rollButton1 or rollButton2,
            //so parse off the number of dice to roll
            int numDice= command.charAt(10) - '0';
            this.game.sendAction(new RollAction(this, numDice));
        }
        else if(command.equals("regularKickButton")){
            this.game.sendAction(new KickoffAction(this, KickoffAction.KickoffType.regular));
        }
        else if(command.equals("onsideKickButton")){
            this.game.sendAction(new KickoffAction(this, KickoffAction.KickoffType.onside));
        }
        else if(command.equals("touchbackButton")){
            this.game.sendAction(new KickReturnAction(this, KickReturnAction.KickReturnType.touchback));
        }
        else if(command.equals("runReturnButton")){
            this.game.sendAction(new KickReturnAction(this, KickReturnAction.KickReturnType.regular));
        }
        else{
            System.out.println("Human Player recieved unknown action: " + command);
        }
    }
}
