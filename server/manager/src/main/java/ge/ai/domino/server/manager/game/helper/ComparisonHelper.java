package ge.ai.domino.server.manager.game.helper;

import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;

public class ComparisonHelper {

	private static final SystemParameterManager sysParamManager = new SystemParameterManager();

	private static final SysParam epsilonForProbabilities = new SysParam("epsilonForProbabilities", "0.00001");

	public static boolean equal(float firstNum, float secondNum) {
		return Math.abs(firstNum - secondNum) < sysParamManager.getFloatParameterValue(epsilonForProbabilities);
	}
}
