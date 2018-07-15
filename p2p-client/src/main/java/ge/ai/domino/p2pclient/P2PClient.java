package ge.ai.domino.p2pclient;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class P2PClient {

    private static final Logger logger = Logger.getLogger(P2PClient.class);

    private static final String HOST = "localhost";

    private static final int PORT = 8080;

    private static final int TIMEOUT = 3_000;

    public GameInfo startClient() throws DAIException {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(HOST, PORT), TIMEOUT);
            logger.info("Connected to server, host[" + HOST + "], port[" + PORT + "]");

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            GameInfo gameInfo = new P2PGame(ois, oos).start();

            socket.close();
            return gameInfo;
        } catch (IOException ex) {
            logger.error("Can't connect p2p server, host[" + HOST + "], port" + PORT + "], timeout[" + TIMEOUT + "]", ex);
            throw new DAIException("cantConnectP2PServer");
        }
    }
}
