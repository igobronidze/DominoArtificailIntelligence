package ge.ai.domino.manager.multiprocessorserver;

import ge.ai.domino.caching.sysparam.CachedSystemParameter;
import ge.ai.domino.dao.client.ClientDAO;
import ge.ai.domino.dao.client.ClientDAOImpl;
import ge.ai.domino.dao.function.FunctionDAO;
import ge.ai.domino.dao.function.FunctionDAOImpl;
import ge.ai.domino.domain.client.Client;
import ge.ai.domino.domain.command.MultiProcessorCommand;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInitialData;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ClientSocket {

    private static final Logger logger = Logger.getLogger(ClientSocket.class);

    private FunctionDAO functionDAO = new FunctionDAOImpl();

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam multiProcessorClientRankSysParam = new SysParam("multiProcessorClientRankSysParam", "minMaxIteration");

    private ClientDAO clientDAO = new ClientDAOImpl();

    private Client client;

    private int rank;

    private Socket socket;

    private ObjectInputStream ois;

    private ObjectOutputStream oos;

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public void specifyClient() throws IOException, ClassNotFoundException, DAIException {
        oos.writeObject(MultiProcessorCommand.GET_CLIENT_ID);
        Integer clientId = (Integer) ois.readObject();

        client = clientDAO.getClientById(clientId);

        if (client == null) {
            logger.warn("Can't find client by id - " + clientId);
            throw new DAIException("clientNotFound");
        }
    }

    public void sendSysParams() throws IOException {
        Map<String, String> sysParams = CachedSystemParameter.getCachedParameters();
        sysParams.putAll(client.getParams());

        oos.writeObject(MultiProcessorCommand.LOAD_SYS_PARAMS);
        oos.writeObject(sysParams);

        rank = Integer.valueOf(sysParams.get(sysParamManager.getStringParameterValue(multiProcessorClientRankSysParam)));
    }

    public void updateSysParams(Map<String, String> params) throws IOException {
        oos.writeObject(MultiProcessorCommand.UPDATE_SYS_PARAMS_IN_CACH);
        oos.writeObject(params);
    }

    public void sendFunctionArgsAndValues() throws IOException {
        oos.writeObject(MultiProcessorCommand.LOAD_FUNCTION_ARG_AND_VALUES);
        oos.writeObject(functionDAO.getFunctionArgsAndValues(""));
    }

    public void initGame(GameInitialData gameInitialData) throws IOException {
        oos.writeObject(MultiProcessorCommand.INIT_GAME);
        oos.writeObject(gameInitialData);
    }

    public void executeRankTest() throws IOException, ClassNotFoundException {
        oos.writeObject(MultiProcessorCommand.RANK_TEST);
        List<Long> result = (List<Long>) ois.readObject();

        logger.info("Rank test for client[" + client.getName() + "], rank[" + rank + "]");
        logger.info(result);

        double average = 0.0;
        for (Long ms : result) {
            average += ms;
        }
        logger.info("Average for " + result.size() + " try is " + (average / result.size()));
    }

    public List<AiPredictionsWrapper> executeMinMax(List<MultiProcessorRound> multiProcessorRounds) throws Exception {
        Long taskId = System.currentTimeMillis();
        logger.info("Starting minmax execution, roundsCount[" + multiProcessorRounds.size() + "], clientName[" + client.getName() + "], taskId[" + taskId + "]");
        long ms = System.currentTimeMillis();

        oos.writeObject(MultiProcessorCommand.EXECUTE_MIN_MAX);
        oos.writeObject(taskId);
        oos.writeObject(multiProcessorRounds);
        List<AiPredictionsWrapper> aiPredictionsWrappers = (List<AiPredictionsWrapper>) ois.readObject();
        logger.info("MinMax for clientName[" + client.getName() + "] took " + (System.currentTimeMillis() - ms) + "ms");

        return aiPredictionsWrappers;
    }

    public String getName() {
        return client.getName();
    }

    public int getRank() {
        return rank;
    }

    public void close() {
        try {
            oos.writeObject(MultiProcessorCommand.FINISH);
            ois.close();
            oos.close();
            socket.close();
        } catch (IOException ex) {
            logger.error("Can't close connection", ex);
        }
    }
}
