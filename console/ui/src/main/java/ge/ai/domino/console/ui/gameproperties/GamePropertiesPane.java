package ge.ai.domino.console.ui.gameproperties;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.console.ui.controlpanel.ControlPanel;
import ge.ai.domino.console.ui.game.GamePane;
import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComboBox;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHFieldLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHTextField;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.service.game.GameService;
import ge.ai.domino.service.game.GameServiceImpl;
import ge.ai.domino.service.sysparam.SystemParameterService;
import ge.ai.domino.service.sysparam.SystemParameterServiceImpl;
import ge.ai.domino.util.string.StringUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class GamePropertiesPane extends VBox {

    private static final SystemParameterService systemParameterService = new SystemParameterServiceImpl();

    private static final GameService GAME_SERVICE = new GameServiceImpl();

    private static final SysParam possiblePoints = new SysParam("possiblePoints", "75,155,175,255,355");

    private final ControlPanel controlPanel;

    public GamePropertiesPane(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
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
        List<Object> objPoints = new ArrayList<>(points);
        TCHComboBox pointComboBox = new TCHComboBox(objPoints);
        TCHFieldLabel pointFieldLabel = new TCHFieldLabel(Messages.get("point"), pointComboBox);
        TCHButton startButton = new TCHButton(Messages.get("start"));
        startButton.setOnAction(e -> {
            if (StringUtil.isEmpty(websiteField.getText()) || StringUtil.isEmpty(nameField.getText())) {
                WarnDialog.showWarnDialog(Messages.get("pleaseFillAllField"));
            } else {
                GameProperties gameProperties = new GameProperties();
                gameProperties.setPointsForWin(Integer.parseInt(pointComboBox.getValue().toString()));
                gameProperties.setWebsite(websiteField.getText());
                gameProperties.setOpponentName(nameField.getText());
                try {
                    AppController.round =  GAME_SERVICE.startGame(gameProperties, 0);
                    controlPanel.getRoot().setCenter(new GamePane(controlPanel, gameProperties));
                } catch (DAIException ex) {
                    WarnDialog.showWarnDialog(ex);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    WarnDialog.showUnexpectedError();
                }
            }
        });
        this.getChildren().addAll(nameFieldLabel, websiteFieldLabel, pointFieldLabel, startButton);
    }
}
