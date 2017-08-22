package ge.ai.domino.server.caching.sysparam;

import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.server.dao.sysparam.SystemParameterDAO;
import ge.ai.domino.server.dao.sysparam.SystemParameterDAOImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedSystemParameter {

    private static SystemParameterDAO systemParameterDAO = new SystemParameterDAOImpl();

    private static Map<String,String> cachedParameters;

    public static String getStringParameterValue(SysParam parameter) {
        if (cachedParameters == null) {
            reloadParams();
        }
        if (cachedParameters == null) {
            return parameter.getDefaultValue();
        }
        String value = cachedParameters.get(parameter.getKey());
        if (value != null) {
            return value;
        } else {
            return parameter.getDefaultValue();
        }
    }

    public static void reloadParams() {
        try {
            cachedParameters = new HashMap<>();
            List<SystemParameter> systemParameterList = systemParameterDAO.getSystemParameters(null, null);
            for (SystemParameter systemParameter : systemParameterList) {
                cachedParameters.put(systemParameter.getKey(), systemParameter.getValue());
            }
        } catch (Exception ex) {
            cachedParameters = null;
            ex.printStackTrace();
            System.out.println("can't catch system parameters... all parameter value mast be default...");
        }
    }
}