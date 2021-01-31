package ge.ai.domino.manager.imageprocessing;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.imageprocessing.recognizer.MyBazaarTileRecognizeParams;
import ge.ai.domino.imageprocessing.recognizer.MyTileRecognizeParams;
import ge.ai.domino.imageprocessing.recognizer.PossMoveTileRecognizeParams;
import ge.ai.domino.imageprocessing.recognizer.TableRecognizer;
import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.imageprocessing.service.Rectangle;
import ge.ai.domino.imageprocessing.service.table.IPPossMovesAndCenter;
import ge.ai.domino.imageprocessing.service.table.IPTile;
import ge.ai.domino.manager.game.GameManager;
import ge.ai.domino.robot.ScreenRobot;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecognizeTableManager {

    private static final Logger logger = Logger.getLogger(GameManager.class);

    public static final String TMP_IMAGE_EXTENSION = ".png";

    private static final String MY_TILES_TOP_LEFT_X = "myTilesDetectorTopLeftX";
    private static final String MY_TILES_TOP_LEFT_Y = "myTilesDetectorTopLeftY";
    private static final String MY_TILES_BOTTOM_RIGHT_X = "myTilesDetectorBottomRightX";
    private static final String MY_TILES_BOTTOM_RIGHT_Y = "myTilesDetectorBottomRightY";
    private static final String MY_TILES_CONTOUR_MIN_AREA = "myTilesDetectorContourMinArea";
    private static final String MY_TILES_BLUR_COEFFICIENT = "myTilesDetectorBlurCoefficient";
    private static final String MY_TILES_COMBINE_POINTS = "myTilesDetectorCombinePoints";

    private static final String POSS_MOVES_CONTOUR_MIN_AREA = "possMovesDetectorMinArea";
    private static final String POSS_MOVES_TOP_LEFT_X = "possMovesDetectorTopLeftX";
    private static final String POSS_MOVES_TOP_LEFT_Y = "possMovesDetectorTopLeftY";
    private static final String POSS_MOVES_BOTTOM_RIGHT_X = "possMovesDetectorBottomRightX";
    private static final String POSS_MOVES_BOTTOM_RIGHT_Y = "possMovesDetectorBottomRightY";

    private static final String MY_BAZAAR_TILES_SCREEN_WIDTH = "myBazaarTilesScreenWidth";
    private static final String MY_BAZAAR_TILES_TILE_WIDTH = "myBazaarTilesTileWidth";
    private static final String MY_BAZAAR_TILES_TILES_SPACING = "myBazaarTilesTilesSpacing";
    private static final String MY_BAZAAR_TILES_TILE_TOP = "myBazaarTilesTileTop";
    private static final String MY_BAZAAR_TILES_TILE_BOTTOM = "myBazaarTilesTileBottom";

    private BufferedImage lastImage;

    public BufferedImage getLastImage() {
        return lastImage;
    }

    public List<Tile> recognizeMyTiles(int gameId) throws DAIException {
        try {
            logger.info("Start detectTiles method");

            List<IPTile> ipTiles = recognizeIPTiles(gameId);
            List<Tile> tiles = balanceTiles(ipTiles.stream().map(this::getTile).collect(Collectors.toList()));
            logger.info("Detected tiles: " + tiles);

            return tiles;
        } catch (Exception ex) {
            logger.error("Error occurred while detect tiles", ex);
            throw new DAIException("cantDetectTiles");
        }
    }

    public Rectangle getRecognizeTileLocation(int gameId, int left, int right) throws DAIException {
        try {
            logger.info("Start detectTileContour method");

            List<IPTile> ipTiles = recognizeIPTiles(gameId);

            IPTile recognizedTile = null;
            for (IPTile ipTile : ipTiles) {
                if ((ipTile.getLeft() == left && ipTile.getRight() == right) ||
                        (ipTile.getLeft() == right && ipTile.getRight() == left)) {
                    recognizedTile = ipTile;
                }
            }

            return new Rectangle(recognizedTile.getTopLeft(), recognizedTile.getBottomRight());
        } catch (Exception ex) {
            logger.error("Error occurred while detect tile contour", ex);
            throw new DAIException("cantDetectTiles");
        }
    }

    public Rectangle getPossibleMoveRectangle(int gameId, MoveDirection moveDirection) throws DAIException {
        logger.info("Start get possible move rectangles method");
        try {
            lastImage = ScreenRobot.getScreenCapture();
        } catch (Exception ex) {
            logger.error("Error occurred while screen capture", ex);
            throw new DAIException("cantDetectPossibleMoveRectangles");
        }

        PossMoveTileRecognizeParams params = getPossMoveTileRecognizeParams(gameId);
        IPPossMovesAndCenter ipPossMovesAndCenter = TableRecognizer.recognizePossMoveTiles(lastImage, params);
        List<Rectangle> possMoveRectangles = ipPossMovesAndCenter.getPossMoves().stream()
                .map(possMoveTile -> new Rectangle(possMoveTile.getTopLeft(), possMoveTile.getBottomRight()))
                .collect(Collectors.toList());
        Rectangle centerRectangle = ipPossMovesAndCenter.getCenter() == null ? null : new Rectangle(ipPossMovesAndCenter.getCenter().getTopLeft(), ipPossMovesAndCenter.getCenter().getBottomRight());
        logger.info("Recognized rectangles: " + possMoveRectangles);
        logger.info("Center: " + centerRectangle);
        Map<MoveDirection, Rectangle> rectanglesByDirections = getRectanglesByDirections(possMoveRectangles, centerRectangle);
        Rectangle relevantRectangle = rectanglesByDirections.get(moveDirection);
        logger.info("Relevant rectangle: " + possMoveRectangles);
        return relevantRectangle;
    }

    public List<Rectangle> getMyBazaarTileRectangles(int gameId, int bazaarTilesCount) {
        logger.info("Start get my bazaar tile rectangles method");
        MyBazaarTileRecognizeParams params = getMyBazaarTileRecognizeParams(gameId, bazaarTilesCount);
        List<Rectangle> myBazaarTilesRectangles = TableRecognizer.recognizeMyBazaarTiles(params).stream()
                .map(possMoveTile -> new Rectangle(possMoveTile.getTopLeft(), possMoveTile.getBottomRight()))
                .collect(Collectors.toList());
        logger.info("Recognized rectangles: " + myBazaarTilesRectangles);
        return myBazaarTilesRectangles;
    }

    private List<IPTile> recognizeIPTiles(int gameId) throws Exception {
        long ms = System.currentTimeMillis();
        lastImage = ScreenRobot.getScreenCapture();
        logger.info("Screenshot took " + (System.currentTimeMillis() - ms) + "ms");

        ms = System.currentTimeMillis();
        List<IPTile> tiles = TableRecognizer.recognizeMyTiles(lastImage, getMyTileRecognizeParams(gameId));
        logger.info("IPTable recognize took " + (System.currentTimeMillis() - ms) + "ms");

        return tiles;
    }

    private Tile getTile(IPTile ipTile) {
        return new Tile(ipTile.getLeft(), ipTile.getRight());
    }

    private List<Tile> balanceTiles(List<Tile> tiles) {
        List<Tile> result = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.getRight() > tile.getLeft()) {
                result.add(new Tile(tile.getRight(), tile.getLeft()));
            } else {
                result.add(new Tile(tile.getLeft(), tile.getRight()));
            }
        }
        return result;
    }

    protected Map<MoveDirection, Rectangle> getRectanglesByDirections(List<Rectangle> rectangles, Rectangle center) {
        Map<MoveDirection, Rectangle> map = new HashMap<>();
        if (rectangles.isEmpty()) return map;
        if (center == null) {
            Rectangle rectangle1 = rectangles.get(0);
            Rectangle rectangle2 = rectangles.get(1);
            map.put(MoveDirection.LEFT, rectangle1.getTopLeft().getX() < rectangle2.getTopLeft().getX() ? rectangle1 : rectangle2);
            map.put(MoveDirection.RIGHT, rectangle1.getTopLeft().getX() < rectangle2.getTopLeft().getX() ? rectangle2 : rectangle1);
        } else {
            for (Rectangle rectangle : rectangles) {
                if (rectangle.isHorizontal() && center.getTopLeft().getY() - 5 < rectangle.getTopLeft().getY() && center.getBottomRight().getY() + 5 > rectangle.getBottomRight().getY()) {
                    if (rectangle.getTopLeft().getX() < center.getTopLeft().getX()) {
                        map.put(MoveDirection.LEFT, rectangle);
                    } else {
                        map.put(MoveDirection.RIGHT, rectangle);
                    }
                    continue;
                }
                if (!rectangle.isHorizontal() && center.getTopLeft().getX() - 5 < rectangle.getTopLeft().getX() && center.getBottomRight().getX() + 5 > rectangle.getBottomRight().getX()) {
                    if (rectangle.getTopLeft().getY() < center.getTopLeft().getY()) {
                        map.put(MoveDirection.TOP, rectangle);
                    } else {
                        map.put(MoveDirection.BOTTOM, rectangle);
                    }
                    continue;
                }
                if (rectangle.isHorizontal()) {
                    if (rectangle.getTopLeft().getY() < center.getTopLeft().getY()) {
                        map.put(MoveDirection.TOP, rectangle);
                    } else {
                        map.put(MoveDirection.BOTTOM, rectangle);
                    }
                } else {
                    if (rectangle.getTopLeft().getX() < center.getTopLeft().getX()) {
                        map.put(MoveDirection.LEFT, rectangle);
                    } else {
                        map.put(MoveDirection.RIGHT, rectangle);
                    }
                }
            }
        }
        return map;
    }

    private PossMoveTileRecognizeParams getPossMoveTileRecognizeParams(int gameId) {
        Map<String, String> params = getChannelParams(gameId);

        int topLeftX = Integer.parseInt(params.get(POSS_MOVES_TOP_LEFT_X));
        int topLeftY = Integer.parseInt(params.get(POSS_MOVES_TOP_LEFT_Y));
        int bottomRightX = Integer.parseInt(params.get(POSS_MOVES_BOTTOM_RIGHT_X));
        int bottomRightY = Integer.parseInt(params.get(POSS_MOVES_BOTTOM_RIGHT_Y));
        int contourMinArea = Integer.parseInt(params.get(POSS_MOVES_CONTOUR_MIN_AREA));

        return new PossMoveTileRecognizeParams()
                .topLeft(new Point(topLeftX, topLeftY))
                .bottomRight(new Point(bottomRightX, bottomRightY))
                .contourMinArea(contourMinArea);
    }

    private MyTileRecognizeParams getMyTileRecognizeParams(int gameId) {
        Map<String, String> params = getChannelParams(gameId);

        int topLeftX = Integer.parseInt(params.get(MY_TILES_TOP_LEFT_X));
        int topLeftY = Integer.parseInt(params.get(MY_TILES_TOP_LEFT_Y));
        int bottomRightX = Integer.parseInt(params.get(MY_TILES_BOTTOM_RIGHT_X));
        int bottomRightY = Integer.parseInt(params.get(MY_TILES_BOTTOM_RIGHT_Y));
        int contourMinArea = Integer.parseInt(params.get(MY_TILES_CONTOUR_MIN_AREA));
        int blurCoefficient = Integer.parseInt(params.get(MY_TILES_BLUR_COEFFICIENT));
        boolean combinePoints = Boolean.parseBoolean(params.get(MY_TILES_COMBINE_POINTS));

        return new MyTileRecognizeParams()
                .topLeft(new Point(topLeftX, topLeftY))
                .bottomRight(new Point(bottomRightX, bottomRightY))
                .contourMinArea(contourMinArea)
                .blurCoefficient(blurCoefficient)
                .combinedPoints(combinePoints);
    }

    private MyBazaarTileRecognizeParams getMyBazaarTileRecognizeParams(int gameId, int bazaarTilesCount) {
        Map<String, String> params = getChannelParams(gameId);

        int screenWidth = Integer.parseInt(params.get(MY_BAZAAR_TILES_SCREEN_WIDTH));
        int tileWidth = Integer.parseInt(params.get(MY_BAZAAR_TILES_TILE_WIDTH));
        int tilesSpacing = Integer.parseInt(params.get(MY_BAZAAR_TILES_TILES_SPACING));
        int tileTop = Integer.parseInt(params.get(MY_BAZAAR_TILES_TILE_TOP));
        int tileBottom = Integer.parseInt(params.get(MY_BAZAAR_TILES_TILE_BOTTOM));

        return new MyBazaarTileRecognizeParams()
                .screenWidth(screenWidth)
                .tilesCount(bazaarTilesCount)
                .tileWidth(tileWidth)
                .tilesSpacing(tilesSpacing)
                .tileTop(tileTop)
                .tileBottom(tileBottom);
    }

    private Map<String, String> getChannelParams(int gameId) {
        Channel channel = CachedGames.getGameProperties(gameId).getChannel();
        return channel.getParams();
    }
}
