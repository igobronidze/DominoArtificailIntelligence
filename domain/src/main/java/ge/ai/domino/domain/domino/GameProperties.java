package ge.ai.domino.domain.domino;

public class GameProperties {

    private String opponentName;

    private String website;

    private int pointForWin;

    private boolean start;

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

    public int getPointForWin() {
        return pointForWin;
    }

    public void setPointForWin(int pointForWin) {
        this.pointForWin = pointForWin;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}
