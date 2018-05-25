package ge.ai.domino.server.caching.function;

import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class CachedFunctions {

	private static PolynomialSplineFunction opponentPlayHeuristicsDiffsFunction;

	public static PolynomialSplineFunction getOpponentPlayHeuristicsDiffsFunction() {
		return opponentPlayHeuristicsDiffsFunction;
	}

	public static void setOpponentPlayHeuristicsDiffsFunction(PolynomialSplineFunction function) {
		opponentPlayHeuristicsDiffsFunction = function;
	}
}
