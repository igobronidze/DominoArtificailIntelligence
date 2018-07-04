package ge.ai.domino.service.sysparam;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.domain.sysparam.SystemParameterType;
import ge.ai.domino.caching.sysparam.CachedSystemParameter;
import ge.ai.domino.dao.sysparam.SystemParameterDAO;
import ge.ai.domino.dao.sysparam.SystemParameterDAOImpl;
import ge.ai.domino.manager.sysparam.SystemParameterManager;

import java.util.List;

public class SystemParameterServiceImpl implements SystemParameterService {

    private final SystemParameterDAO systemParameterDAO = new SystemParameterDAOImpl();

    private final SystemParameterManager systemParameterManager = new SystemParameterManager();

    @Override
    public void addSystemParameter(SystemParameter systemParameter) throws DAIException {
        systemParameterDAO.addSystemParameter(systemParameter);
        CachedSystemParameter.reloadParams();
    }

    @Override
    public void editSystemParameter(SystemParameter systemParameter) throws DAIException {
        systemParameterDAO.editSystemParameter(systemParameter);
        CachedSystemParameter.reloadParams();
    }

    @Override
    public void deleteSystemParameter(String key) throws DAIException {
        systemParameterDAO.deleteSystemParameter(key);
        CachedSystemParameter.reloadParams();
    }

    @Override
    public List<SystemParameter> getSystemParameters(String key, SystemParameterType type) {
        return systemParameterDAO.getSystemParameters(key, type);
    }

    @Override
    public String getStringParameterValue(SysParam parameter) {
        return systemParameterManager.getStringParameterValue(parameter);
    }

    @Override
    public Integer getIntegerParameterValue(SysParam parameter) {
        return systemParameterManager.getIntegerParameterValue(parameter);
    }

    @Override
    public Double getDoubleParameterValue(SysParam parameter) {
        return systemParameterManager.getDoubleParameterValue(parameter);
    }

    @Override
    public Long getLongParameterValue(SysParam parameter) {
        return systemParameterManager.getLongParameterValue(parameter);
    }

    @Override
    public List<Integer> getIntegerListParameterValue(SysParam parameter) {
        return systemParameterManager.getIntegerListParameterValue(parameter);
    }

    @Override
    public boolean getBooleanParameterValue(SysParam parameter) {
        return systemParameterManager.getBooleanParameterValue(parameter);
    }
}
