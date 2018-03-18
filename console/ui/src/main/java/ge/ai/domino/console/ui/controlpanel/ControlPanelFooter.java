package ge.ai.domino.console.ui.controlpanel;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

class ControlPanelFooter extends BorderPane {

    private static final int HEIGHT = 25;

    private Label leftLabel;

    private Label rightLabel;

    ControlPanelFooter() {
        initComponents();
        initUI();
    }

    private void initComponents() {
        leftLabel = new Label("");
        leftLabel.setStyle("-fx-font-family: sylfaen;");
        rightLabel = new Label("© Developed by sg 2017");
        rightLabel.setStyle("-fx-font-family: syfaen;");
        rightLabel.setPadding(new Insets(0, 5, 0, 0));
    }

    private void initUI() {
        this.setHeight(HEIGHT);
        this.setStyle("-fx-background-color: #EBE6E5");
        this.setLeft(leftLabel);
        this.setRight(rightLabel);
    }
}
