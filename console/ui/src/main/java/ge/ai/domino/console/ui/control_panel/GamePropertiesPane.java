package ge.ai.domino.console.ui.control_panel;

import ge.ai.domino.console.ui.TCHcomponents.TCHButton;
import ge.ai.domino.console.ui.TCHcomponents.TCHComboBox;
import ge.ai.domino.console.ui.TCHcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.TCHcomponents.TCHFieldLabel;
import ge.ai.domino.console.ui.TCHcomponents.TCHTextField;
import ge.ai.domino.console.ui.domino.DominoPane;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.GameProperties;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.util.domino.DominoService;
import ge.ai.domino.util.domino.DominoServiceImpl;
import ge.ai.domino.util.sysparam.SystemParameterService;
import ge.ai.domino.util.sysparam.SystemParameterServiceImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class GamePropertiesPane extends VBox {

    private static final SystemParameterService systemParameterService = new SystemParameterServiceImpl();

    private static final DominoService dominoService = new DominoServiceImpl();

    private static final SysParam possiblePoints = new SysParam("possiblePoints", "75,155,255");

    public GamePropertiesPane() {
        initComponents();
        initUI();
    }

    private void initUI() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(50, 0, 0, 0));
    }

    private void initComponents() {
        TCHTextField websiteField = new TCHTextField(TCHComponentSize.MEDIUM);
        TCHFieldLabel websiteFieldLabel = new TCHFieldLabel(Messages.get("website"), websiteField);
        TCHTextField nameField = new TCHTextField(TCHComponentSize.MEDIUM);
        TCHFieldLabel nameFieldLabel = new TCHFieldLabel(Messages.get("name"), nameField);
        List<Integer> points = systemParameterService.getIntegerListParameterValue(possiblePoints);
        List<Object> objPoints = new ArrayList<>();
        for (Integer point : points) {
            objPoints.add(point);
        }
        TCHComboBox pointComboBox = new TCHComboBox(objPoints);
        TCHFieldLabel pointFieldLabel = new TCHFieldLabel(Messages.get("point"), pointComboBox);
        CheckBox startCheckBox = new CheckBox();
        TCHFieldLabel startFieldLabel = new TCHFieldLabel(Messages.get("start"), startCheckBox);
        TCHButton startButton = new TCHButton(Messages.get("start"));
        startButton.setOnAction(e -> {
            GameProperties gameProperties = new GameProperties();
            gameProperties.setStart(startCheckBox.isSelected());
            gameProperties.setPointsForWin(Integer.parseInt(pointComboBox.getValue().toString()));
            gameProperties.setWebsite(websiteField.getText());
            gameProperties.setOpponentName(nameField.getText());
            Game game = dominoService.startGame(gameProperties);
            ControlPanel.getRoot().setCenter(new DominoPane(game));
        });
        this.getChildren().addAll(websiteFieldLabel, nameFieldLabel, pointFieldLabel, startFieldLabel, startButton);
    }
}
