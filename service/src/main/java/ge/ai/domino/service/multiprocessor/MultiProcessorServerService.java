package ge.ai.domino.service.multiprocessor;

import ge.ai.domino.domain.exception.DAIException;

public interface MultiProcessorServerService {

    void startServer() throws DAIException;

    void stopServer() throws DAIException;
}
