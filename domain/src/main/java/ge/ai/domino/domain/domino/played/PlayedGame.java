package ge.ai.domino.domain.domino.played;

import java.util.Date;

public class PlayedGame {

    private int id;

    private String version;

    private PlayedGameResult result;

    private Date date;

    private int myPoint;

    private int himPoint;

    private int pointForWin;

    private String opponentName;

    private String website;

    private GameHistory gameHistory;

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

    public PlayedGameResult getResult() {
        return result;
    }

    public void setResult(PlayedGameResult result) {
        this.result = result;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getHimPoint() {
        return himPoint;
    }

    public void setHimPoint(int himPoint) {
        this.himPoint = himPoint;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public GameHistory getGameHistory() {
        return gameHistory;
    }

    public void setGameHistory(GameHistory gameHistory) {
        this.gameHistory = gameHistory;
    }
}
