package ge.ai.domino.service.initial;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.initial.InitialData;

public interface InitialDataService {

    InitialData getInitialData() throws DAIException;

    void playInitialExtraMoves() throws DAIException;
}
