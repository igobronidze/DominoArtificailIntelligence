package ge.ai.domino.console.ui.main;

import ge.ai.domino.console.transfer.dto.sysparam.SysParamDTO;
import ge.ai.domino.console.transfer.manager.sysparam.SystemParameterManager;
import ge.ai.domino.console.transfer.manager.sysparam.SystemParameterManagerImpl;
import ge.ai.domino.console.ui.TCHcomponents.TCHButton;
import ge.ai.domino.console.ui.TCHcomponents.TCHComboBox;
import ge.ai.domino.console.ui.TCHcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.TCHcomponents.TCHFieldLabel;
import ge.ai.domino.console.ui.TCHcomponents.TCHTextField;
import ge.ai.domino.console.ui.util.Messages;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class GamePropertiesPane extends VBox {

    private static final SystemParameterManager systemParameterManage = new SystemParameterManagerImpl();

    private static final SysParamDTO possiblePoints = new SysParamDTO("possiblePoints", "75,155,255");

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
        List<Integer> points = systemParameterManage.getIntegerListParameterValue(possiblePoints);
        List<Object> objPoints = new ArrayList<>();
        for (Integer point : points) {
            objPoints.add(point);
        }
        TCHComboBox pointComboBox = new TCHComboBox(objPoints);
        TCHFieldLabel pointFieldLabel = new TCHFieldLabel(Messages.get("point"), pointComboBox);
        CheckBox startCheckBox = new CheckBox();
        TCHFieldLabel startFieldLabel = new TCHFieldLabel(Messages.get("start"), startCheckBox);
        TCHButton startButton = new TCHButton(Messages.get("start"));
        this.getChildren().addAll(websiteFieldLabel, nameFieldLabel, pointFieldLabel, startFieldLabel, startButton);
    }
}
