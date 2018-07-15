package ge.ai.domino.p2pserver;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PServer {

    private static final int PORT = 8080;

    private static final Logger logger = Logger.getLogger(P2PServer.class);

    private ServerSocket server = null;

    private boolean open;

    public void startServer(GameProperties gameProperties) throws DAIException {
        try {
            server = new ServerSocket(PORT);
            open = true;
            logger.info("Started p2p server, port[" + PORT + "]");
            while (open) {
                Socket firstPlayer = server.accept();
                logger.info("Accepted first player");
                Socket secondPlayer = server.accept();
                logger.info("Accepted second player");
                new Thread(new P2PGame(firstPlayer, secondPlayer, gameProperties)).start();
                logger.info("Started p2p game");
            }
        } catch (IOException ex) {
            logger.error("Can't p2p start server, port[" + PORT + "]", ex);
            throw new DAIException("canNotStartP2PServer");
        }
    }

    public void stopService() throws DAIException {
        try {
            if (server != null) {
                server.close();
                open = false;
            }
        } catch (IOException ex) {
            throw new DAIException("canNotCloseP2PServer");
        }
    }
}
