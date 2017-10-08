package ge.ai.domino.console.ui.controlpanel;

import ge.ai.domino.console.ui.util.Messages;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ControlPanel extends Application {

    private static Stage stage;

    private static BorderPane root;

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.setTitle(Messages.get("controlPanel"));
        root = new BorderPane();
        initComponents();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    static void initComponents() {
        root.setCenter(new GamePropertiesPane());
        root.setTop(new ControlPanelMenuBar());
        root.setBottom(new ControlPanelFooter());
    }

    static Stage getStage() {
        return stage;
    }

    public static BorderPane getRoot() {
        return root;
    }

    public static Scene getScene() {
        return scene;
    }
}