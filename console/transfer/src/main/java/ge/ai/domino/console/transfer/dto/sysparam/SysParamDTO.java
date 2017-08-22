package ge.ai.domino.console.transfer.dto.sysparam;

import ge.ai.domino.domain.sysparam.SysParam;

public class SysParamDTO {

    private String key;

    private String defaultValue;

    public SysParamDTO() {
    }

    public SysParamDTO(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public static SysParam toSysParam(SysParamDTO dto) {
        return new SysParam(dto.getKey(), dto.getDefaultValue());
    }
}
