package ge.ai.domino.console.transfer.manager.sysparam;

import ge.ai.domino.console.transfer.dto.exception.DAIConsoleException;
import ge.ai.domino.console.transfer.dto.sysparam.SysParamDTO;
import ge.ai.domino.console.transfer.dto.sysparam.SystemParameterDTO;
import ge.ai.domino.console.transfer.dto.sysparam.SystemParameterTypeDTO;

import java.util.List;

public interface SystemParameterManager {

    void addSystemParameter(SystemParameterDTO systemParameter) throws DAIConsoleException;

    void editSystemParameter(SystemParameterDTO systemParameter) throws DAIConsoleException;

    void deleteSystemParameter(String key) throws DAIConsoleException;

    List<SystemParameterDTO> getSystemParameters(String key, SystemParameterTypeDTO type);

    String getStringParameterValue(SysParamDTO parameter);

    Integer getIntegerParameterValue(SysParamDTO parameter);

    Float getFloatParameterValue(SysParamDTO parameter);

    Long getLongParameterValue(SysParamDTO parameter);

    List<Integer> getIntegerListParameterValue(SysParamDTO parameter);

    boolean getBooleanParameterValue(SysParamDTO parameter);
}
