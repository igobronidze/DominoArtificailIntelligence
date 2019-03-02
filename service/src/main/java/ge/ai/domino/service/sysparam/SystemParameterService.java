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

    List<SystemParameter> getSystemParameters(String key, SystemParameterType type) throws DAIException;

    String getStringParameterValue(SysParam parameter) throws DAIException;

    Integer getIntegerParameterValue(SysParam parameter) throws DAIException;

    Double getDoubleParameterValue(SysParam parameter) throws DAIException;

    Long getLongParameterValue(SysParam parameter) throws DAIException;

    List<Integer> getIntegerListParameterValue(SysParam parameter) throws DAIException;

    boolean getBooleanParameterValue(SysParam parameter) throws DAIException;
}