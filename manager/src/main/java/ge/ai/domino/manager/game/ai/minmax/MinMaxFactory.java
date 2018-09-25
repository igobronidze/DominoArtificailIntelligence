package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.minmax.bfs.MinMaxBFS;
import ge.ai.domino.manager.game.ai.minmax.dfs.MinMaxDFS;
import ge.ai.domino.manager.multithreadingserver.Server;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

public class MinMaxFactory {

	private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private static final SysParam minMaxType = new SysParam("minMaxType", "DFS");

	private static final SysParam useMultithreadingMinMax = new SysParam("useMultithreadingMinMax", "true");

	public static MinMax getMinMax(boolean useMultithreading) {
		String type = systemParameterManager.getStringParameterValue(minMaxType);

		MinMax minMaxDFS = new MinMaxDFS();
		MinMax minMaxBFS = new MinMaxBFS();

		MinMax result = minMaxBFS;

		if (type.equals(minMaxDFS.getType())) {
			result = minMaxDFS;
		} else if (type.equals(minMaxBFS.getType())) {
			result = minMaxBFS;
		}

		if (useMultithreading && systemParameterManager.getBooleanParameterValue(useMultithreadingMinMax) && Server.getInstance().getClientsCount() != 0) {
			result.setMultithreadingMinMax(true);
			result = new MultithreadedMinMax(result);
		}

		return result;
	}
}
