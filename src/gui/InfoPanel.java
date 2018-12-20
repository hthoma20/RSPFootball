package gui;

import game.GameState;

import javax.swing.*;

public class InfoPanel extends JPanel {
    private JTextArea textArea;

    public InfoPanel(){
        super();
        this.textArea= new JTextArea("Game Info");
        add(textArea);
    }

    /**
     * updates the info with the current state of the game
     * @param state the current state to get info off of
     */
    public void updateInfo(GameState state){
        String rollString= "";

        int[] roll= state.getRoll();
        if(roll != null) {
            for (int i = 0; i < roll.length; i++) {
                rollString += roll[i] + ",";
            }
        }

        String info= "GamePos: " + state.getGamePos() + "\n" +
                "playCall: " + state.getPlay() + "\n" +
                "ballPos: " + state.getBallPos() + "\n" +
                "Score 0: " + state.getScore(0) + "\n" +
                "Score 1: " + state.getScore(1) + "\n" +
                "Playclock: " + state.getPlayClock() + "\n" +
                "Down: " + state.getDown() + "\n" +
                "Roll: " + rollString + "\n" +
                "Direction: " + (state.isMovingRight() ? ">>" : "<<");

        textArea.setText(info);
    }
}
