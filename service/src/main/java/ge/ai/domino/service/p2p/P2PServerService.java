package ge.ai.domino.service.p2p;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameProperties;

public interface P2PServerService {

    void startServer(GameProperties gameProperties) throws DAIException;

    void stopServer() throws DAIException;
}
