package ge.ai.domino.server.manager.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.helper.CloneUtil;
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

    private final MoveProcessor playForMeProcessor = new PlayForMeProcessor();

    private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor();

    private final MoveProcessor addForMeProcessor = new AddForMeProcessor();

    private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor();

    public Round startGame(GameProperties gameProperties, int gameId) {
        logger.info("Preparing new game");
        Game game = InitialUtil.getInitialGame(gameProperties, gameId);
        CachedGames.addGame(game);
        logger.info("------------Started new game[" + game.getId() + "]------------");
        Round newRound = CachedGames.getCurrentRound(game.getId());
        GameLoggingProcessor.logRoundFullInfo(newRound, false);
        return newRound;
    }

    public Round addTileForMe(int gameId, int left, int right) throws DAIException {
        Round round = CachedGames.getCurrentRound(gameId);
        Move move = getMove(left, right, MoveDirection.LEFT);
        Round newRound = addForMeProcessor.move(round, move, false);
        CachedGames.addRound(gameId, CloneUtil.getClone(newRound));
        if (round.getTableInfo().getLeft() == null && round.getMyTiles().size() == 1) {
            CachedGames.addMove(gameId, MoveHelper.getAddInitialTileForMeMove(move), true);
        } else {
            CachedGames.addMove(gameId, round.getTableInfo().isOmittedMe() ? MoveHelper.getOmittedMeMove() : MoveHelper.getAddTileForMeMove(move), false);
        }
        return newRound;
    }

    public Round addTileForOpponent(int gameId) throws DAIException {
        Round round = CachedGames.getCurrentRound(gameId);
        Round newRound = addForOpponentProcessor.move(round, getMove(0, 0, MoveDirection.LEFT), false);
        CachedGames.addRound(gameId, CloneUtil.getClone(newRound));
        CachedGames.addMove(gameId, round.getTableInfo().isOmittedOpponent() ? MoveHelper.getOmittedOpponentMove() : MoveHelper.getAddTileForOpponentMove(), false);
        return newRound;
    }

    public Round playForMe(int gameId, Move move) throws DAIException {
        move = getMove(move);
        Round round = CachedGames.getCurrentRound(gameId);
        MoveValidator.validateMove(round, move);
        Round newRound = playForMeProcessor.move(round, move, false);
        CachedGames.addRound(gameId, CloneUtil.getClone(newRound));
        CachedGames.addMove(gameId, MoveHelper.getPlayForMeMove(move),false);
        return newRound;
    }

    public Round playForOpponent(int gameId, Move move) throws DAIException {
        move = getMove(move);
        Round round = CachedGames.getCurrentRound(gameId);
        MoveValidator.validateMove(round, move);
        Round newRound = playForOpponentProcessor.move(round, move, false);
        CachedGames.addRound(gameId, CloneUtil.getClone(newRound));
        CachedGames.addMove(gameId, MoveHelper.getPlayForOpponentMove(move), false);
        return newRound;
    }

    public Round getLastPlayedRound(int gameId) throws DAIException {
        Round round = CachedGames.getCurrentRound(gameId);
        logger.info("Start getLastPlayedRound method, gameId[" + gameId + "]");
        Game game = CachedGames.getGame(round.getGameInfo().getGameId());
        if (game.getRounds().isEmpty()) {
            logger.warn("There is not any round in history, gameId[" + gameId + "]");
            return round;
        }
        CachedGames.removeLastMove(gameId);
        logger.info("Undo last game round, gameId[" + gameId + "]");
        Round newRound = game.getRounds().poll();
        OpponentTilesValidator.validateOpponentTiles(newRound, newRound.getTableInfo().getTilesFromBazaar(), "getLastPlayedRound");
        return newRound;
    }

    public Round addLeftTilesForMe(int gameId, int opponentTilesCount) throws DAIException {
        Round round = CachedGames.getCurrentRound(gameId);
        GameInfo gameInfo = round.getGameInfo();
        logger.info("Start addLeftTileForMe method, gameId[" + gameId + "]");
        round.getTableInfo().setNeedToAddLeftTiles(false);
        TableInfo tableInfo = round.getTableInfo();
        Round newRound;
        if (tableInfo.isOmittedMe() && tableInfo.isOmittedOpponent()) {
            int myTilesCount = GameOperations.countLeftTiles(round, true, false);
            if (myTilesCount < opponentTilesCount) {
                GameOperations.addLeftTiles(gameInfo, opponentTilesCount, true, gameId, false);
                logger.info("Added lef tiles for me, gameId[" + gameId + "], count[" + opponentTilesCount + "]");
            } else if (myTilesCount > opponentTilesCount) {
                GameOperations.addLeftTiles(gameInfo, myTilesCount, false, gameId, false);
                logger.info("Added lef tiles for opponent, gameId[" + gameId + "], count[" + myTilesCount + "]");
            } else {
                CachedGames.addLeftTilesCountFromLastRound(gameId, myTilesCount);
                logger.info("Added last round tiles count in cach, gameId[" + gameId + "], count[" + myTilesCount + "]");
            }
            newRound = GameOperations.finishedLastAndGetNewRound(round, round.getTableInfo().isMyMove(), false, false);
        } else {
            GameOperations.addLeftTiles(gameInfo, opponentTilesCount, true, gameId, false);
            logger.info("Added lef tiles for me, gameId[" + gameId + "], count[" + opponentTilesCount + "]");
            newRound = GameOperations.finishedLastAndGetNewRound(round, true, false, false);
        }
        OpponentTilesValidator.validateOpponentTiles(newRound, 0, "addLeftTilesForMe");
        return newRound;
    }

    public Round specifyRoundBeginner(int gameId, boolean startMe) {
        Round round = CachedGames.getCurrentRound(gameId);
        round.getTableInfo().setMyMove(startMe);
        return round;
    }

    private Move getMove(int left, int right, MoveDirection direction) {
        return new Move(Math.max(left, right), Math.min(left, right), direction);
    }

    private Move getMove(Move move) {
        return new Move(Math.max(move.getLeft(), move.getRight()), Math.min(move.getLeft(), move.getRight()), move.getDirection());
    }
}
