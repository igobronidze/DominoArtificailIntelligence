package ge.ai.domino.console.ui.played;

import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.domain.played.PlayedGame;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.SimpleDateFormat;

public class PlayedGameProperty {

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimpleIntegerProperty id;

    private SimpleStringProperty version;

    private SimpleStringProperty result;

    private SimpleStringProperty date;

    private SimpleIntegerProperty myPoint;

    private SimpleIntegerProperty opponentPoint;

    private SimpleIntegerProperty pointForWin;

    private SimpleStringProperty opponentName;

    private SimpleStringProperty channel;

    private SimpleDoubleProperty level;

    public PlayedGameProperty(PlayedGame game) {
        id = new SimpleIntegerProperty(game.getId());
        version = new SimpleStringProperty(game.getVersion());
        result = new SimpleStringProperty(Messages.get(game.getResult().name()));
        if (game.getEndDate() != null) {
            date = new SimpleStringProperty(df.format(game.getEndDate()));
        } else {
            date = new SimpleStringProperty("");
        }
        myPoint = new SimpleIntegerProperty(game.getMyPoint());
        opponentPoint = new SimpleIntegerProperty(game.getOpponentPoint());
        pointForWin = new SimpleIntegerProperty(game.getPointForWin());
        opponentName = new SimpleStringProperty(game.getOpponentName());
        channel = new SimpleStringProperty(game.getChannel().getName());
        level = new SimpleDoubleProperty(game.getLevel());
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getVersion() {
        return version.get();
    }

    public void setVersion(String version) {
        this.version.set(version);
    }

    public String getResult() {
        return result.get();
    }

    public void setResult(String result) {
        this.result.set(result);
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public int getMyPoint() {
        return myPoint.get();
    }

    public void setMyPoint(int myPoint) {
        this.myPoint.set(myPoint);
    }

    public int getOpponentPoint() {
        return opponentPoint.get();
    }

    public void setOpponentPoint(int opponentPoint) {
        this.opponentPoint.set(opponentPoint);
    }

    public int getPointForWin() {
        return pointForWin.get();
    }

    public void setPointForWin(int pointForWin) {
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

    public double getLevel() {
        return level.get();
    }

    public void setLevel(double level) {
        this.level.set(level);
    }
}
