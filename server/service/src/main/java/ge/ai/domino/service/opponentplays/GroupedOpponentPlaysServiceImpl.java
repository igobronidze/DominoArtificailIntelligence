package ge.ai.domino.service.opponentplays;

import ge.ai.domino.domain.game.opponentplay.GroupedOpponentPlay;
import ge.ai.domino.server.manager.opponentplay.OpponentPlaysManager;

import java.util.List;

public class GroupedOpponentPlaysServiceImpl implements GroupedOpponentPlaysService {

	private final OpponentPlaysManager opponentPlaysManager = new OpponentPlaysManager();

	@Override
	public List<GroupedOpponentPlay> getGroupedOpponentPlays(Integer gameId, String version, boolean groupByGame, boolean groupByVersion) {
		return opponentPlaysManager.getGroupedOpponentPlays(gameId, version, groupByGame, groupByVersion);
	}
}
