package ge.ai.domino.service.p2p;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;

public interface P2PClientService {

    GameInfo startClient() throws DAIException;
}
