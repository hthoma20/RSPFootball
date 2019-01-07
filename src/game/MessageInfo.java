package game;

/**
 * holds a message about something that has happened in the game
 * used for user interface
 */
public class MessageInfo extends GameInfo {
    private String message;

    public MessageInfo(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
