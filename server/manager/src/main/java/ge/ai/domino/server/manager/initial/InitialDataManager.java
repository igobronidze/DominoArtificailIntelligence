package ge.ai.domino.server.manager.initial;

import ge.ai.domino.domain.initial.InitialData;
import ge.ai.domino.server.manager.util.ProjectVersionUtil;

public class InitialDataManager {

    public InitialData getInitialData() {
        InitialData initialData = new InitialData();
        String version = ProjectVersionUtil.getVersion();
        initialData.setVersion(version);
        return initialData;
    }
}
