package ge.ai.domino.server.manager.game.ai.minmax;

import java.util.HashMap;
import java.util.Map;

public class CachedMinMax {

	private static Map<Integer, NodeRound> lastNodeRounds = new HashMap<>();

	private static Map<Integer, Boolean> needChangesMap = new HashMap<>();

	private static Map<Integer, Boolean> minMaxInProgressMap = new HashMap<>();

	public static void setLastNodeRound(int gameId, NodeRound nodeRound, boolean needChange) {
		lastNodeRounds.put(gameId, nodeRound);
		needChangesMap.put(gameId, needChange);
	}

	public static NodeRound getNodeRound(int gameId) {
		return lastNodeRounds.get(gameId);
	}

	public static boolean needChange(int gameId) {
		return needChangesMap.get(gameId);
	}

	public static void changeMinMaxInProgress(int gameId, boolean inProgress) {
		minMaxInProgressMap.put(gameId, inProgress);
	}

	public static boolean isMinMaxInProgress(int gameId) {
		return minMaxInProgressMap.get(gameId);
	}
}
