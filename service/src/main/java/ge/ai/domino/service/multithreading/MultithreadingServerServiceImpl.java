package ge.ai.domino.service.multithreading;

import ge.ai.domino.manager.multithreadingserver.MultithreadingServer;

public class MultithreadingServerServiceImpl implements MultithreadingServerService {

    private final MultithreadingServer server = MultithreadingServer.getInstance();

    @Override
    public void startServer() {
        server.startServer();
    }

    @Override
    public void stopServer() {
        server.stopService();
    }
}
