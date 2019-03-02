package ge.ai.domino.service.opponentplays;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.opponentplay.GroupedOpponentPlay;

import java.util.List;

public interface GroupedOpponentPlaysService {

	List<GroupedOpponentPlay> getGroupedOpponentPlays(Integer gameId, String version, boolean groupByGame, boolean groupByVersion, boolean groupInOneResult) throws DAIException;
}
