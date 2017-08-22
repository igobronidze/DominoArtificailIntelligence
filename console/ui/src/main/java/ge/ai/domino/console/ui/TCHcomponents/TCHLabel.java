package ge.ai.domino.console.ui.TCHcomponents;

import javafx.scene.control.Label;

public class TCHLabel extends Label {

	public TCHLabel(String text) {
		initComponent(text);
	}

	private void initComponent(String text) {
		this.setStyle("-fx-font-family: sylfaen; -fx-font-size: 14px;");
		this.setText(text);
	}
}
