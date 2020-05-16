package ge.ai.domino.domain.game;

import ge.ai.domino.domain.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameFromLog {

	private int gameId;

	private Channel channel;

	private int pointForWin;

	private List<Round> rounds = new ArrayList<>();
}
