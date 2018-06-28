package ge.ai.domino.service.initial;

import ge.ai.domino.domain.initial.InitialData;
import ge.ai.domino.server.manager.initial.InitialDataManager;

public class InitialDataServiceImpl implements InitialDataService {

    private final InitialDataManager initialDataManager = new InitialDataManager();

    @Override
    public InitialData getInitialData() {
        return initialDataManager.getInitialData();
    }
}
