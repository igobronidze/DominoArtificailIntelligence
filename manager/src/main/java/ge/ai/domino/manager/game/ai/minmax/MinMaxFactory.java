package ge.ai.domino.manager.game.ai.minmax;

import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.minmax.bfs.MinMaxBFS;
import ge.ai.domino.manager.game.ai.minmax.dfs.MinMaxDFS;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

public class MinMaxFactory {

	private static final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private static final SysParam minMaxType = new SysParam("minMaxType", "DFS");

	public static MinMax getMinMax() {
		String type = systemParameterManager.getStringParameterValue(minMaxType);

		MinMax minMaxDFS = new MinMaxDFS();
		MinMax minMaxBFS = new MinMaxBFS();

		if (type.equals(minMaxDFS.getType())) {
			return minMaxDFS;
		} else if (type.equals(minMaxBFS.getType())) {
			return minMaxBFS;
		}
		return minMaxBFS;
	}
}