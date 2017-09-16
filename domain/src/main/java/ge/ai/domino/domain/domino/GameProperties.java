package ge.ai.domino.domain.domino;

public class GameProperties {

    private String opponentName;

    private String website;

    private int pointsForWin;

    private boolean start;

    private boolean firstHand;

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

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isFirstHand() {
        return firstHand;
    }

    public void setFirstHand(boolean firstHand) {
        this.firstHand = firstHand;
    }
}
