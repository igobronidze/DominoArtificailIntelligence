package ge.ai.domino.domain.game;

import ge.ai.domino.domain.channel.Channel;

import java.util.ArrayList;
import java.util.List;

public class GameFromLog {

	private int gameId;

	private Channel channel;

	private int pointForWin;

	private List<Round> rounds = new ArrayList<>();

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public int getPointForWin() {
		return pointForWin;
	}

	public void setPointForWin(int pointForWin) {
		this.pointForWin = pointForWin;
	}

	public List<Round> getRounds() {
		return rounds;
	}

	public void setRounds(List<Round> rounds) {
		this.rounds = rounds;
	}
}
