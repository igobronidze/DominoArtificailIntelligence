package ge.ai.domino.domain.played;

import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.move.Move;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReplayMoveInfo {

	private int gameId;

	private PlayedMove previousMove;

	private PlayedMove nextMove;

	private int moveIndex;

	private Move bestAiPrediction;

	private List<AiPrediction> aiPredictions;

	private Round round;

	private double heuristicValue;
}
