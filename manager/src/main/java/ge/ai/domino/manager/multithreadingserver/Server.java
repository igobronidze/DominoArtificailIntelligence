package ge.ai.domino.manager.multithreadingserver;

import ge.ai.domino.domain.game.GameInitialData;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.ai.AiPredictionsWrapper;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class);

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam multithreadingServerPort = new SysParam("multithreadingServerPort", "8080");

    private ServerSocket server = null;

    private static List<ClientSocket> clients = new ArrayList<>();

    private boolean open;

    private static Server instance;

    private Server() {}

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void startServer() {
        int port = sysParamManager.getIntegerParameterValue(multithreadingServerPort);
        try {
            server = new ServerSocket(port);
            open = true;
            logger.info("Started multithreading server, port[" + port + "]");
            while (open) {
                Socket socket = server.accept();
                ClientSocket clientSocket = new ClientSocket(socket);
                clientSocket.sendSysParams();
                clientSocket.sendFunctionArgsAndValues();
                clients.add(clientSocket);
                logger.info("Accepted new client");
            }
        } catch (IOException ex) {
            logger.error("Can't start multithreading server, port[" + port + "]", ex);
        }
    }

    public void initGame(GameInitialData gameInitialData) {
        try {
            for (ClientSocket clientSocket : clients) {
                clientSocket.initGame(gameInitialData);
            }
        } catch (IOException ex) {
            logger.error("Error occurred while init game", ex);
        }
    }

    public int getClientsCount() {
        return clients.size();
    }

    public List<AiPredictionsWrapper> executeMinMax(int index, List<Round> rounds) {
        try {
            return clients.get(index).executeMinMax(rounds);
        } catch (Exception ex) {
            logger.error("Error occurred while execute minmax", ex);
        }
        return null;
    }

    public void stopService() {
        try {
            if (server != null) {
                server.close();
                open = false;
            }
            for (ClientSocket clientSocket : clients) {
                clientSocket.close();
            }
        } catch (IOException ex) {
            logger.error("Can't stop multithreading server, port[" + sysParamManager.getIntegerParameterValue(multithreadingServerPort) + "]", ex);
        }
    }
}
