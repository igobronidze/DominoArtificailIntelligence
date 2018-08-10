package ge.ai.domino.console.ui.played;

import ge.ai.domino.domain.played.GroupedPlayedGame;
import javafx.beans.property.SimpleStringProperty;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GroupedPlayedGameProperty {

    private final NumberFormat formatter = new DecimalFormat("#0.0000");

    private SimpleStringProperty version;

    private SimpleStringProperty pointForWin;

    private SimpleStringProperty opponentName;

    private SimpleStringProperty channel;

    private SimpleStringProperty winPercent;

    private SimpleStringProperty losePercent;

    private SimpleStringProperty stoppedPercent;

    private SimpleStringProperty winPercentForFinished;

    GroupedPlayedGameProperty(GroupedPlayedGame game) {
        version = new SimpleStringProperty(game.getVersion());
        pointForWin = new SimpleStringProperty(game.getPointForWin() == null ? "" : "" + game.getPointForWin());
        opponentName = new SimpleStringProperty(game.getOpponentName());
        channel = new SimpleStringProperty(game.getChannel() == null ? "" : game.getChannel().getName());
        int sum = game.getWin() + game.getLose() + game.getStopped();
        winPercent = new SimpleStringProperty("" + game.getWin() + " (" + formatter.format((double)game.getWin() / sum * 100) + "%)");
        losePercent = new SimpleStringProperty("" + game.getLose() + " (" + formatter.format((double)game.getLose() / sum * 100) + "%)");
        stoppedPercent = new SimpleStringProperty("" + game.getStopped() + " (" + formatter.format((double)game.getStopped() / sum * 100) + "%)");
        winPercentForFinished = new SimpleStringProperty("" + formatter.format((double)game.getWin() / (game.getWin() + game.getLose()) * 100) + "%");
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

    public String getChannel() {
        return channel.get();
    }

    public void setChannel(String channel) {
        this.channel.set(channel);
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

    public String getWinPercentForFinished() {
        return winPercentForFinished.get();
    }

    public void setWinPercentForFinished(String winPercentForFinished) {
        this.winPercentForFinished.set(winPercentForFinished);
    }
}
