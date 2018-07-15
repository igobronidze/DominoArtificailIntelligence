package ge.ai.domino.service.p2p;

import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.p2pserver.P2PServer;

public class P2PServerServiceImpl implements P2PServerService {

    private final P2PServer p2PServer = new P2PServer();

    @Override
    public void startServer(GameProperties gameProperties) {
        p2PServer.startServer(gameProperties);
    }

    @Override
    public void stopServer() {
        p2PServer.stopService();
    }
}
