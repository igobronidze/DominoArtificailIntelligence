package ge.ai.domino.console.ui.main;

import ge.ai.domino.console.ui.sysparam.SystemParametersPane;
import ge.ai.domino.console.ui.util.Messages;
import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ControlPanel extends Application {

    private static Stage stage;

    private static BorderPane root;

    private static final int EXTRA_HEIGHT = 50;

    private static final int EXTRA_WIDTH = 20;

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
        String text = Messages.get("systemParameters");
        root.setCenter(new SystemParametersPane());
        root.setTop(new ControlPanelHeader(text));
        root.setBottom(new ControlPanelFooter());
    }

    public static Stage getStage() {
        return stage;
    }

    public static DoubleBinding getCenterHeightBinding() {
        return stage.heightProperty().subtract(ControlPanelHeader.LOGO_HEIGHT).subtract(ControlPanelFooter.HEIGHT).subtract(EXTRA_HEIGHT);
    }

    public static DoubleBinding getCenterWidthBinding() {
        return stage.widthProperty().subtract(EXTRA_WIDTH);
    }
}