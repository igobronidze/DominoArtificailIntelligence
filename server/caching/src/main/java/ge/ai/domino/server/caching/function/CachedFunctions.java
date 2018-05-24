package ge.ai.domino.server.caching.function;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

public class CachedFunctions {

	private static PolynomialFunctionLagrangeForm opponentPlayHeuristicsDiffsFunction;

	public static PolynomialFunctionLagrangeForm getOpponentPlayHeuristicsDiffsFunction() {
		return opponentPlayHeuristicsDiffsFunction;
	}

	public static void setOpponentPlayHeuristicsDiffsFunction(PolynomialFunctionLagrangeForm function) {
		opponentPlayHeuristicsDiffsFunction = function;
	}
}
