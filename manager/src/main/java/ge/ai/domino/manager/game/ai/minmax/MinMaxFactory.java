package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.minmax.bfs.MinMaxBFS;
import ge.ai.domino.manager.game.ai.minmax.bfs.MultithreadingMinMaxBFS;
import ge.ai.domino.manager.game.ai.minmax.dfs.MinMaxDFS;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

public class MinMaxFactory {

	private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private static final SysParam minMaxType = new SysParam("minMaxType", "DFS");

	private static final SysParam useMultithreadingMinMax = new SysParam("useMultithreadingMinMax", "true");

	public static MinMax getMinMax(boolean useMultithreading) {
		String type = systemParameterManager.getStringParameterValue(minMaxType);

		MinMax minMaxDFS = new MinMaxDFS();
		MinMax minMaxBFS = new MinMaxBFS();
		MultithreadingMinMaxBFS multithreadingMinMaxBFS = new MultithreadingMinMaxBFS();

		MinMax result = minMaxBFS;

		if (type.equals(minMaxDFS.getType())) {
			result = minMaxDFS;
		} else if (type.equals(minMaxBFS.getType())) {
			if (useMultithreading && systemParameterManager.getBooleanParameterValue(useMultithreadingMinMax)) {
				return multithreadingMinMaxBFS;
			} else {
				result = minMaxBFS;
			}
		}

		return result;
	}
}
