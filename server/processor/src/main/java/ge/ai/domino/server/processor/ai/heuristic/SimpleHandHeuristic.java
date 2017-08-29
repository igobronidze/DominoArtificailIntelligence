package ge.ai.domino.server.processor.ai.heuristic;

import ge.ai.domino.domain.ai.AIExtraInfo;
import ge.ai.domino.domain.domino.Hand;

public class SimpleHandHeuristic implements HandHeuristic {

    @Override
    public double getHeuristicValue(Hand hand) {
        AIExtraInfo aiExtraInfo = hand.getAiExtraInfo();
        return aiExtraInfo.getMyPoints() - aiExtraInfo.getHimPoints();
    }
}
