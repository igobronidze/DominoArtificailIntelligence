package ge.ai.domino.server.manager.domino.processor;

import ge.ai.domino.domain.domino.game.Game;
import ge.ai.domino.domain.domino.game.GameInfo;
import ge.ai.domino.domain.domino.game.GameProperties;
import ge.ai.domino.domain.domino.game.Hand;
import ge.ai.domino.domain.domino.game.TableInfo;
import ge.ai.domino.server.caching.domino.CachedDominoGames;
import ge.ai.domino.server.manager.domino.DominoHelper;
import ge.ai.domino.server.manager.domino.logging.DominoLoggingProcessor;
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
    public Hand startGame(GameProperties gameProperties, int gameId) {
        logger.info("Started prepare new game");
        if (gameProperties == null) {
            gameProperties = CachedDominoGames.getGame(gameId).getGameProperties();
        }
        Game game = InitialUtil.getInitialGame(gameProperties);
        CachedDominoGames.addGame(game);
        logger.info("------------Started new game[" + game.getId() + "]------------");
        DominoLoggingProcessor.logHandFullInfo(game.getCurrHand(), false);
        return game.getCurrHand();
    }

    /**
     * წინა ხელი(Ctrl+Z)
     * @param hand არსებული ხელი
     * @return ერთი სვლით წინა ხელი
     */
    public Hand getLastPlayedHand(Hand hand) {
        int gameId = hand.getGameInfo().getGameId();
        logger.info("Start get last played hand method, gameId[" + gameId + "]");
        Game game = CachedDominoGames.getGame(hand.getGameInfo().getGameId());
        if (game.getHistory().isEmpty()) {
            logger.warn("There is not any hand in history, gameId[" + gameId + "]");
            return hand;
        }
        logger.info("Undo last played hand, gameId[" + gameId + "]");
        return game.getHistory().poll();
    }

    public Hand addLeftTiles(Hand hand, int himTilesCount) {
        GameInfo gameInfo = hand.getGameInfo();
        int gameId = gameInfo.getGameId();
        logger.info("Start add left tile method, gameId[" + gameId + "]");
        hand.getTableInfo().setNeedToAddLeftTiles(false);
        TableInfo tableInfo = hand.getTableInfo();
        if (tableInfo.isOmittedMe() && tableInfo.isOmittedHim()) {
            int myTilesCount = DominoHelper.countLeftTiles(hand, true, false);
            if (myTilesCount < himTilesCount) {
                DominoHelper.addLeftTiles(gameInfo, himTilesCount, true, gameId, false);
                logger.info("Added lef tiles for me, gameId[" + gameId + "], count[" + himTilesCount + "]");
            } else if (myTilesCount > himTilesCount) {
                DominoHelper.addLeftTiles(gameInfo, myTilesCount, false, gameId, false);
                logger.info("Added lef tiles for him, gameId[" + gameId + "], count[" + myTilesCount + "]");
            } else {
                CachedDominoGames.addPointFromLastHand(gameId, myTilesCount);
                logger.info("Added last hand tiles count in cach, gameId[" + gameId + "], count[" + myTilesCount + "]");
            }
            return DominoHelper.finishedLastAndGetNewHand(hand, hand.getTableInfo().isMyTurn(), false, false);
        } else {
            DominoHelper.addLeftTiles(gameInfo, himTilesCount, true, gameId, false);
            logger.info("Added lef tiles for me, gameId[" + gameId + "], count[" + himTilesCount + "]");
            return DominoHelper.finishedLastAndGetNewHand(hand, true, false, false);
        }
    }
}
