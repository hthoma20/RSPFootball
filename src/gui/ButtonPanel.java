package gui;

import game.GamePos;
import game.GameState;
import game.LocalGame;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;


public class ButtonPanel extends JPanel {
    private ActionListener listner;
    private int playerIndex;

    //keep a map of buttons and their rules for displaying
    private HashMap<JButton, DisplayRule> buttonRules = new HashMap<>();

    /**
     * constructor
     * @param listner the listener that we want to send events to
     * @param playerIndex the index of the player we are giving buttons to
     */
    public ButtonPanel(ActionListener listner, int playerIndex){
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.playerIndex= playerIndex;
        this.listner= listner;
        setupButtons();
    }

    private void setupButtons(){
        DisplayRule playCallRule= gamePosRule(true, false, GamePos.playCall, GamePos.twoPointConversion);
        this.add(createButton("Short Run", "shortRunButton", playCallRule));
        this.add(createButton("Long Run", "longRunButton", playCallRule));

        DisplayRule rspRule= gamePosRule(true, true, LocalGame.rspPos);
        this.add(createButton("Rock", "rockButton", rspRule));
        this.add(createButton("Scissors", "scissorsButton", rspRule));
        this.add(createButton("Paper", "paperButton", rspRule));

        DisplayRule touchdownRule= gamePosRule(true, false, GamePos.touchdown);
        this.add(createButton("1-point kick", "extraKickButton", touchdownRule));
        this.add(createButton("2-point conversion", "twoPointButton", touchdownRule));

        DisplayRule roll1Rule= orRules(
                gamePosRule(false, true, GamePos.defenceRoll),
                gamePosRule(true, false, GamePos.kickReturn),
                gamePosRule(true, false, GamePos.longRun)
            );
        this.add(createButton("Roll 1", "rollButton1", roll1Rule));
        DisplayRule roll2Rule= gamePosRule(true, false, GamePos.extraPoint, GamePos.onsideKick);
        this.add(createButton("Roll 2", "rollButton2", roll2Rule));
        DisplayRule roll3Rule= gamePosRule(true, false, GamePos.regularKick);
        this.add(createButton("Roll 3", "rollButton3", roll3Rule));

        DisplayRule kickoffRule= gamePosRule(true, false, GamePos.kickoff);
        this.add(createButton("Regular kick-off", "regularKickButton", kickoffRule));
        this.add(createButton("Onside kick-off", "onsideKickButton", kickoffRule));

        DisplayRule touchbackRule= gamePosRule(true, false, GamePos.touchback);
        this.add(createButton("Touchback", "touchbackButton", touchbackRule));
        this.add(createButton("Run it out", "runReturnButton", touchbackRule));
    }

    /**
     * create a JButton
     * @param text the text on the button
     * @param command the action command for the button
     * @param rule the DisplayRule that must be true for this button to be displayed
     * @return the button that was created
     */
    private JButton createButton(String text, String command, DisplayRule rule){
        JButton button= new JButton(text);
        button.setActionCommand(command);
        button.addActionListener(listner);

        buttonRules.put(button, rule);

        return button;
    }

    /**
     * updates the game to reflect the current game state
     * @param state the state of the game
     */
    public void updateButtons(GameState state){
        for(JButton button : buttonRules.keySet()){
            button.setVisible(buttonRules.get(button).display(state));
        }
    }

    /***
     * creates a display rule that is true when one of the given gamePos is the current gamePos
     * and the player is on offence or defence accordingly
     *
     * @param offence whether the rule should be true when the player is on offence
     * @param defence whether the rule should be true when the player is on defence
     * @param gamePoses the list of gamePoses to make this rule true
     * @return a rule that is true when the state is in one of the given gamepos
     */
    private DisplayRule gamePosRule(boolean offence, boolean defence, GamePos... gamePoses){
        return state -> {
            boolean onOffence= state.getPossession() == this.playerIndex;

            //if we're on offence, we need the offence flag
            if(onOffence && !offence){
                return false;
            }
            //if we're on defence, we need the defence flag
            if(!onOffence && !defence){
                return false;
            }

            GamePos currPos= state.getGamePos();
            for(GamePos gamePos : gamePoses){
               if(gamePos == currPos){
                   return true;
               }
            }

            return false;
        };
    }

    /**
     * @param rules the rules to or together
     * @return a DisplayRule which is true if any of the given DisplayRules
     *           are true
     */
    private DisplayRule orRules(DisplayRule... rules){
        return state -> {
            for(DisplayRule rule : rules){
                if(rule.display(state)){
                    return true;
                }
            }

            return false;
        };
    }

    private interface DisplayRule{
        /**
         * determine whether to display a component based on this rule
         * if the state fits the rule, then the display will return true
         * @param state the state to check the rule against
         * @return whether the state fits the rule
         */
        boolean display(GameState state);
    }
}
