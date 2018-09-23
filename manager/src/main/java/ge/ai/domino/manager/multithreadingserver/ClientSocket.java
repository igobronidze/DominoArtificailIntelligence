package ge.ai.domino.manager.multithreadingserver;

import ge.ai.domino.caching.sysparam.CachedSystemParameter;
import ge.ai.domino.dao.function.FunctionDAO;
import ge.ai.domino.dao.function.FunctionDAOImpl;
import ge.ai.domino.domain.command.MultithreadingCommand;
import ge.ai.domino.domain.game.GameInitialData;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientSocket {

    private static final Logger logger = Logger.getLogger(ClientSocket.class);

    private FunctionDAO functionDAO = new FunctionDAOImpl();

    private Socket socket;

    private ObjectInputStream ois;

    private ObjectOutputStream oos;

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public void sendSysParams() throws IOException {
        oos.writeObject(MultithreadingCommand.LOAD_SYS_PARAMS);
        oos.writeObject(CachedSystemParameter.getCachedParameters());
    }

    public void sendFunctionArgsAndValues() throws IOException {
        oos.writeObject(MultithreadingCommand.LOAD_FUNCTION_ARG_AND_VALUES);
        oos.writeObject(functionDAO.getFunctionArgsAndValues(""));
    }

    public void initGame(GameInitialData gameInitialData) throws IOException {
        oos.writeObject(MultithreadingCommand.INIT_GAME);
        oos.writeObject(gameInitialData);
    }

    public List<AiPredictionsWrapper> executeMinMax(List<Round> rounds) throws Exception {
        oos.writeObject(MultithreadingCommand.EXECUTE_MIN_MAX);
        oos.writeObject(rounds);
        return (List<AiPredictionsWrapper>) ois.readObject();
    }

    public void close() {
        try {
            oos.close();
            ois.close();
            socket.close();
        } catch (IOException ex) {
            logger.error("Can't close connection", ex);
        }
    }
}
