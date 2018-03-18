package ge.ai.domino.console.ui.shortcut;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ShortcutProperty {

    private StringProperty key;

    private StringProperty value;

    ShortcutProperty(String key, String value) {
        this.key = new SimpleStringProperty(key);
        this.value = new SimpleStringProperty(value);
    }

    public String getKey() {
        return key.get();
    }

    public StringProperty keyProperty() {
        return key;
    }

    public void setKey(String key) {
        this.key.set(key);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}
