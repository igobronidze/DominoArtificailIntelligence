package ge.ai.domino.domain.played;

import ge.ai.domino.domain.channel.Channel;

import java.util.Date;

public class PlayedGame {

    private int id;

    private String version;

    private GameResult result;

    private Date endDate;

    private int myPoint;

    private int opponentPoint;

    private int pointForWin;

    private String opponentName;

    private Channel channel;

    private GameHistory gameHistory;

    private String marshaledGameHistory;

    private int level;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getOpponentPoint() {
        return opponentPoint;
    }

    public void setOpponentPoint(int opponentPoint) {
        this.opponentPoint = opponentPoint;
    }

    public int getMyPoint() {
        return myPoint;
    }

    public void setMyPoint(int myPoint) {
        this.myPoint = myPoint;
    }

    public int getPointForWin() {
        return pointForWin;
    }

    public void setPointForWin(int pointForWin) {
        this.pointForWin = pointForWin;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public GameHistory getGameHistory() {
        return gameHistory;
    }

    public void setGameHistory(GameHistory gameHistory) {
        this.gameHistory = gameHistory;
    }

    public String getMarshaledGameHistory() {
        return marshaledGameHistory;
    }

    public void setMarshaledGameHistory(String marshaledGameHistory) {
        this.marshaledGameHistory = marshaledGameHistory;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
