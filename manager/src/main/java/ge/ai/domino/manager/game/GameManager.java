package ge.ai.domino.manager.game;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.*;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentTile;
import ge.ai.domino.domain.game.opponentplay.OpponentTilesWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.ai.minmax.CachedPrediction;
import ge.ai.domino.manager.game.ai.predictor.OpponentTilesPredictorFactory;
import ge.ai.domino.manager.game.helper.initial.InitialUtil;
import ge.ai.domino.manager.game.helper.play.GameOperations;
import ge.ai.domino.manager.game.helper.play.MoveHelper;
import ge.ai.domino.manager.game.logging.GameLogger;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.game.move.*;
import ge.ai.domino.manager.game.validator.MoveValidator;
import ge.ai.domino.manager.game.validator.OpponentTilesValidator;
import ge.ai.domino.manager.imageprocessing.TilesDetectorManager;
import ge.ai.domino.manager.multiprocessorserver.MultiProcessorServer;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import ge.ai.domino.manager.util.ProjectVersionUtil;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private static final Logger logger = Logger.getLogger(GameManager.class);

    private final MoveProcessor playForMeProcessor = new PlayForMeProcessor();

    private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor();

    private final MoveProcessor addForMeProcessor = new AddForMeProcessor();

    private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor();

    private final TilesDetectorManager tilesDetectorManager = new TilesDetectorManager();

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    private final SysParam useMultiProcessorMinMax = new SysParam("useMultiProcessorMinMax", "true");

    private static final String LOG_IMAGES_DIRECTORY_PATH = "log/images";

    public Round startGame(GameProperties gameProperties) {
        logger.info("Preparing new game");
        Game game = InitialUtil.getInitialGame(gameProperties, true);
        CachedGames.addGame(game);
        CachedGames.addMove(game.getId(), MoveHelper.getStartNewRoundMove());
        GameLogger.logGameInfo(game);
        Round newRound = CachedGames.getCurrentRound(game.getId(), false);

        ifNeedSendInitialData(game, gameProperties);

        RoundLogger.logRoundFullInfo(newRound);
        return newRound;
    }

    public Round addTileForMe(int gameId, int left, int right) throws DAIException {
        Round round = CachedGames.getCurrentRound(gameId, true);
        Move move = getMove(left, right, MoveDirection.LEFT);
        Round newRound = addForMeProcessor.move(round, move);
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
        Round newRound = addForOpponentProcessor.move(round, getMove(0, 0, MoveDirection.LEFT));
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
        Round newRound = playForMeProcessor.move(round, move);
        newRound.setWarnMsgKey(OpponentTilesValidator.validateOpponentTiles(round, 0, "playForMe" + move));
        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, MoveHelper.getPlayForMeMove(move));

        if (OpponentTilesPredictorFactory.useMinMaxPredictor()) {
            changeCachedNodeRound(gameId, move, round);
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
        Round newRound = playForOpponentProcessor.move(round, move);
        newRound.setWarnMsgKey(OpponentTilesValidator.validateOpponentTiles(round, 0, "playForOpponent " + move));
        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, MoveHelper.getPlayForOpponentMove(move));
        return newRound;
    }

    public Round getLastPlayedRound(int gameId) throws DAIException {
        logger.info("Start getLastPlayedRound method, gameId[" + gameId + "]");
        Round newRound = CachedGames.getAndRemoveLastRound(gameId);
        CachedGames.removeLastMove(gameId);
        CachedMinMax.setCachedPrediction(gameId, null, false);
        logger.info("Undo last game round, gameId[" + gameId + "]");
        return newRound;
    }

    public void specifyRoundBeginner(int gameId, boolean startMe) {
        logger.info("Start specifyRoundBeginner method, gameId[" + gameId + "], startMe[" + startMe +"]");
        CachedGames.changeNextRoundBeginner(gameId, startMe);
        logger.info("specified round beginner, gameId[" + gameId + "]");
    }

    public void specifyOpponentLeftTiles(int gameId, int leftTilesCount) {
        logger.info("Start specifyOpponentLeftTiles method, gameId[" + gameId + "], leftTilesCount[" + leftTilesCount +"]");
        CachedGames.specifyOpponentLeftTilesCount(gameId, leftTilesCount);
        logger.info("specified opponent left tiles, gameId[" + gameId + "]");
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
        CachedGames.addMove(gameId, MoveHelper.getSkipRoundMove(myPoint, opponentPoint, leftTiles, startMe, finishGame));
        logger.info("Skipped round, gameId[" + gameId + "]");
        return newRound;
    }

    public Round detectAndAddNewTilesForMe(int gameId, boolean withSecondParams) throws DAIException {
        logger.info("Start detectAndAddNewTilesForMe method, gameId[" + gameId + "], withSecondParams[" + withSecondParams + "]");
        Round round = CachedGames.getCurrentRound(gameId, true);
        try {
            List<Tile> tiles = tilesDetectorManager.detectTiles(gameId, withSecondParams);
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
                logger.info("Random tile for add me is " + tile);
                addTileForMe(gameId, tile.getLeft(), tile.getRight());
            }
        } catch (Exception ex) {
            try {
                logImage(tilesDetectorManager.getLastImage());
            } catch (IOException ioEx) {
                logger.warn("Can't save log image", ioEx);
            }
            throw ex;
        }
        logger.info("Added tiles for me, gameId[" + gameId + "]");
        return CachedGames.getCurrentRound(gameId, false);
    }

    public Round detectAndAddInitialTilesForMe(int gameId, Boolean startMe, boolean withSecondParams) throws DAIException {
        logger.info("Start detectAndAddInitialTilesForMe method, gameId[" + gameId + "], startMe[" + startMe + "], withSecondParams[" + withSecondParams + "]");
        try {
            List<Tile> tiles = tilesDetectorManager.detectTiles(gameId, withSecondParams);
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
            try {
                logImage(tilesDetectorManager.getLastImage());
            } catch (IOException ioEx) {
                logger.warn("Can't save log image", ioEx);
            }
            throw ex;
        }
        logger.info("Added tiles for me, gameId[" + gameId + "]");
        return CachedGames.getCurrentRound(gameId, false);
    }

    public void editOpponentNameInCache(int gameId, String name) {
        logger.info("Start editOpponentNameInCache method, gameId[" + gameId + "], name[" + name +"]");
        CachedGames.getGameProperties(gameId).setOpponentName(name);
        logger.info("Edited opponent name in cache, gameId[" + gameId + "]");
    }

    public String getCurrentRoundInfoInString(int gameId) {
        return RoundLogger.getRoundFullInfo(CachedGames.getCurrentRound(gameId, true));
    }

    public void ifNeedSendInitialData(Game game, GameProperties gameProperties) {
        if (systemParameterManager.getBooleanParameterValue(useMultiProcessorMinMax)) {
            MultiProcessorServer multiProcessorServer = MultiProcessorServer.getInstance();

            GameInitialData gameInitialData = new GameInitialData();
            gameInitialData.setGameId(game.getId());
            gameInitialData.setPointsForWin(gameProperties.getPointsForWin());
            multiProcessorServer.initGame(gameInitialData);
        }
    }

    public boolean roundWillBeBlocked(int gameId, Move move) {
        Round round = CachedGames.getCurrentRound(gameId, true);
        try {
            MoveValidator.validateMove(round, move);
        } catch (DAIException ex) {
            return false;
        }

        GameOperations.playTile(round, move, true);
        return GameOperations.isRoundBlocked(round);
    }

    public Map<Tile, Integer> getTilesOrder(int gameId) {
        return CachedGames.getTilesOrder(gameId);
    }

    private void logImage(BufferedImage image) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");

        File folder = new File(LOG_IMAGES_DIRECTORY_PATH);
        folder.mkdirs();

        String destPath = folder.getPath() + "/" + sdf.format(new Date()) + TilesDetectorManager.TMP_IMAGE_EXTENSION;

        File outputfile = new File(destPath);
        ImageIO.write(image, "jpg", outputfile);
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

    private void changeCachedNodeRound(int gameId, Move move, Round round) throws DAIException {
        if (CachedMinMax.isMinMaxInProgress(gameId)) {
            CachedMinMax.changeUseFirstChild(gameId, true);
        } else if (CachedMinMax.needChange(gameId)) {
            CachedPrediction cachedPrediction = CachedMinMax.getCachePrediction(gameId);
            if (cachedPrediction != null) {
                for (CachedPrediction child : cachedPrediction.getChildren().values()) {
                    if (child.getMove().equals(move)) {
                        CachedMinMax.setCachedPrediction(gameId, GameOperations.fillCachedPrediction(round, child), false);
                        return;
                    }
                }
                logger.warn("Can't find cached prediction for change in MinMax cache, move[" + move + "]");
                throw new DAIException("cantChangeNodeRound");
            }
        }
    }
}
