package ge.ai.domino.manager.function;

import ge.ai.domino.domain.function.FunctionArgsAndValues;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.caching.function.CachedFunctions;
import ge.ai.domino.dao.function.FunctionDAO;
import ge.ai.domino.dao.function.FunctionDAOImpl;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class FunctionManager {

	private static final String OPPONENT_PLAY_HEURISTICS_DIFFS_FUNCTION_NAME_PREFIX = "opponentPlayHeuristicsDiffsFunction";

	private final SystemParameterManager systemParameterManager = new SystemParameterManager();

	private final SysParam opponentPlayHeuristicsDiffsFunctionName = new SysParam("opponentPlayHeuristicsDiffsFunctionName", OPPONENT_PLAY_HEURISTICS_DIFFS_FUNCTION_NAME_PREFIX + "_M");

	private final FunctionDAO functionDAO = new FunctionDAOImpl();

	public void initFunctions() {
		CachedFunctions.putOpponentPlayHeuristicsDiffsFunctions(functionDAO.getFunctionArgsAndValues(OPPONENT_PLAY_HEURISTICS_DIFFS_FUNCTION_NAME_PREFIX)
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> getPolynomialFunctionLagrangeForm(e.getValue(), true))));
	}

	public void setFunctions(Map<String, FunctionArgsAndValues> functionArgsAndValuesMap, boolean reverse) {
		CachedFunctions.putOpponentPlayHeuristicsDiffsFunctions(functionArgsAndValuesMap
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> getPolynomialFunctionLagrangeForm(e.getValue(), reverse))));
	}

	public double getOpponentPlayHeuristicsDiffsFunctionValue(double x) {
		return CachedFunctions.getOpponentPlayHeuristicsDiffsFunction(systemParameterManager.getStringParameterValue(opponentPlayHeuristicsDiffsFunctionName)).value(x);
	}

	private PolynomialSplineFunction getPolynomialFunctionLagrangeForm(FunctionArgsAndValues functionArgsAndValues, boolean reverse) {
		if (reverse) {
			Collections.reverse(functionArgsAndValues.getArgs());
			Collections.reverse(functionArgsAndValues.getValues());
		}

		double [] args = new double[functionArgsAndValues.getArgs().size()];
		for (int i = 0; i < functionArgsAndValues.getArgs().size(); i++) {
			args[i] = functionArgsAndValues.getArgs().get(i);
		}
		double [] values = new double[functionArgsAndValues.getValues().size()];
		for (int i = 0; i < functionArgsAndValues.getValues().size(); i++) {
			values[i] = functionArgsAndValues.getValues().get(i);
		}

		LinearInterpolator linearInterpolator = new LinearInterpolator();
		return linearInterpolator.interpolate(args, values);
	}
}
