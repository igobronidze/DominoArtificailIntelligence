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
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.service.channel.ChannelService;
import ge.ai.domino.service.channel.ChannelServiceImpl;
import ge.ai.domino.service.game.GameService;
import ge.ai.domino.service.game.GameServiceImpl;
import ge.ai.domino.service.sysparam.SystemParameterService;
import ge.ai.domino.service.sysparam.SystemParameterServiceImpl;
import ge.ai.domino.util.string.StringUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GamePropertiesPane extends VBox {

    private static final SystemParameterService systemParameterService = new SystemParameterServiceImpl();

    private static final GameService GAME_SERVICE = new GameServiceImpl();

    private static final ChannelService channelService = new ChannelServiceImpl();

    private static final SysParam possiblePoints = new SysParam("possiblePoints", "75,155,175,255,355");

    private final ControlPanel controlPanel;

    private GamePane gamePane;

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
        List<Channel> channels = channelService.getChannels();
        Map<String, Channel> channelsMap = channels.stream().collect(Collectors.toMap(Channel::getName, channel -> channel));
        TCHComboBox channelsCombo = new TCHComboBox(new ArrayList<>(channelsMap.keySet()));

        TCHFieldLabel channelFieldLabel = new TCHFieldLabel(Messages.get("channel"), channelsCombo);
        TCHTextField nameField = new TCHTextField("tmp", TCHComponentSize.MEDIUM);
        TCHFieldLabel nameFieldLabel = new TCHFieldLabel(Messages.get("name"), nameField);
        List<Integer> points = systemParameterService.getIntegerListParameterValue(possiblePoints);
        List<Object> objPoints = new ArrayList<>(points);
        TCHComboBox pointComboBox = new TCHComboBox(objPoints);
        TCHFieldLabel pointFieldLabel = new TCHFieldLabel(Messages.get("point"), pointComboBox);
        TCHButton startButton = new TCHButton(Messages.get("start"));
        startButton.setOnAction(e -> {
            if (StringUtil.isEmpty((String) channelsCombo.getValue()) || StringUtil.isEmpty(nameField.getText())) {
                WarnDialog.showWarnDialog(Messages.get("pleaseFillAllField"));
            } else {
                GameProperties gameProperties = new GameProperties();
                gameProperties.setPointsForWin(Integer.parseInt(pointComboBox.getValue().toString()));
                gameProperties.setChannel(channelsMap.get(channelsCombo.getValue()));
                gameProperties.setOpponentName(nameField.getText());
                ServiceExecutor.execute(() -> {
                    AppController.round =  GAME_SERVICE.startGame(gameProperties);
                    gamePane = new GamePane(controlPanel, gameProperties) {
                        @Override
                        public void onNewGame() {
                            gamePane = null;
                        }
                    };
                    controlPanel.getRoot().setCenter(gamePane);
                });
            }
        });
        this.getChildren().addAll(nameFieldLabel, channelFieldLabel, pointFieldLabel, startButton);
    }
}
