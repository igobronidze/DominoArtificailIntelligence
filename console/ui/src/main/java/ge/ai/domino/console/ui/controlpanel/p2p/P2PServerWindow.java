package ge.ai.domino.console.ui.controlpanel.p2p;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComboBox;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.service.sysparam.SystemParameterService;
import ge.ai.domino.service.sysparam.SystemParameterServiceImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public abstract class P2PServerWindow {

    private static final SystemParameterService systemParameterService = new SystemParameterServiceImpl();

    private static final SysParam possiblePoints = new SysParam("possiblePoints", "75,155,175,255,355");

    public void showWindow(Stage primaryStage) {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
        stage.setTitle(Messages.get("p2pServer"));

        TCHLabel label = new TCHLabel(Messages.get("selectPointOfWin"));
        List<Integer> points = systemParameterService.getIntegerListParameterValue(possiblePoints);
        TCHComboBox<Integer> pointComboBox = new TCHComboBox<>(points);

        TCHButton startButton = new TCHButton(Messages.get("start"));
        TCHButton stopButton = new TCHButton(Messages.get("stop"));
        startButton.setOnAction(event -> {
            onStart(Integer.parseInt(pointComboBox.getValue().toString()));
            startButton.setDisable(true);
            stopButton.setDisable(false);
        });
        stopButton.setOnAction(event -> {
            onStop();
            stopButton.setDisable(true);
            startButton.setDisable(false);
        });
        stopButton.setDisable(true);
        HBox hBox = new HBox(25);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().addAll(startButton, stopButton);
        VBox vBox = new VBox(30);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(label, pointComboBox, hBox);

        stage.setOnCloseRequest(event -> onStop());
        stage.setScene(new Scene(vBox));
        stage.setWidth(350);
        stage.setHeight(220);
        stage.showAndWait();
    }

    public abstract void onStart(int pointOfWin);

    public abstract void onStop();
}
