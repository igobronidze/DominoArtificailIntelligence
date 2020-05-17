package ge.ai.domino.p2pserver;

import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.game.GameProperties;

public class ServerRunner {

    public static void main(String[] args) throws Exception {
        GameProperties gameProperties = new GameProperties();
        gameProperties.setPointsForWin(175);
        gameProperties.setChannel(new Channel());
        gameProperties.setOpponentName("Name");

        P2PServer p2PServer = new P2PServer();
        p2PServer.startServer(gameProperties);
    }
}
