package ge.ai.domino.console.ui.TCHcomponents;

import javafx.scene.control.TextArea;

public class TCHTextArea extends TextArea {

    public TCHTextArea() {
        initComponent();
    }

    public TCHTextArea(String text) {
        this();
        this.setText(text);
    }

    private void initComponent() {
        this.setStyle("-fx-font-family: sylfaen; -fx-font-size: 12px;");
    }
}
