package ge.ai.domino.service.initial;

import ge.ai.domino.domain.initial.InitialData;
import ge.ai.domino.manager.initial.InitialDataManager;

public class InitialDataServiceImpl implements InitialDataService {

    private final InitialDataManager initialDataManager = new InitialDataManager();

    @Override
    public InitialData getInitialData() {
        return initialDataManager.getInitialData();
    }

    @Override
    public void playInitialExtraMoves() {
        initialDataManager.playInitialExtraMoves();
    }
}
