package ge.ai.domino.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.ai.minmax.MinMaxFactory;
import ge.ai.domino.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.game.helper.game.ProbabilitiesDistributor;
import ge.ai.domino.manager.game.logging.RoundLogger;

public class PlayForMeProcessor extends MoveProcessor {

	@Override
	public Round move(Round round, Move move) throws DAIException {
		boolean firstMove = round.getTableInfo().getLeft() == null;

		round.getTableInfo().getRoundBlockingInfo().setOmitMe(false);
		if (move.getRight() != move.getLeft()) {
			round.getTableInfo().getRoundBlockingInfo().setLastNotTwinPlayedTileMy(true);
		}
		int gameId = round.getGameInfo().getGameId();
		logger.info("Start playForMe method for tile [" + move.getLeft() + "-" + move.getRight() + "] direction ["
				+ move.getDirection().name() + "], gameId[" + gameId + "]");

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
			round = GameOperations.finishedLastAndGetNewRound(round, true, GameOperations.countLeftTiles(round, false, false), false);
		} else if (new MinMaxPredictor().usePredictor()) {
			if (firstMove && CachedMinMax.getCachePrediction(gameId) == null) {
				MinMaxFactory.getMinMax(true).minMaxForCachedNodeRound(round);
			}
		}

		logger.info("Played tile for me, gameId[" + gameId + "]");
		RoundLogger.logRoundFullInfo(round);

		return round;
	}
}
