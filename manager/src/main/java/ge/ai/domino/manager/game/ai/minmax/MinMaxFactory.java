package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.minmax.bfs.MinMaxBFS;
import ge.ai.domino.manager.game.ai.minmax.bfs.MultiProcessorMinMaxBFS;
import ge.ai.domino.manager.game.ai.minmax.dfs.MinMaxDFS;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

public class MinMaxFactory {

	private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private static final SysParam minMaxType = new SysParam("minMaxType", "DFS");

	private static final SysParam useMultiProcessorMinMax = new SysParam("useMultiProcessorMinMax", "true");

	public static MinMax getMinMax(boolean useMultiProcessor) {
		String type = systemParameterManager.getStringParameterValue(minMaxType);

		MinMax minMaxDFS = new MinMaxDFS();
		MinMax minMaxBFS = new MinMaxBFS();
		MultiProcessorMinMaxBFS multiProcessorMinMaxBFS = new MultiProcessorMinMaxBFS();

		MinMax result = minMaxBFS;

		if (type.equals(minMaxDFS.getType())) {
			result = minMaxDFS;
		} else if (type.equals(minMaxBFS.getType())) {
			if (useMultiProcessor && systemParameterManager.getBooleanParameterValue(useMultiProcessorMinMax)) {
				return multiProcessorMinMaxBFS;
			} else {
				result = minMaxBFS;
			}
		}

		return result;
	}
}
