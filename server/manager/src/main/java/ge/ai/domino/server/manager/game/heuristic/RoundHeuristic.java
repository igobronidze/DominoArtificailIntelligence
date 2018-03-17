package ge.ai.domino.server.manager.game.heuristic;

import ge.ai.domino.domain.game.Round;

public interface RoundHeuristic {

    float getHeuristic(Round round);
}
