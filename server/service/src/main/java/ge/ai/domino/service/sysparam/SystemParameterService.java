package ge.ai.domino.service.sysparam;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.domain.sysparam.SystemParameterType;

import java.util.List;

public interface SystemParameterService {

    void addSystemParameter(SystemParameter systemParameter) throws DAIException;

    void editSystemParameter(SystemParameter systemParameter) throws DAIException;

    void deleteSystemParameter(String key) throws DAIException;

    List<SystemParameter> getSystemParameters(String key, SystemParameterType type);

    String getStringParameterValue(SysParam parameter);

    Integer getIntegerParameterValue(SysParam parameter);

    Double getDoubleParameterValue(SysParam parameter);

    Long getLongParameterValue(SysParam parameter);

    List<Integer> getIntegerListParameterValue(SysParam parameter);

    boolean getBooleanParameterValue(SysParam parameter);
}