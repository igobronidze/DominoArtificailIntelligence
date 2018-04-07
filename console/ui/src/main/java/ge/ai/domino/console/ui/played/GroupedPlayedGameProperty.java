package ge.ai.domino.console.ui.played;

import ge.ai.domino.domain.played.GroupedPlayedGame;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class GroupedPlayedGameProperty {

    private SimpleStringProperty version;

    private SimpleStringProperty pointForWin;

    private SimpleStringProperty opponentName;

    private SimpleStringProperty website;

    private SimpleStringProperty winPercent;

    private SimpleStringProperty losePercent;

    private SimpleStringProperty stoppedPercent;

    private SimpleDoubleProperty winPercentForFinished;

    GroupedPlayedGameProperty(GroupedPlayedGame game) {
        version = new SimpleStringProperty(game.getVersion());
        pointForWin = new SimpleStringProperty(game.getPointForWin() == null ? "" : "" + game.getPointForWin());
        opponentName = new SimpleStringProperty(game.getOpponentName());
        website = new SimpleStringProperty(game.getWebsite());
        int sum = game.getWin() + game.getLose() + game.getStopped();
        winPercent = new SimpleStringProperty("" + game.getWin() + " (" + ((double)game.getWin() / sum * 100) + "%)");
        losePercent = new SimpleStringProperty("" + game.getLose() + " (" + ((double)game.getLose() / sum * 100) + "%)");
        stoppedPercent = new SimpleStringProperty("" + game.getStopped() + " (" + ((double)game.getStopped() / sum * 100) + "%)");
        winPercentForFinished = new SimpleDoubleProperty((double)game.getWin() / (game.getWin() + game.getLose()) * 100);
    }

    public String getVersion() {
        return version.get();
    }

    public void setVersion(String version) {
        this.version.set(version);
    }

    public String getPointForWin() {
        return pointForWin.get();
    }

    public void setPointForWin(String pointForWin) {
        this.pointForWin.set(pointForWin);
    }

    public String getOpponentName() {
        return opponentName.get();
    }

    public void setOpponentName(String opponentName) {
        this.opponentName.set(opponentName);
    }

    public String getWebsite() {
        return website.get();
    }

    public void setWebsite(String website) {
        this.website.set(website);
    }

    public String getWinPercent() {
        return winPercent.get();
    }

    public void setWinPercent(String winPercent) {
        this.winPercent.set(winPercent);
    }

    public String getLosePercent() {
        return losePercent.get();
    }

    public void setLosePercent(String losePercent) {
        this.losePercent.set(losePercent);
    }

    public String getStoppedPercent() {
        return stoppedPercent.get();
    }

    public void setStoppedPercent(String stoppedPercent) {
        this.stoppedPercent.set(stoppedPercent);
    }

    public double getWinPercentForFinished() {
        return winPercentForFinished.get();
    }

    public void setWinPercentForFinished(double winPercentForFinished) {
        this.winPercentForFinished.set(winPercentForFinished);
    }
}
