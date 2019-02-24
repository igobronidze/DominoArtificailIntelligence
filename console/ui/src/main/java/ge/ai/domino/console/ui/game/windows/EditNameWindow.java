package ge.ai.domino.console.ui.game.windows;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHTextField;
import ge.ai.domino.console.ui.util.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class EditNameWindow {

	public void showWindow() {
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setTitle(Messages.get("editName"));
		TCHLabel label = new TCHLabel(Messages.get("pleaseSpecifyNewName"));
		TCHTextField nameTextField = new TCHTextField(TCHComponentSize.MEDIUM);
		TCHButton saveButton = new TCHButton(Messages.get("save"));
		saveButton.setOnAction(event -> {
			onSave(nameTextField.getText());
			stage.close();
		});
		TCHButton cancelButton = new TCHButton(Messages.get("cancel"));
		cancelButton.setOnAction(event -> {
			onCancel();
			stage.close();
		});
		HBox hBox = new HBox(25);
		hBox.setAlignment(Pos.TOP_CENTER);
		hBox.getChildren().addAll(saveButton, cancelButton);
		VBox vBox = new VBox(30);
		vBox.setPadding(new Insets(20));
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.getChildren().addAll(label, nameTextField, hBox);
		stage.setScene(new Scene(vBox));
		stage.setWidth(390);
		stage.setHeight(215);
		stage.showAndWait();
	}

	public abstract void onSave(String name);

	public abstract void onCancel();
}
