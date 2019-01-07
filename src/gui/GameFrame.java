package gui;

import game.GameState;
import game.player.HumanPlayer;

import javax.swing.*;
import java.awt.*;

public class GameFrame {
    private Container pane;
    private FieldPanel field;
    private InfoPanel info;
    private ButtonPanel buttons;
    private ScorePanel score;
    private MessagePanel messagePanel;

    /**
     *
     * @param guiPlayer the player this GameFrame is for
     */
    public GameFrame(HumanPlayer guiPlayer){
        //setLookAndFeel();

        JFrame frame= new JFrame();

        frame.setName("RSP Football");
        frame.setSize(800,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pane= frame.getContentPane();

        this.field= new FieldPanel();
        this.info= new InfoPanel();
        this.buttons= new ButtonPanel(guiPlayer, guiPlayer.getPlayerIndex());
        this.score= new ScorePanel();
        this.messagePanel= new MessagePanel();

        layoutPanel();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
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
        c.insets= new Insets(5,5,5,5);

        //add the logo
        c.gridx= 0;
        c.gridy= 0;
        c.gridwidth= 1;
        c.gridheight= 1;
        c.weightx= 2;
        c.weighty= 1;
        pane.add(new ImagePanel("res", "logo.png"), c);

        //add score board
        c.gridx= 1;
        c.gridy= 0;
        c.weightx= 2;
        c.weighty= 1;
        pane.add(score, c);

        //add message panel
        c.gridx= 2;
        c.gridy= 0;
        c.weightx= 1;
        c.weighty= 4;
        pane.add(messagePanel, c);

        //add field panel
        c.gridx= 0;
        c.gridy= 1;
        c.gridwidth= 3;
        c.gridheight= 1;
        c.weighty= 10;
        c.weightx= 1;
        pane.add(field, c);

        //add button panel
        c.gridx= 0;
        c.gridy= 2;
        c.weighty= 1;
        c.weightx= 1;
        c.gridwidth= 3;
        c.gridheight= 1;
        pane.add(buttons, c);
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

    /**
     * updates the gui to reflect the new message
     * @param message the new message
     */
    public void newMessage(String message){
        messagePanel.newMessage(message);
    }
}
