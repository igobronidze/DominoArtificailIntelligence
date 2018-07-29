package ge.ai.domino.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;
import org.apache.log4j.Logger;

public interface RoundHeuristic {

    double getHeuristic(Round round, boolean logTrace);

    static void logInfo(Logger logger, String text, boolean log) {
        if (log) {
            logger.info(text);
        }
    }
}
