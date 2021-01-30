package ge.ai.domino.util.random;

import java.util.Random;

public class RandomUtils {

    public static int getRandomBetween(int from, int to) {
        return from + new Random().nextInt(to - from);
    }

    public static boolean getBooleanByProbability(double probability) {
        return new Random().nextDouble() < probability;
    }
}
