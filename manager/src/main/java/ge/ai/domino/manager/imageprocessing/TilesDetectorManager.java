package ge.ai.domino.manager.imageprocessing;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.imageprocessing.TilesDetector;
import ge.ai.domino.imageprocessing.TilesDetectorParams;
import ge.ai.domino.manager.game.GameManager;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private static final String TMP_IMAGE_PREFIX = "game_";

    public static final String TMP_IMAGE_EXTENSION = ".png";

    private static final Logger logger = Logger.getLogger(GameManager.class);

    private final TilesDetector tilesDetector = new TilesDetector();

    private String tmpImagePath;

    public List<Tile> detectTiles(int gameId) throws DAIException {
        try {
            logger.info("Start detectTiles method");
            long ms = System.currentTimeMillis();
            Robot robot = new Robot();
            Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage Image = robot.createScreenCapture(capture);
            logger.info("Screenshot took " + (System.currentTimeMillis() - ms) + "ms");

            ms = System.currentTimeMillis();
            File tempFile = File.createTempFile(TMP_IMAGE_PREFIX + String.valueOf(gameId), TMP_IMAGE_EXTENSION);
            tmpImagePath = tempFile.getAbsolutePath();
            ImageIO.write(Image, "png", new File(tempFile.getAbsolutePath()));
            logger.info("Save of image took " + (System.currentTimeMillis() - ms) + "ms");

            ms = System.currentTimeMillis();
            List<Tile> tiles = balanceTiles(tilesDetector.getTiles(tempFile.getAbsolutePath(), getTilesDetectorParams(gameId)));
            logger.info("Detection took " + (System.currentTimeMillis() - ms) + "ms");
            logger.info("Detected tiles: " + tiles);
            return tiles;
        } catch (Exception ex) {
            logger.error("Error occurred while detect tiles", ex);
            throw new DAIException("cantDetectTiles");
        }
    }

    private TilesDetectorParams getTilesDetectorParams(int gameId) {
        Channel channel = CachedGames.getGameProperties(gameId).getChannel();
        Map<String, String> params = channel.getParams();

        int contourMinArea = params.containsKey(CONTOUR_MIN_AREA_KEY) ? Integer.valueOf(params.get(CONTOUR_MIN_AREA_KEY)) : CONTOUR_MIN_AREA;
        double heightPercentage = params.containsKey(TILES_HEIGHT_PERCENTAGE_KEY) ? Double.valueOf(params.get(TILES_HEIGHT_PERCENTAGE_KEY)) : TILES_HEIGHT_PERCENTAGE;
        double marginBottomPercentage = params.containsKey(TILES_MARGIN_BOTTOM_PERCENTAGE_KEY) ? Double.valueOf(params.get(TILES_MARGIN_BOTTOM_PERCENTAGE_KEY)) : TILES_MARGIN_BOTTOM_PERCENTAGE;
        double marginLeftPercentage = params.containsKey(TILES_MARGIN_LEFT_PERCENTAGE_KEY) ? Double.valueOf(params.get(TILES_MARGIN_LEFT_PERCENTAGE_KEY)) : TILES_MARGIN_LEFT_PERCENTAGE;
        double widthPercentage = params.containsKey(TILES_WIDTH_PERCENTAGE_KEY) ? Double.valueOf(params.get(TILES_WIDTH_PERCENTAGE_KEY)) : TILES_WIDTH_PERCENTAGE;

        return new TilesDetectorParams()
                .contourMinArea(contourMinArea)
                .heightPercentage(heightPercentage)
                .marginBottomPercentage(marginBottomPercentage)
                .marginLeftPercentage(marginLeftPercentage)
                .widthPercentage(widthPercentage);
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

    public String getTmpImagePath() {
        return tmpImagePath;
    }
}
