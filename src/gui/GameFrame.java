package gui;

import game.GameState;
import game.HumanPlayer;
import game.Player;

import javax.swing.*;
import java.awt.*;

public class GameFrame {
    private Container pane;
    private FieldPanel field;
    private InfoPanel info;
    private ButtonPanel buttons;
    private ScorePanel score;

    /**
     *
     * @param guiPlayer the player this GameFrame is for
     */
    public GameFrame(HumanPlayer guiPlayer){
        //setLookAndFeel();

        JFrame frame= new JFrame();

        frame.setName("RSP Football");
        frame.setSize(700,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pane= frame.getContentPane();

        this.field= new FieldPanel();
        this.info= new InfoPanel();
        this.buttons= new ButtonPanel(guiPlayer, guiPlayer.getPlayerIndex());
        this.score= new ScorePanel();

        JFrame scoreFrame= new JFrame();
        scoreFrame.setName("Scoreboard");
        scoreFrame.setSize(500, 300);
        scoreFrame.add(score);


        layoutPanel();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        scoreFrame.setLocationByPlatform(true);
        scoreFrame.setVisible(true);
    }

    private void setLookAndFeel(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void layoutPanel(){
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c= new GridBagConstraints();

        c.fill= GridBagConstraints.BOTH;


        //add button panel
        c.gridx= 0;
        c.gridy= 0;
        c.weighty= 1;
        c.weightx= 1;
        c.gridwidth= 1;
        c.gridheight= 1;
        pane.add(buttons, c);

        //add field panel
        c.gridx= 0;
        c.gridy= 1;
        c.weighty= 4;
        c.weightx= 1;
        pane.add(field, c);

        //add info panel
        c.gridx= 0;
        c.gridy= 2;
        c.weighty= 5;
        c.weightx= 1;
        pane.add(info, c);
    }

    /**
     * updates the gui to replect the current state
     * @param state the current state of the game
     */
    public void updateFrame(GameState state){
        field.updateField(state);
        info.updateInfo(state);
        buttons.updateButtons(state);
        score.updateScore(state);
    }
}
