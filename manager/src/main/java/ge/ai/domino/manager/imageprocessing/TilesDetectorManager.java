package ge.ai.domino.manager.imageprocessing;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.imageprocessing.TilesDetector;
import ge.ai.domino.manager.game.GameManager;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TilesDetectorManager {

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
            List<Tile> tiles = balanceTiles(tilesDetector.getTiles(tempFile.getAbsolutePath()));
            logger.info("Detection took " + (System.currentTimeMillis() - ms) + "ms");
            logger.info("Detected tiles: " + tiles);
            return tiles;
        } catch (Exception ex) {
            logger.error("Error occurred while detect tiles", ex);
            throw new DAIException("cantDetectTiles");
        }
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
