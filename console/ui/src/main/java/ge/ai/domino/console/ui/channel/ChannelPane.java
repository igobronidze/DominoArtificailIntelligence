package ge.ai.domino.console.ui.channel;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComboBox;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.service.channel.ChannelService;
import ge.ai.domino.service.channel.ChannelServiceImpl;
import ge.ai.domino.util.string.StringUtil;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ChannelPane extends BorderPane {

    private final ChannelService channelService = new ChannelServiceImpl();

    private TCHComboBox channelsCombo;

    private Map<String, Channel> channels;

    private Channel selectedChannel;

    private Map<String, TCHTextField> existedFields = new LinkedHashMap<>();

    private Map<TCHTextField, TCHTextField> newFields = new LinkedHashMap<>();

    public ChannelPane() {
        initChannels();
        initUI();
    }

    private void initChannels() {
        channels = new HashMap<>();
        for (Channel channel : channelService.getChannels()) {
            channels.put(channel.getName(), channel);
        }
    }

    private void initUI() {
        this.setPadding(new Insets(5));
        initTopPane();
        initChannelPane();
        initBottomPane();
    }

    private void initBottomPane() {
        TCHButton saveButton = new TCHButton(Messages.get("save"));
        saveButton.setOnAction(e -> {
            Map<String, String> params = new HashMap<>();
            boolean valid = true;
            Set<String> keys = new HashSet<>();

            for (Map.Entry<String, TCHTextField> entry : existedFields.entrySet()) {
                keys.add(entry.getKey());
                if (!StringUtil.isEmpty(entry.getValue().getText())) {
                    params.put(entry.getKey(), entry.getValue().getText());
                } else {
                    WarnDialog.showWarnDialog(Messages.get("pleaseFillAllField"));
                    valid = false;
                    break;
                }
            }
            if (valid) {
                for (Map.Entry<TCHTextField, TCHTextField> entry : newFields.entrySet()) {
                    if (StringUtil.isEmpty(entry.getKey().getText()) || StringUtil.isEmpty(entry.getValue().getText())) {
                        WarnDialog.showWarnDialog(Messages.get("pleaseFillAllField"));
                        valid = false;
                        break;
                    }
                    if (keys.contains(entry.getKey().getText())) {
                        WarnDialog.showWarnDialog(Messages.get("duplicateKey"));
                        valid = false;
                        break;
                    }
                    keys.add(entry.getKey().getText());
                    params.put(entry.getKey().getText(), entry.getValue().getText());
                }
            }
            if (valid) {
                selectedChannel.setParams(params);
                channelService.editChannel(selectedChannel);
                initChannelPane();
            }
        });

        HBox hBox = new HBox(20);
        hBox.setPadding(new Insets(15));
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().add(saveButton);
        this.setBottom(hBox);
    }

    private void initTopPane() {
        channelsCombo = new TCHComboBox(new ArrayList<>(channels.keySet()));
        channelsCombo.setOnAction(e -> initChannelPane());

        TCHTextField channelNameField = new TCHTextField(TCHComponentSize.SMALL);
        channelNameField.setPromptText(Messages.get("name"));

        TCHButton addChannelButton = new TCHButton(Messages.get("add"));
        addChannelButton.setOnAction(e -> {
            if (!StringUtil.isEmpty(channelNameField.getText())) {
                Channel channel = new Channel();
                channel.setName(channelNameField.getText());
                channelService.addChannel(channel);

                channelNameField.setText("");
                initChannels();
                initUI();
            } else {
                WarnDialog.showWarnDialog(Messages.get("pleaseFillAllField"));
            }
        });

        HBox hBox = new HBox(15);
        hBox.getChildren().addAll(channelNameField, addChannelButton);

        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        flowPane.setVgap(10);
        flowPane.setHgap(140);
        flowPane.setPadding(new Insets(15));
        flowPane.getChildren().addAll(channelsCombo, hBox);
        this.setTop(flowPane);
    }

    private void initChannelPane() {
        VBox paramsBox = new VBox(10);
        paramsBox.setPadding(new Insets(15));

        if (channelsCombo.getValue() != null) {
            selectedChannel = channels.get(channelsCombo.getValue());
            if (selectedChannel != null ) {
                existedFields = new LinkedHashMap<>();
                newFields = new LinkedHashMap<>();

                TCHButton addParamButton = new TCHButton();
                addParamButton.setGraphic(new ImageView(ImageFactory.getImage("add_green.png")));
                paramsBox.getChildren().add(addParamButton);

                addParamButton.setOnAction(e -> {
                    TCHTextField keyField = new TCHTextField(TCHComponentSize.MEDIUM);
                    TCHTextField valueField = new TCHTextField(TCHComponentSize.MEDIUM);
                    newFields.put(keyField, valueField);

                    HBox hBox = new HBox(30);
                    hBox.getChildren().addAll(keyField, valueField);
                    paramsBox.getChildren().add(hBox);
                });

                for (Map.Entry<String, String> entry : selectedChannel.getParams().entrySet()) {
                    TCHLabel label = new TCHLabel(entry.getKey());
                    label.setPrefWidth(300);
                    TCHTextField textField = new TCHTextField(entry.getValue(), TCHComponentSize.MEDIUM);
                    existedFields.put(entry.getKey(), textField);

                    HBox hBox = new HBox(30);
                    hBox.getChildren().addAll(label, textField);
                    paramsBox.getChildren().add(hBox);
                }
            }
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(paramsBox);
        scrollPane.setPrefHeight(500);
        scrollPane.setMaxHeight(500);
        this.setCenter(scrollPane);
    }
}
