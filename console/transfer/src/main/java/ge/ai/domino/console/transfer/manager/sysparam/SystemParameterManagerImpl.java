package ge.ai.domino.console.transfer.manager.sysparam;

import ge.ai.domino.console.transfer.dto.exception.DAIConsoleException;
import ge.ai.domino.console.transfer.dto.sysparam.SysParamDTO;
import ge.ai.domino.console.transfer.dto.sysparam.SystemParameterDTO;
import ge.ai.domino.console.transfer.dto.sysparam.SystemParameterTypeDTO;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SystemParameterType;
import ge.ai.domino.util.sysparam.SystemParameterService;
import ge.ai.domino.util.sysparam.SystemParameterServiceImpl;

import java.util.List;

public class SystemParameterManagerImpl implements SystemParameterManager {

    private static final SystemParameterService systemParameterService = new SystemParameterServiceImpl();

    @Override
    public void addSystemParameter(SystemParameterDTO systemParameter) throws DAIConsoleException {
        try {
            systemParameterService.addSystemParameter(SystemParameterDTO.toSystemParameter(systemParameter));
        } catch (DAIException ex) {
            throw new DAIConsoleException(ex);
        }
    }

    @Override
    public void editSystemParameter(SystemParameterDTO systemParameter) throws DAIConsoleException {
        try {
            systemParameterService.editSystemParameter(SystemParameterDTO.toSystemParameter(systemParameter));
        } catch (DAIException ex) {
            throw new DAIConsoleException(ex);
        }
    }

    @Override
    public void deleteSystemParameter(String key) throws DAIConsoleException {
        try {
            systemParameterService.deleteSystemParameter(key);
        } catch (DAIException ex) {
            throw new DAIConsoleException(ex);
        }
    }

    @Override
    public List<SystemParameterDTO> getSystemParameters(String key, SystemParameterTypeDTO type) {
        SystemParameterType systemParameterType = type == null ? null : SystemParameterType.valueOf(type.name());
        return SystemParameterDTO.toSystemParameterDTOList(systemParameterService.getSystemParameters(key, systemParameterType));
    }

    @Override
    public String getStringParameterValue(SysParamDTO parameter) {
        return systemParameterService.getStringParameterValue(SysParamDTO.toSysParam(parameter));
    }

    @Override
    public Integer getIntegerParameterValue(SysParamDTO parameter) {
        return systemParameterService.getIntegerParameterValue(SysParamDTO.toSysParam(parameter));
    }

    @Override
    public Float getFloatParameterValue(SysParamDTO parameter) {
        return systemParameterService.getFloatParameterValue(SysParamDTO.toSysParam(parameter));
    }

    @Override
    public Long getLongParameterValue(SysParamDTO parameter) {
        return systemParameterService.getLongParameterValue(SysParamDTO.toSysParam(parameter));
    }

    @Override
    public List<Integer> getIntegerListParameterValue(SysParamDTO parameter) {
        return systemParameterService.getIntegerListParameterValue(SysParamDTO.toSysParam(parameter));
    }

    @Override
    public boolean getBooleanParameterValue(SysParamDTO parameter) {
        return systemParameterService.getBooleanParameterValue(SysParamDTO.toSysParam(parameter));
    }
}
