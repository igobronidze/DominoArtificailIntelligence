package ge.ai.domino.p2pclient;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class P2PClient {

    private static final Logger logger = Logger.getLogger(P2PClient.class);

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam p2pServerPort = new SysParam("p2pServerPort", "8080");

    private static final String HOST = "localhost";

    private static final int TIMEOUT = 3_000;

    public void startClient() throws DAIException {
        int port = sysParamManager.getIntegerParameterValue(p2pServerPort);
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(HOST, port), TIMEOUT);
            logger.info("Connected to server, host[" + HOST + "], port[" + port + "]");

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            new P2PGame(ois, oos).start();

            socket.close();
        } catch (IOException ex) {
            logger.error("Can't connect p2p server, host[" + HOST + "], port" + port + "], timeout[" + TIMEOUT + "]", ex);
            throw new DAIException("cantConnectP2PServer");
        }
    }
}
