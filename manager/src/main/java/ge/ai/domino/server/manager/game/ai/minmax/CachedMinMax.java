package ge.ai.domino.server.manager.game.ai.minmax;

import java.util.HashMap;
import java.util.Map;

public class CachedMinMax {

	private static Map<Integer, NodeRound> lastNodeRounds = new HashMap<>();

	private static Map<Integer, Boolean> needChangesMap = new HashMap<>();

	private static Map<Integer, Boolean> minMaxInProgressMap = new HashMap<>();

	private static Map<Integer, Boolean> useFirstChildMap = new HashMap<>();

	public synchronized static void setLastNodeRound(int gameId, NodeRound nodeRound, boolean needChange) {
		lastNodeRounds.put(gameId, nodeRound);
		needChangesMap.put(gameId, needChange);
	}

	public synchronized static NodeRound getNodeRound(int gameId) {
		return lastNodeRounds.get(gameId);
	}

	public synchronized static boolean needChange(int gameId) {
		return needChangesMap.get(gameId) == null ? false : needChangesMap.get(gameId);
	}

	public synchronized static void changeMinMaxInProgress(int gameId, boolean inProgress) {
		minMaxInProgressMap.put(gameId, inProgress);
	}

	public synchronized static boolean isMinMaxInProgress(int gameId) {
		return minMaxInProgressMap.get(gameId) == null ? false : minMaxInProgressMap.get(gameId);
	}

	public synchronized static void changeUseFirstChild(int gameId, boolean useFirstChild) {
		useFirstChildMap.put(gameId, useFirstChild);
	}

	public synchronized static boolean isUseFirstChild(int gameId) {
		return useFirstChildMap.get(gameId) == null ? false : useFirstChildMap.get(gameId);
	}
}
