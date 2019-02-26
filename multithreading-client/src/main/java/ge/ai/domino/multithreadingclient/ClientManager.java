package ge.ai.domino.multithreadingclient;

import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.caching.sysparam.CachedSystemParameter;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.command.MultithreadingCommand;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.function.FunctionArgsAndValues;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameInitialData;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.played.PlayedMove;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.function.FunctionManager;
import ge.ai.domino.manager.game.GameManager;
import ge.ai.domino.manager.game.ai.minmax.CachedMinMax;
import ge.ai.domino.manager.game.ai.minmax.MinMax;
import ge.ai.domino.manager.game.ai.minmax.MinMaxFactory;
import ge.ai.domino.manager.game.ai.minmax.NodeRound;
import ge.ai.domino.manager.game.helper.initial.InitialUtil;
import ge.ai.domino.manager.multithreadingserver.MultithreadingRound;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientManager {

    private static final Logger logger = Logger.getLogger(ClientManager.class);

    private static final int GAME_ID_ADDITION = 1000;

    private static final FunctionManager functionManager = new FunctionManager();

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam rankTestCount = new SysParam("rankTestCount", "5");

    private ObjectInputStream ois;

    private ObjectOutputStream oos;

    private String name;

    public ClientManager(ObjectInputStream ois, ObjectOutputStream oos, String name) {
        this.ois = ois;
        this.oos = oos;
        this.name = name;
    }

    public void startListen() throws DAIException {
        while (true) {
            try {
                MultithreadingCommand command = (MultithreadingCommand) ois.readObject();
                logger.info("Get command[" + command + "]");

                boolean finish = false;
                switch (command) {
                    case FINISH:
                        finish = true;
                        break;
                    case GET_NAME:
                        oos.writeObject(name);
                        break;
                    case LOAD_SYS_PARAMS:
                        Map<String, String> sysParams = (Map<String, String>) ois.readObject();
                        CachedSystemParameter.setCachedParameters(sysParams);
                        logger.info("Load sys params: " + sysParams);
                        break;
                    case UPDATE_SYS_PARAMS_IN_CACH:
                        Map<String, String> params = (Map<String, String>) ois.readObject();
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            sysParamManager.changeParameterOnlyInCache(entry.getKey(), entry.getValue());
                        }
                        logger.info("Updated sys params: " + params);
                        break;
                    case LOAD_FUNCTION_ARG_AND_VALUES:
                        Map<String, FunctionArgsAndValues> functionArgsAndValues = (Map<String, FunctionArgsAndValues>) ois.readObject();
                        functionManager.setFunctions(functionArgsAndValues, true);
                        logger.info("Load function args and values: " + functionArgsAndValues);
                        break;
                    case RANK_TEST:
                        executeRankTest();
                        break;
                    case INIT_GAME:
                        GameInitialData gameInitialData = (GameInitialData) ois.readObject();
                        logger.info("Get gameInitialData");

                        Game game = new Game();
                        game.setId(gameInitialData.getGameId() + GAME_ID_ADDITION);
                        GameProperties gameProperties = new GameProperties();
                        gameProperties.setPointsForWin(gameInitialData.getPointsForWin());
                        gameProperties.setChannel(new Channel());
                        game.setProperties(gameProperties);
                        CachedGames.addGame(game);
                        break;
                    case EXECUTE_MIN_MAX:
                        Long taskId = (Long)ois.readObject();
                        List<MultithreadingRound> multithreadingRounds = (List<MultithreadingRound>) ois.readObject();
                        logger.info(String.format("Starting minmax taskId[%s], count[%s]", taskId, multithreadingRounds.size()));

                        long ms = System.currentTimeMillis();
                        List<AiPredictionsWrapper> aiPredictionsWrappers = new ArrayList<>();
                        for (MultithreadingRound multithreadingRound : multithreadingRounds) {
                            Round round = multithreadingRound.getRound();
                            round.getGameInfo().setGameId(round.getGameInfo().getGameId() + GAME_ID_ADDITION);
                            MinMax minMax = MinMaxFactory.getMinMax(false);
                            minMax.setThreadCount(multithreadingRounds.size());

                            NodeRound nodeRound = new NodeRound();
                            nodeRound.setId(multithreadingRound.getId());
                            nodeRound.setRound(round);
                            nodeRound.setLastPlayedMove(new PlayedMove());
                            nodeRound.getLastPlayedMove().setType(multithreadingRound.getLastPlayedMoveType());
                            aiPredictionsWrappers.add(minMax.minMaxForNodeRound(nodeRound));
                        }
                        logger.info("MinMax for all round took " + (System.currentTimeMillis() - ms) + "ms");

                        oos.writeObject(aiPredictionsWrappers);
                        break;
                }

                if (finish) {
                    break;
                }
            } catch (ClassNotFoundException | IOException ex) {
                logger.error("Error occurred while play minmax for multithreading", ex);
                throw new DAIException("unexpectedError");
            }
        }
    }

    private void executeRankTest() throws IOException {
        List<Long> result = new ArrayList<>();
        int gameId = 0;

        try {
            gameId = initGame();
            NodeRound nodeRound = new NodeRound();
            nodeRound.setRound(CachedGames.getCurrentRound(gameId, true));

            for (int i = 0; i < sysParamManager.getIntegerParameterValue(rankTestCount); i++) {
                long ms = System.currentTimeMillis();

                MinMax minMax = MinMaxFactory.getMinMax(false);
                minMax.minMaxForNodeRound(nodeRound);

                long ans = System.currentTimeMillis() - ms;
                result.add(ans);
                logger.info("Rank test MinMax took " + ans + "ms");
            }

            double average = 0.0;
            for (Long ms : result) {
                average += ms;
            }
            logger.info("Average for " + result.size() + " try is " + (average / result.size()));

            oos.writeObject(result);
        } catch (DAIException ex) {
            logger.error("Error occurred while execute rank test");
        } finally {
            CachedGames.removeGame(gameId);
            CachedMinMax.cleanUp(gameId);
        }
    }

    @SuppressWarnings("Duplicates")
    private int initGame() throws DAIException {
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

        GameManager gameManager = new GameManager();

        gameManager.addTileForMe(gameId, 5, 4);
        gameManager.addTileForMe(gameId, 6, 3);
        gameManager.addTileForMe(gameId, 6, 4);
        gameManager.addTileForMe(gameId, 3, 2);
        gameManager.addTileForMe(gameId, 2, 0);
        gameManager.addTileForMe(gameId, 1, 1);
        gameManager.addTileForMe(gameId, 5, 3);

        gameManager.specifyRoundBeginner(gameId, false);

        return gameId;
    }
}
