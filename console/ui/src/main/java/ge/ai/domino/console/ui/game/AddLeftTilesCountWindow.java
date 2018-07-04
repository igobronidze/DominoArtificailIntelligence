package ge.ai.domino.console.ui.game;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHNumberTextField;
import ge.ai.domino.console.ui.util.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

abstract class AddLeftTilesCountWindow {

	void showWindow() {
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setTitle(Messages.get("addLeftCount"));
		TCHNumberTextField countField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
		TCHButton button = new TCHButton(Messages.get("add"));
		button.setOnAction(event -> {
			onSave(countField.getNumber().intValue());
			stage.close();
		});
		VBox vBox = new VBox(10);
		vBox.setPadding(new Insets(20));
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.getChildren().addAll(countField, button);
		stage.setScene(new Scene(vBox));
		stage.setWidth(300);
		stage.setHeight(100);
		stage.showAndWait();
	}

	public abstract void onSave(int count);
}
