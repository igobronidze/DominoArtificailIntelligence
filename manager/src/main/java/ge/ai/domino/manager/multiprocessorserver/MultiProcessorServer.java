package ge.ai.domino.manager.multiprocessorserver;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInitialData;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiProcessorServer {

    private static final Logger logger = Logger.getLogger(MultiProcessorServer.class);

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam multiProcessorServerPort = new SysParam("multiProcessorServerPort", "8080");

    private final SysParam executeRankTestForMultiProcessorClient = new SysParam("executeRankTestForMultiProcessorClient", "true");

    private ServerSocket server = null;

    private static List<ClientSocket> clients = new ArrayList<>();

    private boolean open;

    private static MultiProcessorServer instance;

    private MultiProcessorServer() {}

    public static MultiProcessorServer getInstance() {
        if (instance == null) {
            instance = new MultiProcessorServer();
        }
        return instance;
    }

    public void startServer() {
        int port = sysParamManager.getIntegerParameterValue(multiProcessorServerPort);
        try {
            server = new ServerSocket(port);
            open = true;
            logger.info("Started multiProcessor server, port[" + port + "]");
            while (open) {
                try {
                    Socket socket = server.accept();
                    ClientSocket clientSocket = new ClientSocket(socket);
                    clientSocket.specifyClient();

                    clientSocket.sendSysParams();
                    clientSocket.sendFunctionArgsAndValues();
                    if (sysParamManager.getBooleanParameterValue(executeRankTestForMultiProcessorClient)) {
                        clientSocket.executeRankTest();
                    }
                    clients.add(clientSocket);
                    logger.info("Accepted new client, name[" + clientSocket.getName() + "]");
                } catch (DAIException ex) {
                    logger.error("Error occurred while connect client", ex);
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            logger.error("Can't start multiProcessor server, port[" + port + "]", ex);
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

    public void updateSysParams(Map<String, String> params) {
        try {
            for (ClientSocket clientSocket : clients) {
                clientSocket.updateSysParams(params);
            }
        } catch (IOException ex) {
            logger.error("Error occurred while update sys params", ex);
        }
    }

    public void stopService() {
        try {
            if (server != null) {
                open = false;
                clients.forEach(ClientSocket::close);
                server.close();
            }
        } catch (IOException ex) {
            logger.error("Can't stop multiProcessor server, port[" + sysParamManager.getIntegerParameterValue(multiProcessorServerPort) + "]", ex);
        }
    }
}
