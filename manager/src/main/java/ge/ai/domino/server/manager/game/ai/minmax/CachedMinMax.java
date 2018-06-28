package ge.ai.domino.server.manager.game.ai.minmax;

import java.util.HashMap;
import java.util.Map;

public class CachedMinMax {

	private static Map<Long, NodeRound> lastNodeRounds = new HashMap<>();

	private static Map<Long, Boolean> needChangesMap = new HashMap<>();

	public static void setLastNodeRound(long gameId, NodeRound nodeRound, boolean needChange) {
		lastNodeRounds.put(gameId, nodeRound);
		needChangesMap.put(gameId, needChange);
	}

	public static NodeRound getNodeRound(long gameId) {
		return lastNodeRounds.get(gameId);
	}

	public static boolean needChange(long gameId) {
		return needChangesMap.get(gameId);
	}
}
