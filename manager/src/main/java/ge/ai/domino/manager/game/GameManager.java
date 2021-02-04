package ge.ai.domino.manager.game;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.*;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.game.opponentplay.OpponentPlay;
import ge.ai.domino.domain.game.opponentplay.OpponentTile;
import ge.ai.domino.domain.game.opponentplay.OpponentTilesWrapper;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.imageprocessing.service.Rectangle;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.ai.minmax.CachedPrediction;
import ge.ai.domino.manager.game.ai.predictor.OpponentTilesPredictorFactory;
import ge.ai.domino.manager.game.helper.initial.InitialUtil;
import ge.ai.domino.manager.game.helper.play.GameOperations;
import ge.ai.domino.manager.game.helper.play.MoveHelper;
import ge.ai.domino.manager.game.helper.play.PossibleMovesManager;
import ge.ai.domino.manager.game.logging.GameLogger;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.game.move.*;
import ge.ai.domino.manager.game.validator.MoveValidator;
import ge.ai.domino.manager.game.validator.OpponentTilesValidator;
import ge.ai.domino.manager.imageprocessing.RecognizeTableManager;
import ge.ai.domino.manager.multiprocessorserver.MultiProcessorServer;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import ge.ai.domino.manager.util.ProjectVersionUtil;
import ge.ai.domino.robot.MouseRobot;
import ge.ai.domino.robot.ScreenRobot;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import ge.ai.domino.util.random.RandomUtils;
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

    private static final int TILE_CONTOUR_CLICK_BORDER_SIZE = 3;

    private final MoveProcessor playForMeProcessor = new PlayForMeProcessor();

    private final MoveProcessor playForOpponentProcessor = new PlayForOpponentProcessor();

    private final MoveProcessor addForMeProcessor = new AddForMeProcessor();

    private final MoveProcessor addForOpponentProcessor = new AddForOpponentProcessor();

    private final RecognizeTableManager recognizeTableManager = new RecognizeTableManager();

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
        Move move = getLeftMove(left, right);
        Round newRound = addForMeProcessor.move(round, move);
        newRound.setWarnMsgKey(OpponentTilesValidator.validateOpponentTiles(round, 0, "addTileForMe"));
        CachedGames.addRound(gameId, newRound);
        if (round.getTableInfo().getLeft() == null && round.getMyTiles().size() == 1) {
            CachedGames.addMove(gameId, MoveHelper.getAddInitialTileForMeMove(move));
        } else {
            CachedGames.addMove(gameId, round.getTableInfo().getRoundBlockingInfo().isOmitMe() ? MoveHelper.getOmittedMeMove() : MoveHelper.getAddTileForMeMove(move));
        }
        fixAiPredictionsMoves(newRound.getAiPredictions(), gameId, newRound.getTableInfo());
        return newRound;
    }

    public Round addTileForOpponent(int gameId) throws DAIException {
        checkMinMaxInProgress(gameId);
        Round round = CachedGames.getCurrentRound(gameId, true);
        CachedGames.addOpponentPlay(gameId, new OpponentPlay(0, gameId, ProjectVersionUtil.getVersion(), MoveType.ADD_FOR_OPPONENT,
                new Tile(0, 0), getOpponentTilesWrapper(round.getOpponentTiles()), new ArrayList<>(GameOperations.getPossiblePlayNumbers(round.getTableInfo()))));
        Round newRound = addForOpponentProcessor.move(round, getLeftMove(0, 0));
        newRound.setWarnMsgKey(OpponentTilesValidator.validateOpponentTiles(round, round.getTableInfo().getTilesFromBazaar(), "addTileForOpponent"));
        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, round.getTableInfo().getRoundBlockingInfo().isOmitOpponent() ? MoveHelper.getOmittedOpponentMove() : MoveHelper.getAddTileForOpponentMove());
        fixAiPredictionsMoves(newRound.getAiPredictions(), gameId, newRound.getTableInfo());
        return newRound;
    }

    public Round playForMe(int gameId, Move move) throws DAIException {
        Round round = CachedGames.getCurrentRound(gameId, true);
        move = getMoveForPlay(move, gameId, round.getTableInfo());
        MoveValidator.validateMove(round, move);
        round.setAiPredictions(null);
        Round newRound = playForMeProcessor.move(round, move);
        newRound.setWarnMsgKey(OpponentTilesValidator.validateOpponentTiles(round, 0, "playForMe" + move));
        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, MoveHelper.getPlayForMeMove(move));

        if (OpponentTilesPredictorFactory.useMinMaxPredictor()) {
            changeCachedNodeRound(gameId, move, round);
        }

        fixDirections(gameId, newRound.getTableInfo());

        return newRound;
    }

    public Round playForOpponent(int gameId, Move move) throws DAIException {
        checkMinMaxInProgress(gameId);
        Round round = CachedGames.getCurrentRound(gameId, true);
        move = getMoveForPlay(move, gameId, round.getTableInfo());
        MoveValidator.validateMove(round, move);
        CachedGames.addOpponentPlay(gameId, new OpponentPlay(0, gameId, ProjectVersionUtil.getVersion(), MoveType.PLAY_FOR_OPPONENT,
                new Tile(move.getLeft(), move.getRight()), getOpponentTilesWrapper(round.getOpponentTiles()), new ArrayList<>()));
        Round newRound = playForOpponentProcessor.move(round, move);
        newRound.setWarnMsgKey(OpponentTilesValidator.validateOpponentTiles(round, 0, "playForOpponent " + move));
        CachedGames.addRound(gameId, newRound);
        CachedGames.addMove(gameId, MoveHelper.getPlayForOpponentMove(move));
        fixDirections(gameId, newRound.getTableInfo());
        fixAiPredictionsMoves(newRound.getAiPredictions(), gameId, newRound.getTableInfo());
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
        CachedGames.initDirectionsMap(gameId);
        logger.info("Skipped round, gameId[" + gameId + "]");
        return newRound;
    }

    public Round recognizeAndAddNewTilesForMe(int gameId) throws DAIException {
        logger.info("Start detectAndAddNewTilesForMe method, gameId[" + gameId + "]");
        Round round = CachedGames.getCurrentRound(gameId, true);
        try {
            List<Tile> tiles = recognizeTableManager.recognizeMyTiles(gameId);
            List<Tile> tilesForAdd = getAddedTiles(tiles, round.getMyTiles());
            Tile canPlayTile = getCanPlayTile(tilesForAdd, round.getTableInfo());
            logger.info("Can play tile: " + canPlayTile);
            for (Tile tile : tilesForAdd) {
                if (canPlayTile == null || !canPlayTile.equals(tile)) {
                    addTileForMe(gameId, tile.getLeft(), tile.getRight());
                }
            }
            if (canPlayTile != null) {
                addTileForMe(gameId, canPlayTile.getLeft(), canPlayTile.getRight());
            } else {
                if (CachedGames.getCurrentRound(gameId, false).getTableInfo().getBazaarTilesCount() == 2) {
                    Tile tile = round.getOpponentTiles().keySet().stream().findAny().get();
                    logger.info("Random tile for add me is " + tile);
                    addTileForMe(gameId, tile.getLeft(), tile.getRight());
                }
            }
        } catch (Exception ex) {
            try {
                logImage(recognizeTableManager.getLastImage());
            } catch (IOException ioEx) {
                logger.warn("Can't save log image", ioEx);
            }
            throw ex;
        }
        logger.info("Added tiles for me, gameId[" + gameId + "]");
        return CachedGames.getCurrentRound(gameId, false);
    }

    public Round recognizeAndAddInitialTilesForMe(int gameId, Boolean startMe) throws DAIException {
        logger.info("Start detectAndAddInitialTilesForMe method, gameId[" + gameId + "], startMe[" + startMe + "]");
        try {
            List<Tile> tiles = recognizeTableManager.recognizeMyTiles(gameId);
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
                logImage(recognizeTableManager.getLastImage());
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

    public void simulatePlayMove(int gameId, int left, int right, MoveDirection direction) throws DAIException {
        try {
            ScreenRobot.changeScreen();

            Rectangle tileLocation = recognizeTableManager.getRecognizeTileLocation(gameId, left, right);

            if (isMultiplePossibleMove(gameId, left, right)) {
                moveOnRandomPosition(tileLocation);
                Thread.sleep(RandomUtils.getRandomBetween(100, 150));
                Rectangle possibleMoveRectangle = recognizeTableManager.getPossibleMoveRectangle(gameId, direction);

                if (RandomUtils.getBooleanByProbability(0.25)) {
                    MouseRobot.click();
                    Thread.sleep(RandomUtils.getRandomBetween(50, 100));
                    moveOnRandomPosition(possibleMoveRectangle);
                    MouseRobot.click();
                } else {
                    dragOnRandomPosition(possibleMoveRectangle);
                }
            } else {
                moveOnRandomPosition(tileLocation);
                MouseRobot.click();
                Thread.sleep(RandomUtils.getRandomBetween(200, 400));
                MouseRobot.moveDeltaPosition(RandomUtils.getRandomBetween(-30, 30), RandomUtils.getRandomBetween(-120, -75));
            }
        } catch (Exception ex) {
            try {
                logImage(recognizeTableManager.getLastImage());
            } catch (IOException ioEx) {
                logger.warn("Can't save log image", ioEx);
            }
            logger.error("Error occurred while simulate click", ex);
            throw new DAIException("clickSimulateError");
        }
    }

    public Round simulateAddNewTile(int gameId) throws DAIException {
        Round round = CachedGames.getCurrentRound(gameId, false);
        List<Rectangle> myBazaarTiles = recognizeTableManager.getMyBazaarTileRectangles(gameId, (int) round.getTableInfo().getBazaarTilesCount());

        try {
            boolean clicked = false;
            for (Rectangle rectangle : myBazaarTiles) {
                if (MouseRobot.isCursorOnRectangle(rectangle.getTopLeft().getX() + TILE_CONTOUR_CLICK_BORDER_SIZE, rectangle.getTopLeft().getY() + TILE_CONTOUR_CLICK_BORDER_SIZE,
                        rectangle.getBottomRight().getX() - TILE_CONTOUR_CLICK_BORDER_SIZE, rectangle.getBottomRight().getY() - TILE_CONTOUR_CLICK_BORDER_SIZE)) {
                    MouseRobot.click();
                    clicked = true;
                    break;
                }
            }

            if (!clicked) {
                int randomIndex = RandomUtils.getRandomBetween(myBazaarTiles.size() / 2 - 1, myBazaarTiles.size() / 2 + 2);
                Rectangle randomTile = myBazaarTiles.get(randomIndex);
                moveOnRandomPosition(randomTile);
                MouseRobot.click();
            }

            Thread.sleep(RandomUtils.getRandomBetween(300, 400));
        } catch (Exception ex) {
            logger.error("Error occurred while simulate add new tile", ex);
            throw new DAIException("clickSimulateError");
        }

        return recognizeAndAddNewTilesForMe(gameId);
    }

    private boolean isMultiplePossibleMove(int gameId, int left, int right) {
        TableInfo tableInfo = CachedGames.getCurrentRound(gameId, true).getTableInfo();
        int possMovesCount = 0;
        if (tableInfo.getLeft() != null) {
            if (tableInfo.getLeft().getOpenSide() == left || tableInfo.getLeft().getOpenSide() == right) {
                possMovesCount++;
            }
        }
        if (tableInfo.getRight() != null) {
            if (tableInfo.getRight().getOpenSide() == left || tableInfo.getRight().getOpenSide() == right) {
                possMovesCount++;
            }
        }
        if (tableInfo.getTop() != null && !tableInfo.getLeft().isCenter() && !tableInfo.getRight().isCenter()) {
            if (tableInfo.getTop().getOpenSide() == left || tableInfo.getTop().getOpenSide() == right) {
                possMovesCount++;
            }
        }
        if (tableInfo.getBottom() != null && !tableInfo.getLeft().isCenter() && !tableInfo.getRight().isCenter()) {
            if (tableInfo.getBottom().getOpenSide() == left || tableInfo.getBottom().getOpenSide() == right) {
                possMovesCount++;
            }
        }
        return possMovesCount > 1;
    }

    private void moveOnRandomPosition(Rectangle rectangle) throws Exception {
        int randomPositionX = RandomUtils.getRandomBetween(rectangle.getTopLeft().getX() + TILE_CONTOUR_CLICK_BORDER_SIZE,
                rectangle.getBottomRight().getX() - TILE_CONTOUR_CLICK_BORDER_SIZE + 1);
        int randomPositionY = RandomUtils.getRandomBetween(rectangle.getTopLeft().getY() + TILE_CONTOUR_CLICK_BORDER_SIZE,
                rectangle.getBottomRight().getY() - TILE_CONTOUR_CLICK_BORDER_SIZE + 1);

        MouseRobot.move(randomPositionX, randomPositionY);
    }

    private void dragOnRandomPosition(Rectangle rectangle) throws Exception {
        int randomPositionX = RandomUtils.getRandomBetween(rectangle.getTopLeft().getX() + TILE_CONTOUR_CLICK_BORDER_SIZE,
                rectangle.getBottomRight().getX() - TILE_CONTOUR_CLICK_BORDER_SIZE + 1);
        int randomPositionY = RandomUtils.getRandomBetween(rectangle.getTopLeft().getY() + TILE_CONTOUR_CLICK_BORDER_SIZE,
                rectangle.getBottomRight().getY() - TILE_CONTOUR_CLICK_BORDER_SIZE + 1);

        MouseRobot.drag(randomPositionX, randomPositionY);
    }

    private void logImage(BufferedImage image) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");

        File folder = new File(LOG_IMAGES_DIRECTORY_PATH);
        folder.mkdirs();

        String destPath = folder.getPath() + "/" + sdf.format(new Date()) + RecognizeTableManager.TMP_IMAGE_EXTENSION;

        File outputFile = new File(destPath);
        ImageIO.write(image, "jpg", outputFile);
    }

    private void checkMinMaxInProgress(int gameId) throws DAIException {
        if (CachedMinMax.isMinMaxInProgress(gameId)) {
            throw new DAIException("minMaxIsInProgress");
        }
    }

    private Tile getCanPlayTile(List<Tile> tiles, TableInfo tableInfo) {
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

    private Move getLeftMove(int left, int right) {
        return new Move(Math.max(left, right), Math.min(left, right), MoveDirection.LEFT);
    }

    private Move getMoveForPlay(Move move, int gameId, TableInfo tableInfo) {
        MoveDirection direction = CachedGames.getDirectionsMap(gameId).get(move.getDirection());

        if (direction != null) {
            List<MoveDirection> movePriorities = PossibleMovesManager.getMovePriority(gameId);
            for (MoveDirection md : movePriorities) {
                if (md == direction) {
                    break;
                }

                if (isSameMove(direction, md, tableInfo)) {
                    Map<MoveDirection, MoveDirection> directionsMap = CachedGames.getDirectionsMap(gameId);
                    directionsMap.put(md, direction);
                    directionsMap.put(direction, md);
                    direction = md;
                    break;
                }
            }
        }

        return new Move(Math.max(move.getLeft(), move.getRight()), Math.min(move.getLeft(), move.getRight()), direction);
    }

    private boolean isSameMove(MoveDirection direction1, MoveDirection direction2, TableInfo tableInfo) {
        if (direction1 == null || direction2 == null) {
            return false;
        }
        PlayedTile playedTile1 = TileAndMoveHelper.getPlayedTile(tableInfo, direction1);
        PlayedTile playedTile2 = TileAndMoveHelper.getPlayedTile(tableInfo, direction2);

        if (playedTile1 == null || playedTile2 == null) {
            return false;
        }
        if (playedTile1.getOpenSide() != playedTile2.getOpenSide()) {
            return false;
        }
        if (playedTile1.isTwin() != playedTile2.isTwin() && playedTile1.getOpenSide() != 0) {
            return false;
        }
        if (playedTile1.isConsiderInSum() != playedTile2.isConsiderInSum()) {
            return false;
        }
        return playedTile1.isCenter() == playedTile2.isCenter();
    }

    private void fixDirections(int gameId, TableInfo tableInfo) {
        Map<MoveDirection, MoveDirection> directionsMap = CachedGames.getDirectionsMap(gameId);
        new ArrayList<>(directionsMap.keySet()).forEach(direction1 -> {
            MoveDirection direction2 = directionsMap.get(direction1);
            if (direction1 != direction2) {
                if (direction1 == directionsMap.get(direction2) && direction2 == directionsMap.get(direction1) && isSameMove(direction1, direction2, tableInfo)) {
                    directionsMap.put(direction1, direction1);
                    directionsMap.put(direction2, direction2);
                }
            }
        });
    }

    private void fixAiPredictionsMoves(AiPredictionsWrapper aiPredictionsWrapper, int gameId, TableInfo tableInfo) {
        Map<MoveDirection, MoveDirection> directionsMap = CachedGames.getDirectionsMap(gameId);

        if (aiPredictionsWrapper != null && aiPredictionsWrapper.getAiPredictions() != null) {
            aiPredictionsWrapper.getAiPredictions().forEach(aiPrediction -> {
                MoveDirection direction = aiPrediction.getMove().getDirection();
                MoveDirection reversedDirection = getReversMoveDirection(directionsMap, direction);

                if (direction != reversedDirection) {
                    if (!isSameMove(direction, reversedDirection, tableInfo)) {
                        Move move = new Move(aiPrediction.getMove().getLeft(), aiPrediction.getMove().getRight(), reversedDirection);
                        aiPrediction.setMove(move);
                    }
                }
            });
        }
    }

    private MoveDirection getReversMoveDirection(Map<MoveDirection, MoveDirection> directionsMap, MoveDirection direction) {
        for (Map.Entry<MoveDirection, MoveDirection> entry : directionsMap.entrySet()) {
            if (entry.getValue() == direction) {
                return entry.getKey();
            }
        }
        return null;
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
