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
import ge.ai.domino.manager.function.FunctionManager;
import ge.ai.domino.manager.game.ai.minmax.MinMax;
import ge.ai.domino.manager.game.ai.minmax.MinMaxFactory;
import ge.ai.domino.manager.game.ai.minmax.NodeRound;
import ge.ai.domino.manager.game.logging.RoundLogger;
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
                    case LOAD_FUNCTION_ARG_AND_VALUES:
                        Map<String, FunctionArgsAndValues> functionArgsAndValues = (Map<String, FunctionArgsAndValues>) ois.readObject();
                        functionManager.setFunctions(functionArgsAndValues, true);
                        logger.info("Load function args and values: " + functionArgsAndValues);
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
                        List<Round> rounds = (List<Round>) ois.readObject();
                        logger.info("Starting minmax for rounds, count[" + rounds.size() + "]");

                        long ms = System.currentTimeMillis();
                        List<AiPredictionsWrapper> aiPredictionsWrappers = new ArrayList<>();
                        for (Round round : rounds) {
                            logger.info("Starting minmax for round:");
                            RoundLogger.logRoundFullInfo(round);
                            round.getGameInfo().setGameId(round.getGameInfo().getGameId() + GAME_ID_ADDITION);
                            MinMax minMax = MinMaxFactory.getMinMax(false);
                            minMax.setThreadCount(rounds.size());

                            NodeRound nodeRound = new NodeRound();
                            nodeRound.setRound(round);
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
}
