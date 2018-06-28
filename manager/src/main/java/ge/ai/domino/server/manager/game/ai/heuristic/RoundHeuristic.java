package ge.ai.domino.server.manager.game.ai.heuristic;

import ge.ai.domino.domain.game.Round;

public interface RoundHeuristic {

    double getHeuristic(Round round);
}
