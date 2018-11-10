package ge.ai.domino.domain.game;

import ge.ai.domino.domain.channel.Channel;

import java.io.Serializable;

public class GameProperties implements Serializable {

    private String opponentName;

    private Channel channel;

    private int pointsForWin;

    private int level;

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

    public int getPointsForWin() {
        return pointsForWin;
    }

    public void setPointsForWin(int pointsForWin) {
        this.pointsForWin = pointsForWin;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
