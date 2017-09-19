package ge.ai.domino.server.manager.domino.processor;

import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.server.caching.domino.CachedDominoGames;
import ge.ai.domino.server.manager.domino.DominoHelper;
import ge.ai.domino.server.manager.domino.logging.DominoLoggingProcessor;
import ge.ai.domino.server.manager.util.InitialUtil;
import org.apache.log4j.Logger;

public class GameProcessor {

    private static final Logger logger = Logger.getLogger(GameProcessor.class);

    public Hand startGame(GameProperties gameProperties) {
        logger.info("Started prepare new game");
        Game game = InitialUtil.getInitialGame(gameProperties);
        CachedDominoGames.addGame(game);
        logger.info("Started new game[" + game.getId() + "]");
        DominoLoggingProcessor.logTilesFullInfo(game.getCurrHand());
        return game.getCurrHand();
    }

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

    public Hand addLeftTilesForHim(Hand hand, int count) {
        int gameId = hand.getGameInfo().getGameId();
        logger.info("Start add left tile for him method, gameId[" + gameId + "]");
        hand.getGameInfo().setHimPoints(hand.getGameInfo().getHimPoints() + count);
        hand.getTableInfo().setNeedToAddLeftTiles(false);
        logger.info("Added lef tiles for him, gameId[" + gameId + "]");
        return DominoHelper.finishedLastAndGetNewHand(hand, false, false);
    }
}
