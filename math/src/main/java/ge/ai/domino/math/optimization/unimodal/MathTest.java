package ge.ai.domino.math.optimization.unimodal;

import ge.ai.domino.math.optimization.OptimizationDirection;
import ge.ai.domino.math.optimization.unimodal.multipleparams.ParamInterval;
import ge.ai.domino.math.optimization.unimodal.multipleparams.UnimodalOptimizationWithMultipleParams;
import ge.ai.domino.math.optimization.unimodal.oneparam.UnimodalOptimizationType;

import java.util.Arrays;
import java.util.List;

public class MathTest {

    public static void main(String[] args) {
        UnimodalOptimizationWithMultipleParams unimodalOptimizationWithMultipleParams =
                new UnimodalOptimizationWithMultipleParams(UnimodalOptimizationType.INTERVAL_DIVISION, OptimizationDirection.MAX) {
                    @Override
                    public double getValue(List<Double> params) {
                        return f(params);
                    }
                };
        List<Double> params = getInitValues();
        for (int i = 0; i < 5; i++) {
            params = unimodalOptimizationWithMultipleParams.getExtremaVector(params, getParamIntervals(),   3);
        }
        System.out.println(params);
    }

    private static double f(List<Double> params) {
        return params.get(0) * params.get(1);
    }

    private static List<Double> getInitValues() {
        return Arrays.asList(1.0, 0.5);
    }

    private static List<ParamInterval> getParamIntervals() {
        return Arrays.asList(new ParamInterval(-3, 5), new ParamInterval(-2, 6));
    }
}
