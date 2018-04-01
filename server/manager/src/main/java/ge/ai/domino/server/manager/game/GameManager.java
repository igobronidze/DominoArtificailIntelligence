package ge.ai.domino.server.manager.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.*;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.helper.GameOperations;
import ge.ai.domino.server.manager.game.helper.InitialUtil;
import ge.ai.domino.server.manager.game.helper.MoveHelper;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.game.move.AddForMeProcessor;
import ge.ai.domino.server.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.server.manager.game.move.MoveProcessor;
import ge.ai.domino.server.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.server.manager.game.move.PlayForOpponentProcessor;
import ge.ai.domino.server.manager.game.validator.MoveValidator;
import ge.ai.domino.server.manager.game.validator.OpponentTilesValidator;
import org.apache.log4j.Logger;

public class GameManager {

    private static final Logger logger = Logger.getLogger(GameManager.class);

    private final OpponentTilesValidator opponentTilesValidator = new OpponentTilesValidator();

    private final MoveProcessor playForMeProcessor = new PlayForMeProcessor(opponentTilesValidator);

    private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor(opponentTilesValidator);

    private final MoveProcessor addForMeProcessor = new AddForMeProcessor(opponentTilesValidator);

    private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor(opponentTilesValidator);

    public Round startGame(GameProperties gameProperties, int gameIdWithSameProperties) {
        logger.info("Preparing new game");
        Game game = InitialUtil.getInitialGame(gameProperties, gameIdWithSameProperties);
        CachedGames.addGame(game);
        CachedGames.addMove(game.getId(), MoveHelper.getStartNewRoundMove());
        logger.info("------------Started new game[" + game.getId() + "]------------");
        Round newRound = CachedGames.getCurrentRound(game.getId(), false);
        GameLoggingProcessor.logRoundFullInfo(newRound, false);
        return newRound;
    }

    public Round addTileForMe(int gameId, int left, int right) throws DAIException {
        Round round = CachedGames.getCurrentRound(gameId, true);
        Move move = getMove(left, right, MoveDirection.LEFT);
        Round newRound = addForMeProcessor.move(round, move, false);
        CachedGames.addRound(gameId, newRound);
        if (round.getTableInfo().getLeft() == null && round.getMyTiles().size() == 1) {
            CachedGames.addMove(gameId, MoveHelper.getAddInitialTileForMeMove(move));
        } else {
            CachedGames.addMove(gameId, round.getTableInfo().getRoundBlockingInfo().isOmitMe() ? MoveHelper.getOmittedMeMove() : MoveHelper.getAddTileForMeMove(move));
        }
        return newRound;
    }

    public Round addTileForOpponent(int gameId) throws DAIException {
        Round round = CachedGames.getCurrentRound(gameId, true);
        Round newRound = addForOpponentProcessor.move(round, getMove(0, 0, MoveDirection.LEFT), false);
        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, round.getTableInfo().getRoundBlockingInfo().isOmitOpponent() ? MoveHelper.getOmittedOpponentMove() : MoveHelper.getAddTileForOpponentMove());
        return newRound;
    }

    public Round playForMe(int gameId, Move move) throws DAIException {
        move = getMove(move);
        Round round = CachedGames.getCurrentRound(gameId, true);
        MoveValidator.validateMove(round, move);
        round.setAiPrediction(null);
        Round newRound = playForMeProcessor.move(round, move, false);
        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, MoveHelper.getPlayForMeMove(move));
        return newRound;
    }

    public Round playForOpponent(int gameId, Move move) throws DAIException {
        move = getMove(move);
        Round round = CachedGames.getCurrentRound(gameId, true);
        MoveValidator.validateMove(round, move);
        Round newRound = playForOpponentProcessor.move(round, move, false);
        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, MoveHelper.getPlayForOpponentMove(move));
        return newRound;
    }

    public Round getLastPlayedRound(int gameId) throws DAIException {
        logger.info("Start getLastPlayedRound method, gameId[" + gameId + "]");
        Round newRound = CachedGames.getAndRemoveLastRound(gameId);
        CachedGames.removeLastMove(gameId);
        logger.info("Undo last game round, gameId[" + gameId + "]");
        return newRound;
    }

    public void specifyRoundBeginner(int gameId, boolean startMe) {
        if (!startMe) {
            CachedGames.makeOpponentNextRoundBeginner(gameId);
        }
    }

    public void specifyOpponentLeftTiles(int gameId, int leftTilesCount) {
        CachedGames.specifyOpponentLeftTilesCount(gameId, leftTilesCount);
    }

    private Move getMove(int left, int right, MoveDirection direction) {
        return new Move(Math.max(left, right), Math.min(left, right), direction);
    }

    private Move getMove(Move move) {
        return new Move(Math.max(move.getLeft(), move.getRight()), Math.min(move.getLeft(), move.getRight()), move.getDirection());
    }
}
