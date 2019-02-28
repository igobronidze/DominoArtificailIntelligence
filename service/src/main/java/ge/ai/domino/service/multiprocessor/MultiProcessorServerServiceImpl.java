package ge.ai.domino.service.multiprocessor;

import ge.ai.domino.manager.multiprocessorserver.MultiProcessorServer;

public class MultiProcessorServerServiceImpl implements MultiProcessorServerService {

    private final MultiProcessorServer multiProcessorServer = MultiProcessorServer.getInstance();

    @Override
    public void startServer() {
        multiProcessorServer.startServer();
    }

    @Override
    public void stopServer() {
        multiProcessorServer.stopService();
    }
}
