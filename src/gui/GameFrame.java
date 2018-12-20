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

    /**
     *
     * @param guiPlayer the player this GameFrame is for
     */
    public GameFrame(HumanPlayer guiPlayer){
        JFrame frame= new JFrame();

        frame.setName("RSP Football");
        frame.setSize(700,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pane= frame.getContentPane();

        this.field= new FieldPanel();
        this.info= new InfoPanel();
        this.buttons= new ButtonPanel(guiPlayer, guiPlayer.getPlayerIndex());

        layoutPanel();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
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
        c.weighty= 3;
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
    }
}
