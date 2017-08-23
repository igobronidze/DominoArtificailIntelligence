package ge.ai.domino.console.ui.control_panel;

import ge.ai.domino.console.ui.main.GamePropertiesPane;
import ge.ai.domino.console.ui.util.Messages;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ControlPanel extends Application {

    private static Stage stage;

    private static BorderPane root;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.setTitle(Messages.get("controlPanel"));
        root = new BorderPane();
        initComponents();
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void initComponents() {
        root.setCenter(new GamePropertiesPane());
        root.setTop(new ControlPanelMenuBar());
        root.setBottom(new ControlPanelFooter());
    }

    public static Stage getStage() {
        return stage;
    }
}