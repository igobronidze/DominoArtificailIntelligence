package ge.ai.domino.p2pserver;

import ge.ai.domino.domain.game.GameProperties;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PServer {

    private static final int PORT = 8080;

    private static final Logger logger = Logger.getLogger(P2PServer.class);

    private ServerSocket server = null;

    private Socket firstPlayer = null;

    private Socket secondPlayer = null;

    private boolean open;

    public void startServer(GameProperties gameProperties) {
        try {
            server = new ServerSocket(PORT);
            open = true;
            logger.info("Started p2p server, port[" + PORT + "]");
            while (open) {
                firstPlayer = server.accept();
                logger.info("Accepted first player");
                secondPlayer = server.accept();
                logger.info("Accepted second player");
                new Thread(new P2PGame(firstPlayer, secondPlayer, gameProperties)).start();
                logger.info("Started p2p game, pointOfWin[" + gameProperties.getPointsForWin() + "]");
            }
        } catch (IOException ex) {
            logger.error("Can't start p2p server, port[" + PORT + "]", ex);
        }
    }

    public void stopService() {
        try {
            if (server != null) {
                server.close();
                open = false;
            }
            if (firstPlayer != null) {
                firstPlayer.close();
            }
            if (secondPlayer != null) {
                secondPlayer.close();
            }
        } catch (IOException ex) {
            logger.error("Can't stop p2p server, port[" + PORT + "]", ex);
        }
    }
}
