package ge.ai.domino.domain.domino;

public class GameProperties {

    private String opponentName;

    private String website;

    private int pointsForWin;

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

    public int getPointsForWin() {
        return pointsForWin;
    }

    public void setPointsForWin(int pointsForWin) {
        this.pointsForWin = pointsForWin;
    }
}
