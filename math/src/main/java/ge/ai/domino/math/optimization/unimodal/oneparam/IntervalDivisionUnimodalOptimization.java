package ge.ai.domino.math.optimization.unimodal.oneparam;

import ge.ai.domino.math.optimization.OptimizationDirection;
import org.apache.log4j.Logger;

public abstract class IntervalDivisionUnimodalOptimization extends UnimodalOptimization {

    private static final Logger logger = Logger.getLogger(IntervalDivisionUnimodalOptimization.class);

    public IntervalDivisionUnimodalOptimization(OptimizationDirection optimizationDirection) {
        super(optimizationDirection);
    }

    @Override
    public double getExtremaPoint(double a, double b, int iteration) {
        logger.info("Starting IntervalDivisionUnimodalOptimization getExtremaPoint method, a[" + a + "], b[" + b + "], iteration[" + iteration + "]");
        double l = b - a;
        double xMiddle = (a + b) / 2;
        double xMiddleValue = getRealValue(xMiddle);

        double xLeft, xRight;
        double xLeftValue, xRightValue;
        for (int i = 0 ; i < iteration; i++) {
            logger.info("Starting getExtremaPoint iteration[" + (i + 1) + "]");
            xLeft = a + l / 4;
            xRight = b - l / 4;

            xLeftValue = getRealValue(xLeft);
            if (xLeftValue < xMiddleValue) {
                b = xMiddle;
                xMiddle = xLeft;
                xMiddleValue = xLeftValue;
            } else {
                xRightValue = getRealValue(xRight);
                if (xRightValue < xMiddleValue) {
                    a = xMiddle;
                    xMiddle = xRight;
                    xMiddleValue = xRightValue;
                } else {
                    a = xLeft;
                    b = xRight;
                }
            }

            l = b - a;
        }

        return xMiddle;
    }

    private double getRealValue(double x) {
        if (super.optimizationDirection == OptimizationDirection.MIN) {
            return getValue(x);
        } else {
            return -1 * getValue(x);
        }
    }
}
