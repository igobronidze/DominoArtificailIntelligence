package ge.ai.domino.console.transfer.dto.sysparam;

import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.domain.sysparam.SystemParameterType;

import java.util.ArrayList;
import java.util.List;

public class SystemParameterDTO {

    private int id;

    private String key;

    private String value;

    private SystemParameterTypeDTO type;

    public SystemParameterDTO() {
    }

    public SystemParameterDTO(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public SystemParameterDTO(int id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public SystemParameterDTO(int id, String key, String value, SystemParameterTypeDTO type) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SystemParameterTypeDTO getType() {
        return type;
    }

    public void setType(SystemParameterTypeDTO type) {
        this.type = type;
    }

    public static SystemParameter toSystemParameter(SystemParameterDTO dto) {
        return new SystemParameter(dto.getId(), dto.getKey(), dto.getValue(), SystemParameterType.valueOf(dto.getType().name()));
    }

    public static SystemParameterDTO toSystemParameterDTO(SystemParameter systemParameter) {
        return new SystemParameterDTO(systemParameter.getId(), systemParameter.getKey(), systemParameter.getValue(),
                SystemParameterTypeDTO.valueOf(systemParameter.getType().name()));
    }

    public static List<SystemParameterDTO> toSystemParameterDTOList(List<SystemParameter> systemParameters) {
        List<SystemParameterDTO> dtoList = new ArrayList<>();
        for (SystemParameter systemParameter : systemParameters) {
            dtoList.add(toSystemParameterDTO(systemParameter));
        }
        return dtoList;
    }
}
