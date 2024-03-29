package ge.ai.domino.manager.initial;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.initial.InitialData;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.imageprocessing.recognizer.MyTileRecognizeParams;
import ge.ai.domino.imageprocessing.recognizer.TableRecognizer;
import ge.ai.domino.imageprocessing.service.Point;
import ge.ai.domino.manager.game.GameManager;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.helper.initial.InitialUtil;
import ge.ai.domino.manager.util.ProjectVersionUtil;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class InitialDataManager {

    private static final Logger logger = Logger.getLogger(GameManager.class);

    private static final String INITIAL_EXTRA_TILES_IMAGE_PATH = "../properties/domino.png";

    private final GameManager gameManager = new GameManager();

    public InitialData getInitialData() {
        InitialData initialData = new InitialData();
        String version = ProjectVersionUtil.getVersion();
        initialData.setVersion(version);
        return initialData;
    }

    public void playInitialExtraMoves() {
        int gameId = 0;
        try {
            detectTiles();

            logger.info("Start extra moves");
            gameId = initGame();

            gameManager.addTileForMe(gameId, 5, 4);
            gameManager.addTileForMe(gameId, 6, 3);
            gameManager.addTileForMe(gameId, 6, 4);
            gameManager.addTileForMe(gameId, 3, 2);
            gameManager.addTileForMe(gameId, 2, 0);
            gameManager.addTileForMe(gameId, 1, 1);
            gameManager.addTileForMe(gameId, 5, 3);

            gameManager.specifyRoundBeginner(gameId, false);

            gameManager.playForOpponent(gameId, new Move(6, 6, MoveDirection.LEFT));
            gameManager.playForMe(gameId, new Move(6, 3, MoveDirection.LEFT));
            gameManager.playForOpponent(gameId, new Move(3, 1, MoveDirection.LEFT));
            gameManager.playForMe(gameId, new Move(1, 1, MoveDirection.LEFT));
            gameManager.playForOpponent(gameId, new Move(5, 1, MoveDirection.LEFT));
            gameManager.playForMe(gameId, new Move(5, 3, MoveDirection.LEFT));
            gameManager.playForOpponent(gameId, new Move(3, 0, MoveDirection.LEFT));
        } catch (DAIException | IOException ex) {
            logger.error("Error occurred while play initial extra moves");
        } finally {
            CachedGames.removeGame(gameId);
            CachedMinMax.cleanUp(gameId);

            logger.info("Finished extra moves");
        }
    }

    private void detectTiles() throws IOException {
        logger.info("Start extra tiles detect method");
        BufferedImage img = ImageIO.read(new File(INITIAL_EXTRA_TILES_IMAGE_PATH));
        TableRecognizer.recognizeMyTiles(img, geMyTilesRecognizeParams());
        logger.info("Finished extra tiles detect method");
    }

    private MyTileRecognizeParams geMyTilesRecognizeParams() {
        return new MyTileRecognizeParams()
                .topLeft(new Point(310, 655))
                .bottomRight(new Point(1050, 725))
                .contourMinArea(200)
                .blurCoefficient(1)
                .combinedPoints(true);
    }

    private int initGame() {
        GameProperties gameProperties = new GameProperties();
        gameProperties.setOpponentName("Test");
        gameProperties.setPointsForWin(175);
        Channel channel = new Channel();
        channel.setName("Test");
        gameProperties.setChannel(channel);

        Game game = InitialUtil.getInitialGame(gameProperties, false);
        int gameId = game.getId();

        game.setId(gameId);
        CachedGames.addGame(game);
        return gameId;
    }
}
