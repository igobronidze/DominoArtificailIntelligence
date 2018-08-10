package ge.ai.domino.domain.played;

import ge.ai.domino.domain.channel.Channel;

public class GroupedPlayedGame {

    private String version;

    private String opponentName;

    private Channel channel;

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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
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
