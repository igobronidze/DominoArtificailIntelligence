package ge.ai.domino.server.manager.game.move;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.helper.GameOperations;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.game.minmax.MinMax;
import ge.ai.domino.server.manager.game.validator.OpponentTilesValidator;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;

import java.util.Map;

public class AddForMeProcessor extends MoveProcessor {

	private final SystemParameterManager sysParamManager = new SystemParameterManager();

	private final SysParam minMaxOnFirstTile = new SysParam("minMaxOnFirstTile", "false");

	@Override
	public Round move(Round round, Move move, boolean virtual) throws DAIException {
		// Logging
		if (virtual) {
			GameLoggingProcessor.logInfoAboutMove("<<<<<<<Virtual Mode>>>>>>>", true);
		} else {
			GameLoggingProcessor.logInfoAboutMove("<<<<<<<Real Mode<<<<<<<", false);
		}
		int gameId = round.getGameInfo().getGameId();
		GameLoggingProcessor.logInfoAboutMove("Start addTileForMe method for tile [" + move.getLeft() + "-" + move.getRight() + "], gameId[" + gameId + "]", virtual);
		TableInfo tableInfo = round.getTableInfo();

		// If omit -> a) If opponent also has omitted finish b) Make opponent try
		if (tableInfo.getBazaarTilesCount() == 2) {
			tableInfo.setOmittedMe(true);
			if (tableInfo.isOmittedOpponent()) {
				round.getTableInfo().setNeedToAddLeftTiles(true);
				return round;
			} else {
				round.getTableInfo().setMyMove(false);
				return round;
			}
		}

		// Add for mer
		Tile tile = new Tile(move.getLeft(), move.getRight());
		round.getMyTiles().add(tile);

		// Delete for opponent and produce probability
		Map<Tile, Float> opponentTiles = round.getOpponentTiles();
		float prob = opponentTiles.get(tile);
		opponentTiles.remove(tile);
		GameOperations.distributeProbabilitiesOpponentProportional(opponentTiles, prob);

		tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);

		// Execute MinMax
		MinMax minMax = new MinMax();
		if (tableInfo.getLeft() == null && round.getMyTiles().size() == 7) {
			if (CachedGames.isOpponentNextRoundBeginner(gameId) && !virtual) {
				round.getTableInfo().setMyMove(false);
			}
			if (sysParamManager.getBooleanParameterValue(minMaxOnFirstTile) && !round.getTableInfo().isFirstRound() && round.getTableInfo().isMyMove() && !virtual) {
//				Move aiPrediction = minMax.minMax(round);  TODO
//				round.setAiPrediction(aiPrediction);
			}
		} else if (round.getTableInfo().getLeft() != null && !virtual) {
//			Move aiPrediction = minMax.minMax(round);   TODO
//			round.setAiPrediction(aiPrediction);
		}

		GameLoggingProcessor.logInfoAboutMove("Added tile for me, gameId[" + gameId + "]", virtual);
		GameLoggingProcessor.logRoundFullInfo(round, virtual);

		OpponentTilesValidator.validateOpponentTiles(round, 0, "addTileForMe");
		return round;
	}
}
