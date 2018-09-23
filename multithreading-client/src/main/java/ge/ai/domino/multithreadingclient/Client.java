package ge.ai.domino.multithreadingclient;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

    private static final Logger logger = Logger.getLogger(Client.class);

    private static final String DEFAULT_HOST = "localhost";

    private static final int TIMEOUT = 3_000;

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam multithreadingServerPort = new SysParam("multithreadingServerPort", "8080");

    private boolean open = true;

    public void startClient() throws DAIException {
        int port = sysParamManager.getIntegerParameterValue(multithreadingServerPort);
        startClient(DEFAULT_HOST, port);
    }

    public void startClient(String host, int port) throws DAIException {
        try {
            while (open) {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host, port), TIMEOUT);
                logger.info("Connected to server, host[" + host + "], port[" + port + "]");

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                new ClientManager(ois, oos).startListen();
                oos.close();
                ois.close();
                socket.close();
            }
        } catch (IOException ex) {
            logger.error("Can't connect multithreading server, host[" + host + "], port" + port + "], timeout[" + TIMEOUT + "]", ex);
            throw new DAIException("cantConnectMultithreadingServer");
        }
    }

    public void stopService() {
        open = false;
    }
}
