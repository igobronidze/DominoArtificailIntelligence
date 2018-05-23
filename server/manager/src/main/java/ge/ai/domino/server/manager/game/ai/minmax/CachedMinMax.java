package ge.ai.domino.server.manager.game.ai.minmax;

import java.util.HashMap;
import java.util.Map;

public class CachedMinMax {

	private static Map<Long, NodeRound> lastNodeRounds = new HashMap<>();

	public static void setLastNodeRound(long gameId, NodeRound nodeRound) {
		lastNodeRounds.put(gameId, nodeRound);
	}

	public static NodeRound getNodeRound(long gameId) {
		return lastNodeRounds.get(gameId);
	}
}
