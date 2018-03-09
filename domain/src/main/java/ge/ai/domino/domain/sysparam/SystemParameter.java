package ge.ai.domino.domain.sysparam;

import java.io.Serializable;

public class SystemParameter implements Serializable {

    private int id;

    private String key;

    private String value;

    private SystemParameterType type;

    public SystemParameter(int id, String key, String value, SystemParameterType type) {
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

    public SystemParameterType getType() {
        return type;
    }

    public void setType(SystemParameterType type) {
        this.type = type;
    }
}
