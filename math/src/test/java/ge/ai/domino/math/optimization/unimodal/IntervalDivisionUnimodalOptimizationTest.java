package ge.ai.domino.math.optimization.unimodal;

import ge.ai.domino.math.optimization.OptimizationDirection;
import ge.ai.domino.math.optimization.unimodal.oneparam.IntervalDivisionUnimodalOptimization;
import ge.ai.domino.math.optimization.unimodal.oneparam.UnimodalOptimization;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntervalDivisionUnimodalOptimizationTest {

    private static UnimodalOptimization unimodalOptimization;

    private static final int iteration = 3;

    private static final int a = 96;

    private static final int b = 102;

    private static final double extremaPoint = 100.125;

    @BeforeClass
    public static void init() {
        unimodalOptimization = new IntervalDivisionUnimodalOptimization(OptimizationDirection.MIN) {
            @Override
            public double getValue(double x) {
                return (100 - x) * (100 - x);
            }
        };
    }

    @Test
    public void testGetExtremaPoint() {
        Assert.assertEquals(extremaPoint, unimodalOptimization.getExtremaPoint(a, b, iteration), 0.0);
    }
}
