package ge.ai.domino.manager;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.dao.played.PlayedGameDAO;
import ge.ai.domino.dao.played.PlayedGameDAOImpl;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.played.ReplayMoveInfo;
import ge.ai.domino.manager.function.FunctionManager;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.replaygame.ReplayGameManager;
import ge.ai.domino.manager.script.ScriptManager;
import ge.ai.domino.util.properties.DAIPropertiesUtil;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamesTest {

    private static final boolean CREATE_MODE = false;

    private static final List<Integer> idsForCreateMode = Arrays.asList(4, 5, 6, 7, 8, 9, 10);

    private static final String TEST_DAI_PROPERTIES_FILE_PATH = "../../dai/properties/dai_test.properties";

    private static final String scriptFilePath = "/gamesTest.sql";

    private static final Logger logger = Logger.getLogger(GamesTest.class);

    private static final FunctionManager functionManager = new FunctionManager();

    private static final PlayedGameDAO playedGameDAO = new PlayedGameDAOImpl();

    private static final ReplayGameManager replayGameManager = new ReplayGameManager();

    private static final ScriptManager scriptManager = new ScriptManager();

    @BeforeClass
    public static void startUp() {
        DAIPropertiesUtil.daiPropertiesFile = new File(TEST_DAI_PROPERTIES_FILE_PATH);

        try (BufferedReader br = new BufferedReader(new FileReader(GamesTest.class.getResource(scriptFilePath).getFile()))) {
            String line;
            StringBuilder script = new StringBuilder();
            while ((line = br.readLine()) != null) {
                script.append(line).append(System.lineSeparator());
            }

            scriptManager.executeUpdateScript(script.toString());
        } catch (IOException e) {
            logger.error("Can't read script file, path[" + scriptFilePath + "]");
        }
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        String sql = "DELETE FROM system_parameter; DELETE FROM arg_and_value; DELETE FROM channel;";
        PreparedStatement pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
        pstmt.executeUpdate();
    }

    @Test
    public void testGames() throws Exception {
        functionManager.initFunctions();

        List<Integer> idsForReplay = CREATE_MODE ? idsForCreateMode : playedGameDAO.getAllPlayedGame();

        List<GamesTestFailure> gamesTestFailures = new ArrayList<>();

        for (int id : idsForReplay) {
            logger.info("Starting test for game id[" + id + "]");

            List<PossibleMoves> possibleMovesList = new ArrayList<>();
            PossibleMovesWrapper possibleMovesWrapper = null;
            int index = 0;

            if (!CREATE_MODE) {
                possibleMovesWrapper = PossibleMovesWrapperMarshaller.unmarshall(new File(getClass().getResource("/" + id + ".xml").getFile()));
            }

            ReplayMoveInfo replayMoveInfo = replayGameManager.startReplayGame(id);
            int gameId = replayMoveInfo.getGameId();
            while (replayMoveInfo.getNextMove() != null) {
                replayMoveInfo = replayGameManager.replayMove(replayMoveInfo.getGameId(), replayMoveInfo.getMoveIndex());
                replayMoveInfo.setRound(CachedGames.getCurrentRound(gameId, true));
                if (replayMoveInfo.getNextMove() != null) {
                    Move nextMove = new Move(replayMoveInfo.getNextMove().getLeft(), replayMoveInfo.getNextMove().getRight(), replayMoveInfo.getNextMove().getDirection());
                    if (replayMoveInfo.getBestAiPrediction() != null) {
                        if (CREATE_MODE) {
                            PossibleMoves possibleMoves = new PossibleMoves();
                            possibleMoves.setIndex(index + 1);
                            possibleMoves.getMoves().add(getMove(nextMove));
                            possibleMovesList.add(possibleMoves);
                        } else {
                            PossibleMoves possibleMoves = possibleMovesWrapper.getPossibleMoves().get(index);
                            if (!contains(replayMoveInfo.getBestAiPrediction(), possibleMoves)) {
                                GamesTestFailure gamesTestFailure = new GamesTestFailure();
                                gamesTestFailure.setReplayMoveInfo(replayMoveInfo);
                                gamesTestFailure.setPossibleMoves(possibleMoves);
                                gamesTestFailure.setIndex(index + 1);
                                gamesTestFailures.add(gamesTestFailure);
                            }
                        }
                        index++;
                    }
                }
            }

            logger.info("Finished game for replay id[" + id + "]");
            if (CREATE_MODE) {
                createPossibleMovesFile(possibleMovesList, id);
            }
        }

        if (!CREATE_MODE) {
            logger.info("Test failure count[" + gamesTestFailures.size() + "]");
            for (int i = 0; i < gamesTestFailures.size(); i++) {
                GamesTestFailure gamesTestFailure = gamesTestFailures.get(i);
                logger.info("Failed move[" + (i + 1) + "]");
                logger.info("Move index[" + gamesTestFailure.getIndex() + "]");
                logger.info("Possible moves: ");
                gamesTestFailure.getPossibleMoves().getMoves().forEach(logger::info);
                logger.info("Next move: " + gamesTestFailure.getReplayMoveInfo().getNextMove());
                logger.info("Best prediction: " + gamesTestFailure.getReplayMoveInfo().getBestAiPrediction());
                gamesTestFailure.getReplayMoveInfo().getAiPredictions().forEach(logger::info);
                RoundLogger.logRoundFullInfo(gamesTestFailure.getReplayMoveInfo().getRound());
                logger.info("");
            }

            Assert.assertEquals(0, gamesTestFailures.size());
        }
    }

    private boolean contains(Move move, PossibleMoves possibleMoves) {
        for (ge.ai.domino.manager.Move m : possibleMoves.getMoves()) {
            if (move.getLeft() == m.getLeft() && move.getRight() == m.getRight() && move.getDirection() == m.getDirection()) {
                return true;
            }
        }
        return false;
    }

    private ge.ai.domino.manager.Move getMove(Move move) {
        ge.ai.domino.manager.Move result = new ge.ai.domino.manager.Move();
        result.setLeft(move.getLeft());
        result.setRight(move.getRight());
        result.setDirection(move.getDirection());
        return result;
    }

    private void createPossibleMovesFile(List<PossibleMoves> possibleMovesList, long gameId) {
        PossibleMovesWrapper possibleMovesWrapper = new PossibleMovesWrapper();
        possibleMovesWrapper.setPossibleMoves(possibleMovesList);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(gameId + ".xml"));
            PossibleMovesWrapperMarshaller.marshall(possibleMovesWrapper, bufferedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
