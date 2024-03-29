package ge.ai.domino.manager.sysparam;

import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.caching.sysparam.CachedSystemParameter;
import ge.ai.domino.util.string.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SystemParameterManager {

    public String getStringParameterValue(SysParam parameter) {
        return CachedSystemParameter.getStringParameterValue(parameter);
    }

    public Integer getIntegerParameterValue(SysParam parameter) {
        return Integer.parseInt(getStringParameterValue(parameter));
    }

    public Double getDoubleParameterValue(SysParam parameter) {
        return Double.parseDouble(getStringParameterValue(parameter));
    }

    public Long getLongParameterValue(SysParam parameter) {
        return Long.parseLong(getStringParameterValue(parameter));
    }

    public boolean getBooleanParameterValue(SysParam parameter) {
        return Boolean.parseBoolean(getStringParameterValue(parameter));
    }

    public List<Integer> getIntegerListParameterValue(SysParam parameter) {
        String text = getStringParameterValue(parameter);
        if (StringUtil.isEmpty(text)) {
            return new ArrayList<>();
        }
        return StringUtil.getIntegerListFromString(text);
    }

    public void changeParameterOnlyInCache(String key, String value) {
        CachedSystemParameter.changeParameterOnlyInCache(key, value);
    }
}
