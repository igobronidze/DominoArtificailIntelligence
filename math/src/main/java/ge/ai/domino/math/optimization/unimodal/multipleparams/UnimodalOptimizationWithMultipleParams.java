package ge.ai.domino.math.optimization.unimodal.multipleparams;

import ge.ai.domino.math.optimization.OptimizationDirection;
import ge.ai.domino.math.optimization.unimodal.oneparam.IntervalDivisionUnimodalOptimization;
import ge.ai.domino.math.optimization.unimodal.oneparam.UnimodalOptimization;
import ge.ai.domino.math.optimization.unimodal.oneparam.UnimodalOptimizationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class UnimodalOptimizationWithMultipleParams {

    private UnimodalOptimizationType unimodalOptimizationType;

    private OptimizationDirection optimizationDirection;

    public UnimodalOptimizationWithMultipleParams(UnimodalOptimizationType unimodalOptimizationType, OptimizationDirection optimizationDirection) {
        this.unimodalOptimizationType = unimodalOptimizationType;
        this.optimizationDirection = optimizationDirection;
    }

    public List<Double> getExtremaVector(List<Double> params, List<ParamInterval> paramIntervals, int iteration) {
        int index = new Random().nextInt(params.size());
        switch (unimodalOptimizationType) {
            case INTERVAL_DIVISION:
                default:
                    UnimodalOptimization unimodalOptimization = new IntervalDivisionUnimodalOptimization(optimizationDirection) {
                        @Override
                        public double getValue(double x) {
                            List<Double> paramsClone = new ArrayList<>(params);
                            paramsClone.set(index, x);
                            return UnimodalOptimizationWithMultipleParams.this.getValue(paramsClone);
                        }
                    };
                    params.set(index, unimodalOptimization.getExtremaPoint(paramIntervals.get(index).getLeft(), paramIntervals.get(index).getRight(), iteration));
                    return params;
        }
    }

    public abstract double getValue(List<Double> params);
}
