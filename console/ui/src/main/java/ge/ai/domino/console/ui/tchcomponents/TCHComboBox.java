package ge.ai.domino.console.ui.tchcomponents;

import javafx.scene.control.ComboBox;

import java.util.List;

public class TCHComboBox<T> extends ComboBox<T> {

    public TCHComboBox(List<T> values) {
        initComponent(values);
    }

    private void initComponent(List<T> values) {
        this.setStyle("-fx-font-family: sylfaen; -fx-font-size: 14px;");
        this.setPrefWidth(220);
        this.getItems().addAll(values);
        if (this.getItems().size() != 0) {
            this.setValue(this.getItems().get(0));
        }
    }
}
