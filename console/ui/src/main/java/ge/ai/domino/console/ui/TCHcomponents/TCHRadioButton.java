package ge.ai.domino.console.ui.TCHcomponents;

import javafx.scene.control.RadioButton;

public class TCHRadioButton extends RadioButton {

    public TCHRadioButton() {
        initComponent();
    }

    public TCHRadioButton(String text) {
        this();
        this.setText(text);
    }

    private void initComponent() {
        this.setStyle("-fx-font-family: sylfaen; -fx-font-size: 14px;");
    }
}
