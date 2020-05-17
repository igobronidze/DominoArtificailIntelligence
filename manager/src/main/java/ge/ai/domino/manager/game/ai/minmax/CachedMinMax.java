package ge.ai.domino.manager.game.ai.minmax;

import java.util.HashMap;
import java.util.Map;

public class CachedMinMax {

	private static final Map<Integer, Boolean> needChangesMap = new HashMap<>();

	private static final Map<Integer, Boolean> minMaxInProgressMap = new HashMap<>();

	private static final Map<Integer, Boolean> useFirstChildMap = new HashMap<>();

	private static final Map<Integer, CachedPrediction> cachedPredictionMap = new HashMap<>();

	public static void cleanUp(int gameId) {
		needChangesMap.remove(gameId);
		minMaxInProgressMap.remove(gameId);
		useFirstChildMap.remove(gameId);
		cachedPredictionMap.remove(gameId);
	}

	public synchronized static void setCachedPrediction(int gameId, CachedPrediction cachedPrediction, boolean needChange) {
		cachedPredictionMap.put(gameId, cachedPrediction);
		needChangesMap.put(gameId, needChange);
	}

	public synchronized static CachedPrediction getCachePrediction(int gameId) {
		return cachedPredictionMap.get(gameId);
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
