package gui;

import game.GameState;
import javafx.scene.canvas.Canvas;

import javax.swing.*;
import java.awt.*;

public class FieldPanel extends JPanel {
    private int ballPos;
    private int firstDown;
    private boolean movingRight;

    public FieldPanel(){
        super();

        this.ballPos= 50;
        this.firstDown= 60;
    }

    @Override
    public void paint(Graphics g){
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, getWidth(), getHeight());

        paintYardLines(g);
        paintFirstDown(g);
        paintBall(g);
    }

    //paints yard markers
    private void paintYardLines(Graphics g){
        if(g instanceof Graphics2D){
            ((Graphics2D)g).setStroke(new BasicStroke(5));
        }

        g.setColor(Color.WHITE);

        for(int yard=0; yard <= 100; yard+= 10){
            int x= yardToX(yard);
            g.drawLine(x, 0, x, getHeight());
        }

        for(int yard=5; yard <= 95; yard+= 10){
            int x= yardToX(yard);
            g.drawLine(x, 0, x, getHeight());
        }
    }

    private void paintFirstDown(Graphics g){
        if(g instanceof Graphics2D){
            ((Graphics2D)g).setStroke(new BasicStroke(5));
        }

        g.setColor(Color.YELLOW);

        int x= yardToX(firstDown);
        g.drawLine(x, 0, x, getHeight());
    }

    private void paintBall(Graphics g){
        g.setColor(Color.GRAY);

        int x= yardToX(ballPos);
        int y= getHeight()/2;

        int ballWidth= len5();
        int ballHeight= 2*ballWidth/5;

        g.fillOval(x-ballWidth/2, y-ballHeight/2, ballWidth, ballHeight);
    }

    //returns the x coord of a given yard line
    public int yardToX(int yard){
        int naturalPos= (yard+10)*getWidth()/120;

        return movingRight ? naturalPos : getWidth()-naturalPos;
    }

    //the length in pixels of 5 yards
    public int len5(){
        return getWidth()/24;
    }

    public void setBallPos(int ballPos) {
        this.ballPos = ballPos;
    }

    public void setFirstDown(int firstDown) {
        this.firstDown = firstDown;
    }

    public void setMovingRight(boolean movingRight){
        this.movingRight= movingRight;
    }

    /**
     * update the field to reflect the current state of the game
     * @param state the current state of the game
     */
    public void updateField(GameState state){
        setBallPos(state.getBallPos());
        setFirstDown(state.getFirstDown());
        setMovingRight(state.isMovingRight());
        this.repaint();
    }
}
