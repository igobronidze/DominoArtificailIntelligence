package ge.ai.domino.server.manager.game;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentTile;
import ge.ai.domino.domain.game.opponentplay.OpponentTilesWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.server.manager.game.ai.minmax.NodeRound;
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
import ge.ai.domino.server.manager.util.ProjectVersionUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;

public class GameManager {

    private static final Logger logger = Logger.getLogger(GameManager.class);

    private final MoveProcessor playForMeProcessor = new PlayForMeProcessor();

    private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor();

    private final MoveProcessor addForMeProcessor = new AddForMeProcessor();

    private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor();

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
        newRound.setWarnMsgKey(OpponentTilesValidator.validateOpponentTiles(round, 0, "addTileForMe"));
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
        CachedGames.addOpponentPlay(gameId, new OpponentPlay(0, gameId, ProjectVersionUtil.getVersion(), MoveType.ADD_FOR_OPPONENT,
                new Tile(0, 0), getOpponentTilesWrapper(round.getOpponentTiles()), new ArrayList<>(GameOperations.getPossiblePlayNumbers(round.getTableInfo()))));
        Round newRound = addForOpponentProcessor.move(round, getMove(0, 0, MoveDirection.LEFT), false);
        newRound.setWarnMsgKey(OpponentTilesValidator.validateOpponentTiles(round, round.getTableInfo().getTilesFromBazaar(), "addTileForOpponent"));
        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, round.getTableInfo().getRoundBlockingInfo().isOmitOpponent() ? MoveHelper.getOmittedOpponentMove() : MoveHelper.getAddTileForOpponentMove());
        return newRound;
    }

    public Round playForMe(int gameId, Move move) throws DAIException {
        move = getMove(move);
        Round round = CachedGames.getCurrentRound(gameId, true);
        MoveValidator.validateMove(round, move);
        round.setAiPredictions(null);
        Round newRound = playForMeProcessor.move(round, move, false);
        newRound.setWarnMsgKey(OpponentTilesValidator.validateOpponentTiles(round, 0, "playForMe" + move));
        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, MoveHelper.getPlayForMeMove(move));

        changeCachedNodeRound(gameId, move);

        return newRound;
    }

    public Round playForOpponent(int gameId, Move move) throws DAIException {
        move = getMove(move);
        Round round = CachedGames.getCurrentRound(gameId, true);
        MoveValidator.validateMove(round, move);
        CachedGames.addOpponentPlay(gameId, new OpponentPlay(0, gameId, ProjectVersionUtil.getVersion(), MoveType.PLAY_FOR_OPPONENT,
                new Tile(move.getLeft(), move.getRight()), getOpponentTilesWrapper(round.getOpponentTiles()), new ArrayList<>()));
        Round newRound = playForOpponentProcessor.move(round, move, false);
        newRound.setWarnMsgKey(OpponentTilesValidator.validateOpponentTiles(round, 0, "playForOpponent " + move));
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
        CachedGames.changeNextRoundBeginner(gameId, startMe);
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

    private OpponentTilesWrapper getOpponentTilesWrapper(Map<Tile, Double> opponentTiles) {
        OpponentTilesWrapper opponentTilesWrapper = new OpponentTilesWrapper();
        for (Map.Entry<Tile, Double> entry : opponentTiles.entrySet()) {
            opponentTilesWrapper.getOpponentTiles().add(new OpponentTile(entry.getKey().getLeft(), entry.getKey().getRight(), entry.getValue()));
        }
        return opponentTilesWrapper;
    }

    private void changeCachedNodeRound(long gameId, Move move) {
        NodeRound nodeRound = CachedMinMax.getNodeRound(gameId);
        if (nodeRound != null) {
            for (NodeRound child : nodeRound.getChildren()) {
                if (Move.equals(move, child.getLastPlayedMove())) {
                    CachedMinMax.setLastNodeRound(gameId, child);
                    break;
                }
            }
        }
    }
}
