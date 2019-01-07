package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {
    private BufferedImage image;

    public ImagePanel(String... path){
        super();

        readImage(new File(concatWithSeperators(path)));
    }

    public ImagePanel(String path){
        this(new File(path));
    }

    public ImagePanel(File imageFile){
        super();

        readImage(imageFile);
    }

    private void readImage(File imageFile){
        try{
            image= ImageIO.read(imageFile);
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
    }

    private String concatWithSeperators(String[] strings){
        if(strings == null || strings.length == 0){
            return "";
        }

        String string= strings[0];
        for(int i= 1; i < strings.length; i++){
            string+= File.separator + strings[i];
        }

        return string;
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
}
