package ge.ai.domino.console.ui.client;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComboBox;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.client.Client;
import ge.ai.domino.service.client.ClientService;
import ge.ai.domino.service.client.ClientServiceImpl;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientPane extends BorderPane {

    private final ClientService clientService = new ClientServiceImpl();

    private TCHComboBox<String> clientsCombo;

    private Map<String, Client> clientsMap;

    private Client selectedClient;

    private Map<String, TCHTextField> existedFields = new LinkedHashMap<>();

    private Map<TCHTextField, TCHTextField> newFields = new LinkedHashMap<>();

    public ClientPane() {
        initClients();
        initUI();
    }

    private void initClients() {
        clientsMap = new HashMap<>();

        List<Client> clients = new ArrayList<>();

        new ServiceExecutor() {}.execute(() -> clients.addAll(clientService.getClients()));

        for (Client client : clients) {
            clientsMap.put(client.getName(), client);
        }
    }

    private void initUI() {
        this.setPadding(new Insets(5));
        initTopPane();
        initClientsPane();
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
                selectedClient.setParams(params);

                new ServiceExecutor() {}.execute(() -> clientService.editClient(selectedClient));
                initClientsPane();
            }
        });

        HBox hBox = new HBox(20);
        hBox.setPadding(new Insets(15));
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().add(saveButton);
        this.setBottom(hBox);
    }

    private void initTopPane() {
        clientsCombo = new TCHComboBox<>(new ArrayList<>(clientsMap.keySet()));
        clientsCombo.setOnAction(e -> initClientsPane());

        TCHTextField clientNameField = new TCHTextField(TCHComponentSize.SMALL);
        clientNameField.setPromptText(Messages.get("name"));

        TCHButton addClientButton = new TCHButton(Messages.get("add"));
        addClientButton.setOnAction(e -> {
            if (!StringUtil.isEmpty(clientNameField.getText())) {
                Client client = new Client();
                client.setName(clientNameField.getText());

                new ServiceExecutor() {}.execute(() -> clientService.addClient(client));
                clientNameField.setText("");
                initClients();
                initUI();
            } else {
                WarnDialog.showWarnDialog(Messages.get("pleaseFillAllField"));
            }
        });

        HBox hBox = new HBox(15);
        hBox.getChildren().addAll(clientNameField, addClientButton);

        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL);
        flowPane.setVgap(10);
        flowPane.setHgap(140);
        flowPane.setPadding(new Insets(15));
        flowPane.getChildren().addAll(clientsCombo, hBox);
        this.setTop(flowPane);
    }

    private void initClientsPane() {
        VBox paramsBox = new VBox(10);
        paramsBox.setPadding(new Insets(15));

        if (clientsCombo.getValue() != null) {
            selectedClient = clientsMap.get(clientsCombo.getValue());
            if (selectedClient != null ) {
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

                for (Map.Entry<String, String> entry : selectedClient.getParams().entrySet()) {
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
