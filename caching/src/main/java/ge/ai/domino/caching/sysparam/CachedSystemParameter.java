package ge.ai.domino.caching.sysparam;

import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.dao.sysparam.SystemParameterDAO;
import ge.ai.domino.dao.sysparam.SystemParameterDAOImpl;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedSystemParameter {

    private static final Logger logger = Logger.getLogger(CachedSystemParameter.class);

    private static final SystemParameterDAO systemParameterDAO = new SystemParameterDAOImpl();

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
            logger.info("Reloading System parameters");
            cachedParameters = new HashMap<>();
            List<SystemParameter> systemParameterList = systemParameterDAO.getSystemParameters(null, null);
            for (SystemParameter systemParameter : systemParameterList) {
                cachedParameters.put(systemParameter.getKey(), systemParameter.getValue());
            }
            logger.info("Cached system parameters");
        } catch (Exception ex) {
            cachedParameters = null;
            logger.error("Can't cached system parameters", ex);
        }
    }

    public static void changeParameterOnlyInCache(String key, String value) {
        if (cachedParameters == null) {
            reloadParams();
        }
        cachedParameters.put(key, value);
    }
}