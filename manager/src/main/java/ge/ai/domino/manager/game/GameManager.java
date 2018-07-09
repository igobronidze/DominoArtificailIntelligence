package ge.ai.domino.manager.game;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentTile;
import ge.ai.domino.domain.game.opponentplay.OpponentTilesWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.ai.minmax.NodeRound;
import ge.ai.domino.manager.game.ai.predictor.MinMaxPredictor;
import ge.ai.domino.manager.game.helper.game.GameOperations;
import ge.ai.domino.manager.game.helper.game.MoveHelper;
import ge.ai.domino.manager.game.helper.initial.InitialUtil;
import ge.ai.domino.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.manager.game.move.AddForMeProcessor;
import ge.ai.domino.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.manager.game.move.MoveProcessor;
import ge.ai.domino.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.manager.game.move.PlayForOpponentProcessor;
import ge.ai.domino.manager.game.validator.MoveValidator;
import ge.ai.domino.manager.game.validator.OpponentTilesValidator;
import ge.ai.domino.manager.imageprocessing.TilesDetectorManager;
import ge.ai.domino.manager.util.ProjectVersionUtil;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameManager {

    private static final Logger logger = Logger.getLogger(GameManager.class);

    private final MoveProcessor playForMeProcessor = new PlayForMeProcessor();

    private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor();

    private final MoveProcessor addForMeProcessor = new AddForMeProcessor();

    private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor();

    private final TilesDetectorManager tilesDetectorManager = new TilesDetectorManager();

    private static final String LOG_IMAGES_DIRECTORY_PATH = "log/images";

    public Round startGame(GameProperties gameProperties) {
        logger.info("Preparing new game");
        Game game = InitialUtil.getInitialGame(gameProperties);
        CachedGames.addGame(game);
        CachedGames.addMove(game.getId(), MoveHelper.getStartNewRoundMove());
        logger.info("------------Started new game[" + game.getId() + "]------------");
        logger.info("Opponent Name: " + game.getProperties().getOpponentName() + "     |     " + "WebSite: " + game.getProperties().getWebsite() + "     |     " +
        "Point for win: " + game.getProperties().getPointsForWin());
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
        checkMinMaxInProgress(gameId);
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

        if (new MinMaxPredictor().usePredictor()) {
            changeCachedNodeRound(gameId, move);
        }

        return newRound;
    }

    public Round playForOpponent(int gameId, Move move) throws DAIException {
        checkMinMaxInProgress(gameId);
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
        CachedMinMax.setLastNodeRound(gameId, null, false);
        logger.info("Undo last game round, gameId[" + gameId + "]");
        return newRound;
    }

    public void specifyRoundBeginner(int gameId, boolean startMe) {
        CachedGames.changeNextRoundBeginner(gameId, startMe);
    }

    public void specifyOpponentLeftTiles(int gameId, int leftTilesCount) {
        CachedGames.specifyOpponentLeftTilesCount(gameId, leftTilesCount);
    }

    public Round skipRound(int gameId, int myPoint, int opponentPoint, int leftTiles, boolean startMe, boolean finishGame) {
        logger.info("Start skipRound method, gameId[" + gameId + "], myPoint[" + myPoint + "], opponentPoint[" + opponentPoint + "]," +
                " leftTiles[" + leftTiles + "], startMe[" + startMe + "], finishGame[" + finishGame + "]");
        Round newRound = InitialUtil.getInitialRound(gameId, false);
        newRound.getGameInfo().setMyPoint(myPoint);
        newRound.getGameInfo().setOpponentPoint(opponentPoint);
        newRound.getGameInfo().setFinished(finishGame);

        CachedGames.changeNextRoundBeginner(gameId, startMe);
        CachedGames.setLeftTilesCountFromLastRound(gameId, leftTiles);

        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, MoveHelper.getSkipRoundMove());
        logger.info("Skipped round, gameId[" + gameId + "]");
        return newRound;
    }

    public Round detectAndAddNewTilesForMe(int gameId) throws DAIException {
        logger.info("Start detectAndAddNewTilesForMe method, gameId[" + gameId + "]");
        Round round = CachedGames.getCurrentRound(gameId, true);
        try {
            List<Tile> tiles = tilesDetectorManager.detectTiles(gameId);
            List<Tile> tilesForAdd = getAddedTiles(tiles, round.getMyTiles());
            Tile lastAddedTile = getLastAddedTile(tilesForAdd, round.getTableInfo());
            logger.info("Last added tile: " + lastAddedTile);
            for (Tile tile : tilesForAdd) {
                if (lastAddedTile == null || !lastAddedTile.equals(tile)) {
                    addTileForMe(gameId, tile.getLeft(), tile.getRight());
                }
            }
            if (lastAddedTile != null) {
                addTileForMe(gameId, lastAddedTile.getLeft(), lastAddedTile.getRight());
            } else {
                Tile tile = round.getOpponentTiles().keySet().stream().findAny().get();
                addTileForMe(gameId, tile.getLeft(), tile.getRight());
            }
        } catch (Exception ex) {
            logImage(gameId, tilesDetectorManager.getTmpImagePath());
            throw ex;
        }
        logger.info("Added tiles for me, gameId[" + gameId + "]");
        return CachedGames.getCurrentRound(gameId, false);
    }

    public Round detectAndAddInitialTilesForMe(int gameId, Boolean startMe) throws DAIException {
        logger.info("Start detectAndAddInitialTilesForMe method, gameId[" + gameId + "]");
        try {
            List<Tile> tiles = tilesDetectorManager.detectTiles(gameId);
            for (int i = 0; i < tiles.size() - 1; i++) {
                Tile tile = tiles.get(i);
                addTileForMe(gameId, tile.getLeft(), tile.getRight());
            }
            if (startMe != null) {
                specifyRoundBeginner(gameId, startMe);
            }
            Tile tile = tiles.get(tiles.size() - 1);
            addTileForMe(gameId, tile.getLeft(), tile.getRight());
        } catch (Exception ex) {
            logImage(gameId,tilesDetectorManager.getTmpImagePath());
            throw ex;
        }
        logger.info("Added tiles for me, gameId[" + gameId + "]");
        return CachedGames.getCurrentRound(gameId, false);
    }

    private void logImage(int gameId, String imagePath) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");

            File folder = new File(LOG_IMAGES_DIRECTORY_PATH);
            folder.mkdirs();
            String destPath = folder.getPath() + "/" + sdf.format(new Date()) + TilesDetectorManager.TMP_IMAGE_EXTENSION;
            Files.copy(Paths.get(imagePath), Paths.get(destPath));
            new File(imagePath).delete();
            logger.info("Save log image");
        } catch (IOException ex) {
            logger.error("Can't save log image, gameId[" + gameId + "]");
        }
    }

    private void checkMinMaxInProgress(int gameId) throws DAIException {
        if (CachedMinMax.isMinMaxInProgress(gameId)) {
            throw new DAIException("minMaxIsInProgress");
        }
    }

    private Tile getLastAddedTile(List<Tile> tiles, TableInfo tableInfo) {
        for (Tile tile : tiles) {
            if (canPlay(tile.getLeft(), tableInfo) || canPlay(tile.getRight(), tableInfo)) {
                return tile;
            }
        }
        return null;
    }

    private boolean canPlay(int x, TableInfo tableInfo) {
        return (tableInfo.getTop() != null && tableInfo.getTop().getOpenSide() == x) ||
                (tableInfo.getRight() != null && tableInfo.getRight().getOpenSide() == x) ||
                (tableInfo.getBottom() != null && tableInfo.getBottom().getOpenSide() == x) ||
                (tableInfo.getLeft() != null && tableInfo.getLeft().getOpenSide() == x);
    }

    private List<Tile> getAddedTiles(List<Tile> tiles, Set<Tile> myTiles) {
        return tiles.stream().filter(tile -> !myTiles.contains(tile)).collect(Collectors.toList());
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

    private void changeCachedNodeRound(int gameId, Move move) throws DAIException {
        if (CachedMinMax.isMinMaxInProgress(gameId)) {
            CachedMinMax.changeUseFirstChild(gameId, true);
        } else if (CachedMinMax.needChange(gameId)) {
            NodeRound nodeRound = CachedMinMax.getNodeRound(gameId);
            if (nodeRound != null) {
                for (NodeRound child : nodeRound.getChildren()) {
                    if (TileAndMoveHelper.equalWithHash(move, child.getLastPlayedMove(), nodeRound.getRound().getTableInfo())) {
                        CachedMinMax.setLastNodeRound(gameId, child, false);
                        return;
                    }
                }
                logger.warn("Can't find node round for change in MinMax cache, move[" + move + "]");
                throw new DAIException("cantChangeNodeRound");
            }
        }
    }
}
