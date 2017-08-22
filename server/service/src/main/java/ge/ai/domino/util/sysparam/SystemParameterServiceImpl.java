package ge.ai.domino.util.sysparam;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.domain.sysparam.SystemParameterType;
import ge.ai.domino.server.caching.sysparam.CachedSystemParameter;
import ge.ai.domino.server.dao.sysparam.SystemParameterDAO;
import ge.ai.domino.server.dao.sysparam.SystemParameterDAOImpl;
import ge.ai.domino.server.processor.sysparam.SystemParameterProcessor;

import java.util.List;

public class SystemParameterServiceImpl implements SystemParameterService {

    private SystemParameterDAO systemParameterDAO = new SystemParameterDAOImpl();

    private SystemParameterProcessor systemParameterProcessor = new SystemParameterProcessor();

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
        return systemParameterProcessor.getStringParameterValue(parameter);
    }

    @Override
    public Integer getIntegerParameterValue(SysParam parameter) {
        return systemParameterProcessor.getIntegerParameterValue(parameter);
    }

    @Override
    public Float getFloatParameterValue(SysParam parameter) {
        return systemParameterProcessor.getFloatParameterValue(parameter);
    }

    @Override
    public Long getLongParameterValue(SysParam parameter) {
        return systemParameterProcessor.getLongParameterValue(parameter);
    }

    @Override
    public List<Integer> getIntegerListParameterValue(SysParam parameter) {
        return systemParameterProcessor.getIntegerListParameterValue(parameter);
    }

    @Override
    public boolean getBooleanParameterValue(SysParam parameter) {
        return systemParameterProcessor.getBooleanParameterValue(parameter);
    }
}
