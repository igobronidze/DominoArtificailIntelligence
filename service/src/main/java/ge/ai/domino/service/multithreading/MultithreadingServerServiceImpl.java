package ge.ai.domino.service.multithreading;

import ge.ai.domino.manager.multithreadingserver.Server;

public class MultithreadingServerServiceImpl implements MultithreadingServerService {

    private final Server server = Server.getInstance();

    @Override
    public void startServer() {
        server.startServer();
    }

    @Override
    public void stopServer() {
        server.stopService();
    }
}
