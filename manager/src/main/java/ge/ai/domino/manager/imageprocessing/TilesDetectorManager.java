package ge.ai.domino.manager.imageprocessing;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.imageprocessing.TileContour;
import ge.ai.domino.imageprocessing.TilesDetector;
import ge.ai.domino.imageprocessing.TilesDetectorParams;
import ge.ai.domino.manager.game.GameManager;
import ge.ai.domino.robot.ScreenRobot;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TilesDetectorManager {

    private static final double TILES_HEIGHT_PERCENTAGE = 15;

    private static final String TILES_HEIGHT_PERCENTAGE_KEY = "tilesDetectorHeightPercentage";

    private static final double TILES_MARGIN_BOTTOM_PERCENTAGE = 5;

    private static final String TILES_MARGIN_BOTTOM_PERCENTAGE_KEY = "tilesDetectorMarginBottomPercentage";

    private static final double TILES_WIDTH_PERCENTAGE = 70;

    private static final String TILES_WIDTH_PERCENTAGE_KEY = "tilesDetectorWidthPercentage";

    private static final double TILES_MARGIN_LEFT_PERCENTAGE =  15;

    private static final String TILES_MARGIN_LEFT_PERCENTAGE_KEY = "tilesDetectorMarginLeftPercentage";

    private static final int CONTOUR_MIN_AREA = 200;

    private static final String CONTOUR_MIN_AREA_KEY = "tilesDetectorContour";

    private static final String BLUR_COEFFICIENT_KEY = "blurCoefficient";

    private static final int BLUR_COEFFICIENT = 3;

    private static final String COMBINE_POINTS_KEY = "combinePoints";

    private static final boolean COMBINE_POINTS = true;

    public static final String TMP_IMAGE_EXTENSION = ".png";

    private static final String SECOND_PARAM_SUFFIX = "_2";

    private static final Logger logger = Logger.getLogger(GameManager.class);

    private final TilesDetector tilesDetector = new TilesDetector();

    private BufferedImage lastImage;

    public List<Tile> detectTiles(int gameId, boolean withSecondParams) throws DAIException {
        try {
            logger.info("Start detectTiles method");
            long ms = System.currentTimeMillis();
            lastImage = ScreenRobot.getScreenCapture();
            logger.info("Screenshot took " + (System.currentTimeMillis() - ms) + "ms");

            ms = System.currentTimeMillis();
            List<TileContour> tileContours = tilesDetector.getTiles(lastImage, getTilesDetectorParams(gameId, withSecondParams));
            List<Tile> tiles = balanceTiles(tileContours.stream().map(TileContour::getTile).collect(Collectors.toList()));
            logger.info("Detection took " + (System.currentTimeMillis() - ms) + "ms");
            logger.info("Detected tiles: " + tiles);
            return tiles;
        } catch (Exception ex) {
            logger.error("Error occurred while detect tiles", ex);
            throw new DAIException("cantDetectTiles");
        }
    }

    public TileContour detectTileContour(int gameId, int left, int right) throws DAIException {
        try {
            logger.info("Start detectTileContour method");
            long ms = System.currentTimeMillis();

            lastImage = ScreenRobot.getScreenCapture();

            logger.info("Screenshot took " + (System.currentTimeMillis() - ms) + "ms");

            ms = System.currentTimeMillis();
            List<TileContour> tileContours = tilesDetector.getTiles(lastImage, getTilesDetectorParams(gameId, false));
            TileContour detectedContour = null;
            for (TileContour tileContour : tileContours) {
                if ((tileContour.getTile().getLeft() == left && tileContour.getTile().getRight() == right) ||
                        (tileContour.getTile().getLeft() == right && tileContour.getTile().getRight() == left)) {
                    detectedContour = tileContour;
                }
            }

            logger.info("Detection took " + (System.currentTimeMillis() - ms) + "ms");
            return detectedContour;
        } catch (Exception ex) {
            logger.error("Error occurred while detect tile contour", ex);
            throw new DAIException("cantDetectTiles");
        }
    }

    private TilesDetectorParams getTilesDetectorParams(int gameId, boolean withSecondParams) {
        Channel channel = CachedGames.getGameProperties(gameId).getChannel();
        Map<String, String> params = channel.getParams();

        int contourMinArea = params.containsKey(CONTOUR_MIN_AREA_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))
                ? Integer.parseInt(params.get(CONTOUR_MIN_AREA_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))) : CONTOUR_MIN_AREA;
        double heightPercentage = params.containsKey(TILES_HEIGHT_PERCENTAGE_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))
                ? Double.parseDouble(params.get(TILES_HEIGHT_PERCENTAGE_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))) : TILES_HEIGHT_PERCENTAGE;
        double marginBottomPercentage = params.containsKey(TILES_MARGIN_BOTTOM_PERCENTAGE_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))
                ? Double.parseDouble(params.get(TILES_MARGIN_BOTTOM_PERCENTAGE_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))) : TILES_MARGIN_BOTTOM_PERCENTAGE;
        double marginLeftPercentage = params.containsKey(TILES_MARGIN_LEFT_PERCENTAGE_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))
                ? Double.parseDouble(params.get(TILES_MARGIN_LEFT_PERCENTAGE_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))) : TILES_MARGIN_LEFT_PERCENTAGE;
        double widthPercentage = params.containsKey(TILES_WIDTH_PERCENTAGE_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))
                ? Double.parseDouble(params.get(TILES_WIDTH_PERCENTAGE_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))) : TILES_WIDTH_PERCENTAGE;
        int blurCoefficient = params.containsKey(BLUR_COEFFICIENT_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))
                ? Integer.parseInt(params.get(BLUR_COEFFICIENT_KEY  + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))) : BLUR_COEFFICIENT;
        boolean combinedPoints = params.containsKey(COMBINE_POINTS_KEY + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))
                ? Boolean.parseBoolean(params.get(COMBINE_POINTS_KEY  + (withSecondParams ? SECOND_PARAM_SUFFIX : ""))) : COMBINE_POINTS;

        return new TilesDetectorParams()
                .contourMinArea(contourMinArea)
                .heightPercentage(heightPercentage)
                .marginBottomPercentage(marginBottomPercentage)
                .marginLeftPercentage(marginLeftPercentage)
                .widthPercentage(widthPercentage)
                .blurCoefficient(blurCoefficient)
                .combinedPoints(combinedPoints);
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

    public BufferedImage getLastImage() {
        return lastImage;
    }
}
