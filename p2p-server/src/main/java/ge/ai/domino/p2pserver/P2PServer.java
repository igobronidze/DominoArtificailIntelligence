package ge.ai.domino.p2pserver;

import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2PServer {

    private static final Logger logger = Logger.getLogger(P2PServer.class);

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam p2pServerPort = new SysParam("p2pServerPort", "8080");

    private ServerSocket server = null;

    private Socket firstPlayer = null;

    private Socket secondPlayer = null;

    private boolean open;

    public void startServer(GameProperties gameProperties) {
        int port = sysParamManager.getIntegerParameterValue(p2pServerPort);
        try {
            server = new ServerSocket(port);
            open = true;
            logger.info("Started p2p server, port[" + port + "]");
            while (open) {
                firstPlayer = server.accept();
                logger.info("Accepted first player");
                secondPlayer = server.accept();
                logger.info("Accepted second player");
                new Thread(new P2PGame(firstPlayer, secondPlayer, gameProperties)).start();
                logger.info("Started p2p game, pointOfWin[" + gameProperties.getPointsForWin() + "]");
            }
        } catch (IOException ex) {
            logger.error("Can't start p2p server, port[" + port + "]", ex);
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
            logger.error("Can't stop p2p server, port[" + sysParamManager.getIntegerParameterValue(p2pServerPort) + "]", ex);
        }
    }
}
