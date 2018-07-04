package ge.ai.domino.dao.sysparam;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.domain.sysparam.SystemParameterType;

import java.util.List;

public interface SystemParameterDAO {

    void addSystemParameter(SystemParameter systemParameter) throws DAIException;

    void editSystemParameter(SystemParameter systemParameter) throws DAIException;

    void deleteSystemParameter(String key) throws DAIException;

    List<SystemParameter> getSystemParameters(String key, SystemParameterType type);
}