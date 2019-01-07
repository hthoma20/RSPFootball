package gui;

import javax.swing.*;
import java.util.ArrayList;

public class MessagePanel extends JPanel {
    private JLabel messageLabel;
    private ArrayList<String> messages;
    private int numMessages= 7;

    public MessagePanel(){
        super();

        messages= new ArrayList<>();
        messageLabel= new JLabel("<html></html>");
        this.add(messageLabel);
    }

    public void newMessage(String message){
        messages.add(message);

        String text= "<html>";

        int stop= Math.max(0, messages.size() - numMessages);

        for(int i= messages.size()-1; i >= stop; i--){
            text+= messages.get(i) + "<br>";
        }
        text+= "<html>";

        messageLabel.setText(text);
    }

    /**
     * appends the given string to the message label
     */
    private void append(String str){
        String text= messageLabel.getText();

        //chop off the html tag
        text= text.substring(0,text.length()-7);
        //add the new message
        text+= str;
        //add the line break and the html tag
        text+= "<br></html>";

        messageLabel.setText(text);
    }
}
