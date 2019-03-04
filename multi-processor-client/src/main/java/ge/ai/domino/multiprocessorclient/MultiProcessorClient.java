package ge.ai.domino.multiprocessorclient;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MultiProcessorClient {

    private static final Logger logger = Logger.getLogger(MultiProcessorClient.class);

    private static final String DEFAULT_HOST = "localhost";

    private static final int DEFAULT_CLIENT_ID = 1;

    private static final int TIMEOUT = 3_000;

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam multiProcessorServerPort = new SysParam("multiProcessorServerPort", "8080");

    public void startClient() throws DAIException {
        int port = sysParamManager.getIntegerParameterValue(multiProcessorServerPort);
        startClient(DEFAULT_HOST, port, DEFAULT_CLIENT_ID);
    }

    public void startClient(String host, int port, Integer clientId) throws DAIException {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), TIMEOUT);
            logger.info("Connected to server, host[" + host + "], port[" + port + "]");

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            if (clientId == null) {
                clientId = DEFAULT_CLIENT_ID;
            }

            new MultiProcessorClientManager(ois, oos, clientId).startListen();
            ois.close();
            oos.close();
            socket.close();
        } catch (IOException ex) {
            logger.error("Can't connect multiProcessor server, host[" + host + "], port[" + port + "], timeout[" + TIMEOUT + "]", ex);
            throw new DAIException("cantConnectMultiProcessorServer");
        }
    }
}
