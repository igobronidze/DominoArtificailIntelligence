package ge.ai.domino.math.optimization.unimodal.oneparam;

import ge.ai.domino.math.optimization.Optimization;
import ge.ai.domino.math.optimization.OptimizationDirection;

public abstract class UnimodalOptimization extends Optimization {

    public UnimodalOptimization(OptimizationDirection optimizationDirection) {
        super(optimizationDirection);
    }

    public abstract double getExtremaPoint(double a, double b, int iteration);

    public abstract double getValue(double x);
}
