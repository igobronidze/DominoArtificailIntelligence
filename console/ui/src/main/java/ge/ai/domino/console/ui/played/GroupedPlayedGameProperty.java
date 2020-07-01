package ge.ai.domino.console.ui.played;

import ge.ai.domino.domain.played.GroupedPlayedGame;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GroupedPlayedGameProperty {

    private static final NumberFormat formatter = new DecimalFormat("#0.0000");

    private final SimpleStringProperty version;

    private final SimpleStringProperty pointForWin;

    private final SimpleStringProperty opponentName;

    private final SimpleStringProperty channel;

    private final SimpleIntegerProperty finished;

    private final SimpleStringProperty winPercent;

    private final SimpleStringProperty losePercent;

    private final SimpleStringProperty stoppedPercent;

    private final SimpleStringProperty winPercentForFinished;

    private final SimpleStringProperty level;

    private final SimpleDoubleProperty profit;

    GroupedPlayedGameProperty(GroupedPlayedGame game) {
        version = new SimpleStringProperty(game.getVersion());
        pointForWin = new SimpleStringProperty(game.getPointForWin() == null ? "" : "" + game.getPointForWin());
        opponentName = new SimpleStringProperty(game.getOpponentName());
        channel = new SimpleStringProperty(game.getChannel() == null ? "" : game.getChannel().getName());
        finished = new SimpleIntegerProperty(game.getFinished());
        int sum = game.getWin() + game.getLose() + game.getStopped();
        winPercent = new SimpleStringProperty("" + game.getWin() + " (" + formatter.format((double)game.getWin() / sum * 100) + "%)");
        losePercent = new SimpleStringProperty("" + game.getLose() + " (" + formatter.format((double)game.getLose() / sum * 100) + "%)");
        stoppedPercent = new SimpleStringProperty("" + game.getStopped() + " (" + formatter.format((double)game.getStopped() / sum * 100) + "%)");
        winPercentForFinished = new SimpleStringProperty("" + formatter.format((double)game.getWin() / (game.getWin() + game.getLose()) * 100) + "%");
        level = new SimpleStringProperty(game.getLevel() == null ? "" : "" + game.getLevel());
        profit = new SimpleDoubleProperty(game.getProfit() == null ? 0.0 : game.getProfit());
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

    public Integer getFinished() {
        return finished.get();
    }

    public void setFinished(Integer finished) {
        this.finished.set(finished);
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

    public String getLevel() {
        return level.get();
    }

    public void setLevel(String level) {
        this.winPercentForFinished.set(level);
    }

    public Double getProfit() {
        return profit.get();
    }

    public void setProfit(Double profit) {
        this.profit.set(profit);
    }
}
