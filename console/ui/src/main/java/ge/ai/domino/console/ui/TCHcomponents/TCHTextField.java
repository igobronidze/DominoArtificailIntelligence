package ge.ai.domino.console.ui.tchcomponents;

import javafx.scene.control.TextField;

public class TCHTextField extends TextField {

	public TCHTextField(TCHComponentSize size) {
		initComponent(size);
	}

	public TCHTextField(String text, TCHComponentSize size) {
		initComponent(size);
		this.setText(text);
	}

	private void initComponent(TCHComponentSize size) {
		this.setStyle("-fx-font-family: sylfaen; -fx-font-size: 14px;");
		switch (size) {
			case SMALL:
				this.setPrefWidth(150);
				this.setMaxWidth(150);
				break;
			case MEDIUM:
				this.setPrefWidth(300);
				this.setMaxWidth(300);
				break;
			case LARGE:
				this.setPrefWidth(500);
				this.setMaxWidth(500);
				break;
		}
	}
}
