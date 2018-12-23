package gui;

import game.GameState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class ScorePanel extends JPanel {
    private Image homeLabel;
    private Image awayLabel;
    private Image penLabel;
    private Image perLabel;
    private Image downLabel;

    private Image homeScore;
    private Image awayScore;
    private Image homePen;
    private Image awayPen;
    private Image down;
    private Image per;
    private Image time;

    public ScorePanel(){
        super();

        try{
            homeLabel= ImageIO.read(scoreFile("label","home.png"));
            awayLabel= ImageIO.read(scoreFile("label","away.png"));
            penLabel= ImageIO.read(scoreFile("label","penalty.png"));
            perLabel= ImageIO.read(scoreFile("label","period.png"));
            downLabel= ImageIO.read(scoreFile("label","down.png"));

        }
        catch (IOException io){
            io.printStackTrace();
        }
    }

    /**
     * makes a file from the given path
     * @param path the paths to go thru, starting at res/score
     * @return the platform independent file path
     */
    private File scoreFile(String... path){
        String filePath= "res"+File.separator+"score";
        for(String str : path){
            filePath+= File.separator + str;
        }
        return new File(filePath);
    }

    @Override
    public void paint(Graphics g){
        int width= getWidth();
        int height= getHeight();

        g.setColor(Color.BLACK);
        g.fillRect(0,0, width, height);

        int homeLabelx= 0;
        int awayLabelx= 3*width/4;
        int perLabelx= width/4;
        int downLabelx= width/2;
        int homePenx= width/10;
        int awayPenx= 9*width/11;
        int perx= width/3;
        int downx= 3*width/5;
        int homex= width/20;
        int awayx= 16*width/20;
        int timex= 2*width/5;

        int teamLabely= 0;
        int penLabely= 2*height/3;
        int timeLabely= height/2;
        int timey= 0;
        int scorey= height/6;
        int peny= 5*height/6;

        int smallHeight= height/6;
        int medHeight= height/4;
        int largeHeight= 7*height/16;

        int smallWidth= width/20;
        int medWidth= width/6;
        int largeWidth= width/4;

        int smallLabelHeight= height/7;
        int largeLabelHeight= height/6;

        int teamLabelWidth= 2*width/9;
        int penLabelWidth= 2*width/11;
        int perLabelWidth= 2*width/11;
        int downLabelWidth= 3*width/11;

        //home label
        g.drawImage(homeLabel, homeLabelx, teamLabely, teamLabelWidth, largeLabelHeight, this);
        //away label
        g.drawImage(awayLabel, awayLabelx, teamLabely, teamLabelWidth, largeLabelHeight, this);
        //home pen label
        g.drawImage(penLabel, homeLabelx, penLabely, penLabelWidth, smallLabelHeight, this);
        //away pen label
        g.drawImage(penLabel, awayLabelx, penLabely, penLabelWidth, smallLabelHeight, this);
        //per label
        g.drawImage(perLabel, perLabelx, timeLabely, perLabelWidth, smallLabelHeight, this);
        //down label
        g.drawImage(downLabel, downLabelx, timeLabely, downLabelWidth, smallLabelHeight, this);

        //home score
        g.drawImage(homeScore, homex, scorey, medWidth, medHeight, this);
        //away score
        g.drawImage(awayScore, awayx, scorey, medWidth, medHeight, this);
        //home pen
        g.drawImage(homePen, homePenx, peny, smallWidth, smallHeight, this);
        //away pen
        g.drawImage(awayPen, awayPenx, peny, smallWidth, smallHeight, this);
        //period
        g.drawImage(per, perx, penLabely, smallWidth, smallHeight, this);
        //down
        g.drawImage(down, downx, penLabely, smallWidth, smallHeight, this);
        //time
        g.drawImage(time, timex, timey, largeWidth, largeHeight, this);
    }

    /**
     * update the score board to reflect the current state
     * of the game
     * @param state the state of the game
     */
    public void updateScore(GameState state){
        updateScoreImages(state);
        repaint();
    }

    /**
     * load the correct images to reflect the score
     * @param state the current state of the game
     */
    private void updateScoreImages(GameState state){
        try{
            this.homeScore= intToImage2(state.getScore(0), "red");
            this.awayScore= intToImage2(state.getScore(1), "red");
            this.homePen= intToImage1(0, "red");
            this.awayPen= intToImage1(0, "red");
            this.down= intToImage1(state.getDown(), "red");
            this.per= intToImage1(state.getQuarter(), "red");
            this.time= intToImage2(state.getPlayClock(), "red");
        }
        catch (IOException io){
            io.printStackTrace();
        }
    }

    /**
     * convert a number to a two digit image
     * @param num the number to convert
     * @param color the color of the image
     * @return an image which is the number
     */
    private BufferedImage intToImage2(int num, String color) throws IOException {
        int digit1= num/10;
        int digit0= num%10;

        BufferedImage img1= intToImage1(digit1, color);
        BufferedImage img0= intToImage1(digit0, color);

        int space= 10;
        int width= img1.getWidth()+img0.getWidth()+space;
        int height= img1.getHeight();

        BufferedImage img= new BufferedImage(width, height, TYPE_INT_RGB);
        Graphics g= img.createGraphics();
        g.drawImage(img1,0,0,this);
        g.drawImage(img0,img1.getWidth()+space,0,this);

        return img;
    }

    /**
     * convers a number to a one digit image
     * @param digit the number to convert
     * @param color the color of the image
     * @return an image representing the number
     */
    private BufferedImage intToImage1(int digit, String color) throws IOException {
        return ImageIO.read(scoreFile(color,String.valueOf(digit)+".png"));
    }
}
