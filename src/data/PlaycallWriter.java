package data;

import game.GameState;
import game.Play;
import weka.core.converters.ArffSaver;

import java.io.File;

public class PlaycallWriter {
    private ArffSaver arffSaver;

    /**
     * construct a playcall writer which will
     * create a new .arff file
     */
    public PlaycallWriter(){

    }

    /**
     * construct a playcall writer with the .arff fire
     * to write to
     * @param file the file to write data to
     */
    public PlaycallWriter(File file){

    }

    /**
     * write the given information as an instance
     * in the .arff file
     *
     * @param state the state of the game when the play was called
     * @param playCalled the play that was called
     */
    public void writeInstance(GameState state, Play playCalled){

    }
}
