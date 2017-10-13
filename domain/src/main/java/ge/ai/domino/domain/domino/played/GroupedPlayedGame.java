package ge.ai.domino.domain.domino.played;

public class GroupedPlayedGame {

    private String version;

    private String opponentName;

    private String website;

    private Integer pointForWin;

    private int win;

    private int lose;

    private int stopped;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public Integer getPointForWin() {
        return pointForWin;
    }

    public void setPointForWin(Integer pointForWin) {
        this.pointForWin = pointForWin;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public int getStopped() {
        return stopped;
    }

    public void setStopped(int stopped) {
        this.stopped = stopped;
    }
}
