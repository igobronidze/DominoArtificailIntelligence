package ge.ai.domino.console.ui.controlpanel;

import ge.ai.domino.console.ui.game.SaveGameWindow;
import ge.ai.domino.console.ui.gameproperties.GamePropertiesPane;
import ge.ai.domino.console.ui.util.Messages;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ControlPanel extends Application {

    private Stage stage;

    private BorderPane root;

    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        primaryStage.setTitle(Messages.get("Domino"));
        root = new BorderPane();
        initComponents();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        stage.setOnCloseRequest(we -> {
            if (AppController.round != null) {
                new SaveGameWindow() {
                    @Override
                    public void onSave() {
                        AppController.round = null;
                    }

                    @Override
                    public void onCancel() {
                        we.consume();
                    }
                }.showWindow();
            }
        });
    }

    void initComponents() {
        root.setCenter(new GamePropertiesPane(this));
        root.setTop(new ControlPanelMenuBar(this));
        root.setBottom(new ControlPanelFooter());
    }

    Stage getStage() {
        return stage;
    }

    public BorderPane getRoot() {
        return root;
    }

    public Scene getScene() {
        return scene;
    }
}