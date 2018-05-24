package ge.ai.domino.server.manager.function;

import ge.ai.domino.domain.function.FunctionArgsAndValues;
import ge.ai.domino.server.caching.function.CachedFunctions;
import ge.ai.domino.server.dao.function.FunctionDAO;
import ge.ai.domino.server.dao.function.FunctionDAOImpl;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

public class FunctionManager {

	private static final String OPPONENT_PLAY_HEURISTICS_DIFFS_FUNCTION_NAME = "opponentPlayHeuristicsDiffsFunction";

	private FunctionDAO functionDAO = new FunctionDAOImpl();

	public void initFunctions() {
		FunctionArgsAndValues opponentPlayHeuristicsDiffsFunctionArgsAndValues = functionDAO.getFunctionArgsAndValues(OPPONENT_PLAY_HEURISTICS_DIFFS_FUNCTION_NAME);
		CachedFunctions.setOpponentPlayHeuristicsDiffsFunction(getPolynomialFunctionLagrangeForm(opponentPlayHeuristicsDiffsFunctionArgsAndValues));
	}

	public double getOpponentPlayHeuristicsDiffsFunctionValue(double x) {
		return CachedFunctions.getOpponentPlayHeuristicsDiffsFunction().value(x);
	}

	private PolynomialFunctionLagrangeForm getPolynomialFunctionLagrangeForm(FunctionArgsAndValues functionArgsAndValues) {
		double [] args = new double[functionArgsAndValues.getArgs().size()];
		for (int i = 0; i < functionArgsAndValues.getArgs().size(); i++) {
			args[i] = functionArgsAndValues.getArgs().get(i);
		}
		double [] values = new double[functionArgsAndValues.getValues().size()];
		for (int i = 0; i < functionArgsAndValues.getValues().size(); i++) {
			values[i] = functionArgsAndValues.getValues().get(i);
		}
		return new PolynomialFunctionLagrangeForm(args, values);
	}
}
