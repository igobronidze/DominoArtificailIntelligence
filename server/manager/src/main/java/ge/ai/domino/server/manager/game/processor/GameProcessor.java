package ge.ai.domino.server.manager.game.processor;

import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.GameHelper;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.util.InitialUtil;
import org.apache.log4j.Logger;

public class GameProcessor {

    private static final Logger logger = Logger.getLogger(GameProcessor.class);

    /**
     * ახალი თამაშის დაწყება
     * @param gameProperties თამაშის პარამეტრები
     * @param gameId თუ ძველი თამაშის პარამეტრებით იწყება ახალი, ვიყენებთ ძელის id-ს
     * @return ახალი თამაში
     */
    public Round startGame(GameProperties gameProperties, int gameId) {
        logger.info("Started prepare new game");
        if (gameProperties == null) {
            gameProperties = CachedGames.getGame(gameId).getProperties();
        }
        Game game = InitialUtil.getInitialGame(gameProperties);
        CachedGames.addGame(game);
        CachedGames.addGameHistory(game.getId());
        logger.info("------------Started new game[" + game.getId() + "]------------");
        GameLoggingProcessor.logRoundFullInfo(game.getCurrRound(), false);
        return game.getCurrRound();
    }

    /**
     * წინა ხელი(Ctrl+Z)
     * @param round არსებული ხელი
     * @return ერთი სვლით წინა ხელი
     */
    public Round getLastPlayedRound(Round round) {
        int gameId = round.getGameInfo().getGameId();
        logger.info("Start get last game round method, gameId[" + gameId + "]");
        Game game = CachedGames.getGame(round.getGameInfo().getGameId());
        if (game.getHistory().isEmpty()) {
            logger.warn("There is not any round in history, gameId[" + gameId + "]");
            return round;
        }
        CachedGames.removeLastMove(gameId);
        logger.info("Undo last game round, gameId[" + gameId + "]");
        return game.getHistory().poll();
    }

    public Round addLeftTiles(Round round, int opponentTilesCount) {
        GameInfo gameInfo = round.getGameInfo();
        int gameId = gameInfo.getGameId();
        logger.info("Start add left tile method, gameId[" + gameId + "]");
        round.getTableInfo().setNeedToAddLeftTiles(false);
        TableInfo tableInfo = round.getTableInfo();
        if (tableInfo.isOmittedMe() && tableInfo.isOmittedOpponent()) {
            int myTilesCount = GameHelper.countLeftTiles(round, true, false);
            if (myTilesCount < opponentTilesCount) {
                GameHelper.addLeftTiles(gameInfo, opponentTilesCount, true, gameId, false);
                logger.info("Added lef tiles for me, gameId[" + gameId + "], count[" + opponentTilesCount + "]");
            } else if (myTilesCount > opponentTilesCount) {
                GameHelper.addLeftTiles(gameInfo, myTilesCount, false, gameId, false);
                logger.info("Added lef tiles for opponent, gameId[" + gameId + "], count[" + myTilesCount + "]");
            } else {
                CachedGames.addPointFromLastRound(gameId, myTilesCount);
                logger.info("Added last round tiles count in cach, gameId[" + gameId + "], count[" + myTilesCount + "]");
            }
            return GameHelper.finishedLastAndGetNewRound(round, round.getTableInfo().isMyMove(), false, false);
        } else {
            GameHelper.addLeftTiles(gameInfo, opponentTilesCount, true, gameId, false);
            logger.info("Added lef tiles for me, gameId[" + gameId + "], count[" + opponentTilesCount + "]");
            return GameHelper.finishedLastAndGetNewRound(round, true, false, false);
        }
    }
}
