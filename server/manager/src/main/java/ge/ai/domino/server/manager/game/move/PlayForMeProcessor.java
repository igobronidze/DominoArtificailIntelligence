package ge.ai.domino.server.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.server.manager.game.ai.minmax.MinMax;
import ge.ai.domino.server.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.server.manager.game.helper.GameOperations;
import ge.ai.domino.server.manager.game.helper.ProbabilitiesDistributor;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;

public class PlayForMeProcessor extends MoveProcessor {

	@Override
	public Round move(Round round, Move move, boolean virtual) throws DAIException {
		// Logging
		GameLoggingProcessor.logInfoAboutMove(virtual ? "<<<<<<<Virtual Mode>>>>>>>" : "<<<<<<<Real Mode<<<<<<<", virtual);
		MoveDirection direction = move.getDirection();

		boolean firstMove = round.getTableInfo().getLeft() == null;

		round.getTableInfo().getRoundBlockingInfo().setOmitMe(false);
		if (move.getRight() != move.getLeft()) {
			round.getTableInfo().getRoundBlockingInfo().setLastNotTwinPlayedTileMy(true);
		}
		int gameId = round.getGameInfo().getGameId();
		GameLoggingProcessor.logInfoAboutMove("Start playForMe method for tile [" + move.getLeft() + "-" + move.getRight() + "] direction [" + direction.name() + "], gameId[" + gameId + "]", virtual);

		// Not played twins case
		if (round.getTableInfo().isFirstRound() && round.getTableInfo().getLeft() == null) {
			double sum = GameOperations.makeTwinTilesAsBazaarAndReturnProbabilitiesSum(round.getOpponentTiles(), (move.getLeft() == move.getRight() ? move.getLeft() : -1));
			ProbabilitiesDistributor.distributeProbabilitiesOpponentProportional(round.getOpponentTiles(), sum);
		}

		// Play tile
		Tile tmpTile = new Tile(move.getLeft(), move.getRight());
		round.getMyTiles().remove(tmpTile);
		GameOperations.playTile(round, move);

		round.getGameInfo().setMyPoint(round.getGameInfo().getMyPoint() + GameOperations.countScore(round));
		round.getTableInfo().setMyMove(false);

		if (round.getMyTiles().size() == 0) {
			round = GameOperations.finishedLastAndGetNewRound(round, true, GameOperations.countLeftTiles(round, false, virtual), virtual);
		}

		if (new MinMaxPredictor().usePredictor()) {
			if (firstMove) {
				new MinMax().minMaxForCachedNodeRound(round);
			}
		}

		GameLoggingProcessor.logInfoAboutMove("Played tile for me, gameId[" + gameId + "]", virtual);
		GameLoggingProcessor.logRoundFullInfo(round, virtual);

		return round;
	}
}
