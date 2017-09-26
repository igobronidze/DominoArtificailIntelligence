package ge.ai.domino.console.ui.util.dialog;

import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.domain.exception.DAIException;
import javafx.scene.control.Alert;

public class WarnDialog {

    public static void showWarnDialog(String textKey) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(Messages.get("warn"));
        alert.setContentText(Messages.get(textKey));
        alert.getDialogPane().setStyle("-fx-font-family: sylfaen;");
        alert.showAndWait();
    }

    public static void showWarnDialog(DAIException ex) {
        showWarnDialog(ex.getMessageKey());
    }
}
