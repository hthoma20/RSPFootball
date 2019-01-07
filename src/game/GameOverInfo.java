package game;

public class GameOverInfo extends GameInfo {
    private String winnerMessage;

    public GameOverInfo(String winnerMessage) {
        this.winnerMessage = winnerMessage;
    }

    public String getWinnerMessage() {
        return winnerMessage;
    }
}
