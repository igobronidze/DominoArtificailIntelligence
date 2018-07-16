package ge.ai.domino.service.p2p;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.p2pclient.P2PClient;

public class P2PClientServiceImpl implements P2PClientService {

    private final P2PClient p2PClient = new P2PClient();

    @Override
    public void startClient() throws DAIException {
        p2PClient.startClient();
    }
}
