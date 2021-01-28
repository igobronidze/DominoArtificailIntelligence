package ge.ai.domino.manager.imageprocessing;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.imageprocessing.recognizer.MyTileRecognizeParams;
import ge.ai.domino.imageprocessing.recognizer.PossMoveTileRecognizeParams;
import ge.ai.domino.imageprocessing.recognizer.TableRecognizer;
import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.imageprocessing.service.Rectangle;
import ge.ai.domino.imageprocessing.service.table.IPPossMoveTile;
import ge.ai.domino.imageprocessing.service.table.IPTile;
import ge.ai.domino.manager.game.GameManager;
import ge.ai.domino.robot.ScreenRobot;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
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

    public Rectangle getPossibleMoveRectangle(int gameId, MoveDirection moveDirection) {
        logger.info("Start get possible move rectangles method");
        PossMoveTileRecognizeParams params = getPossMoveTileRecognizeParams(gameId);
        List<IPPossMoveTile> possMoveTiles = TableRecognizer.recognizePossMoveTiles(lastImage, params);
        List<Rectangle> possMoveRectangles = possMoveTiles.stream()
                .map(possMoveTile -> new Rectangle(possMoveTile.getTopLeft(), possMoveTile.getBottomRight()))
                .collect(Collectors.toList());
        return getRelevantRectangle(possMoveRectangles, moveDirection, params);
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

    protected Rectangle getRelevantRectangle(List<Rectangle> rectangles, MoveDirection direction, PossMoveTileRecognizeParams params) {
        Rectangle result = null;
        switch (direction) {
            case RIGHT:
                for (Rectangle rectangle : rectangles) {
                    if (!rectangle.isHorizontal() && rectangle.getBottomRight().getX() > params.getBottomRight().getX() - 180) {
                        return rectangle;
                    }
                    if (rectangle.isHorizontal()) {
                        if (result == null || result.getTopLeft().getX() < rectangle.getTopLeft().getX()) {
                            result = rectangle;
                        }
                    }
                }
                break;
            case LEFT:
                for (Rectangle rectangle : rectangles) {
                    if (!rectangle.isHorizontal() && rectangle.getTopLeft().getX() < params.getTopLeft().getX() + 180) {
                        return rectangle;
                    }
                    if (rectangle.isHorizontal()) {
                        if (result == null || result.getTopLeft().getX() > rectangle.getTopLeft().getX()) {
                            result = rectangle;
                        }
                    }
                }
                break;
            case BOTTOM:
                for (Rectangle rectangle : rectangles) {
                    if (rectangle.isHorizontal() && rectangle.getBottomRight().getY() > params.getBottomRight().getY() - 80) {
                        return rectangle;
                    }
                    if (!rectangle.isHorizontal()) {
                        if (result == null || result.getTopLeft().getY() < rectangle.getTopLeft().getY()) {
                            result = rectangle;
                        }
                    }
                }
                break;
            case TOP:
                for (Rectangle rectangle : rectangles) {
                    if (rectangle.isHorizontal() && rectangle.getTopLeft().getY() < params.getTopLeft().getY() + 80) {
                        return rectangle;
                    }
                    if (!rectangle.isHorizontal()) {
                        if (result == null || result.getTopLeft().getY() > rectangle.getTopLeft().getY()) {
                            result = rectangle;
                        }
                    }
                }
                break;
        }
        return result;
    }

    private PossMoveTileRecognizeParams getPossMoveTileRecognizeParams(int gameId) {
        Channel channel = CachedGames.getGameProperties(gameId).getChannel();
        Map<String, String> params = channel.getParams();

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
        Channel channel = CachedGames.getGameProperties(gameId).getChannel();
        Map<String, String> params = channel.getParams();

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
}
