package ge.ai.domino.caching.function;

import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.HashMap;
import java.util.Map;

public class CachedFunctions {

	private static final Map<String, PolynomialSplineFunction> opponentPlayHeuristicsDiffsFunctions = new HashMap<>();

	public static PolynomialSplineFunction getOpponentPlayHeuristicsDiffsFunction(String name) {
		return opponentPlayHeuristicsDiffsFunctions.get(name);
	}

	public static void putOpponentPlayHeuristicsDiffsFunctions(Map<String, PolynomialSplineFunction> functions) {
		opponentPlayHeuristicsDiffsFunctions.putAll(functions);
	}
}
