package ge.ai.domino.manager.multithreadingserver;

import ge.ai.domino.domain.game.GameInitialData;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MultithreadingServer {

    private static final Logger logger = Logger.getLogger(MultithreadingServer.class);

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam multithreadingServerPort = new SysParam("multithreadingServerPort", "8080");

    private final SysParam executeExtraMinMaxForMultithreadingClient = new SysParam("executeExtraMinMaxForMultithreadingClient", "true");

    private final SysParam executeRankTestForMultithreadingClient = new SysParam("executeRankTestForMultithreadingClient", "true");

    private ServerSocket server = null;

    private static List<ClientSocket> clients = new ArrayList<>();

    private boolean open;

    private static MultithreadingServer instance;

    private MultithreadingServer() {}

    public static MultithreadingServer getInstance() {
        if (instance == null) {
            instance = new MultithreadingServer();
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
                clientSocket.specifyClientName();

                clientSocket.sendSysParams();
                clientSocket.sendFunctionArgsAndValues();
                if (sysParamManager.getBooleanParameterValue(executeExtraMinMaxForMultithreadingClient)) {
                    clientSocket.executeExtraMinMax();
                }
                if (sysParamManager.getBooleanParameterValue(executeRankTestForMultithreadingClient)) {
                    clientSocket.executeRankTest();
                }
                clients.add(clientSocket);
                logger.info("Accepted new client, name[" + clientSocket.getName() + "]");
            }
        } catch (IOException | ClassNotFoundException ex) {
            logger.error("Can't start multithreading server, port[" + port + "]", ex);
            stopService();
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

    public List<ClientSocket> getClients() {
        return clients;
    }

    public void stopService() {
        try {
            if (server != null) {
                open = false;
                clients.forEach(ClientSocket::close);
                server.close();
            }
        } catch (IOException ex) {
            logger.error("Can't stop multithreading server, port[" + sysParamManager.getIntegerParameterValue(multithreadingServerPort) + "]", ex);
        }
    }
}
