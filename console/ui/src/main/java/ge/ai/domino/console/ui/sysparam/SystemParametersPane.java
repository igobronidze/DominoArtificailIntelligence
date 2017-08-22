package ge.ai.domino.console.ui.sysparam;

import ge.ai.domino.console.transfer.dto.exception.DAIConsoleException;
import ge.ai.domino.console.transfer.dto.sysparam.SystemParameterDTO;
import ge.ai.domino.console.transfer.dto.sysparam.SystemParameterTypeDTO;
import ge.ai.domino.console.transfer.manager.sysparam.SystemParameterManager;
import ge.ai.domino.console.transfer.manager.sysparam.SystemParameterManagerImpl;
import ge.ai.domino.console.ui.TCHcomponents.TCHButton;
import ge.ai.domino.console.ui.TCHcomponents.TCHComboBox;
import ge.ai.domino.console.ui.TCHcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.TCHcomponents.TCHFieldLabel;
import ge.ai.domino.console.ui.TCHcomponents.TCHTextField;
import ge.ai.domino.console.ui.main.ControlPanel;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SystemParametersPane extends HBox {

    private SystemParameterManager systemParameterManager = new SystemParameterManagerImpl();

    private TableView<SystemParameterProperty> tableView;

    private TCHTextField keyField;

    private TCHTextField valueField;

    private TCHComboBox typeComboBox;

    public SystemParametersPane() {
        initUI();
    }

    private void initUI() {
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        initTable();
        initParams();
    }

    @SuppressWarnings("unchecked")
    private void initTable() {
        DoubleBinding doubleProperty = ControlPanel.getCenterWidthBinding().subtract(250 + 90 + 60 + 50);
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-family: sylfaen; -fx-text-alignment: center; -fx-font-size: 16px;");
        TableColumn<SystemParameterProperty, Boolean> idColumn = new TableColumn<>(Messages.get("id"));
        idColumn.setPrefWidth(60);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<SystemParameterProperty, Boolean> keyColumn = new TableColumn<>(Messages.get("key"));
        keyColumn.prefWidthProperty().bind(doubleProperty.divide(3));
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        TableColumn<SystemParameterProperty, Boolean> valueColumn = new TableColumn<>(Messages.get("value"));
        valueColumn.prefWidthProperty().bind(doubleProperty.divide(3));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        TableColumn<SystemParameterProperty, Boolean> typeColumn = new TableColumn<>(Messages.get("type"));
        typeColumn.prefWidthProperty().bind(doubleProperty.divide(3));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn deleteColumn = new TableColumn<>("");
        deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SystemParameterProperty, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<SystemParameterProperty, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });
        deleteColumn.setCellFactory(new Callback<TableColumn<SystemParameterProperty, Boolean>, TableCell<SystemParameterProperty, Boolean>>() {
            @Override
            public TableCell<SystemParameterProperty, Boolean> call(TableColumn<SystemParameterProperty, Boolean> p) {
                return new DeleteButtonCell();
            }
        });
        deleteColumn.setPrefWidth(70);
        tableView.getColumns().addAll(idColumn, keyColumn, valueColumn, typeColumn, deleteColumn);
        loadSystemParameters();
        tableView.setRowFactory( tv -> {
            TableRow<SystemParameterProperty> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    SystemParameterProperty systemParameterProperty = row.getItem();
                    keyField.setText(systemParameterProperty.getKey());
                    valueField.setText(systemParameterProperty.getValue());
                    typeComboBox.setValue(systemParameterProperty.getType());
                    keyField.setDisable(true);
                }
            });
            return row ;
        });
        this.getChildren().add(tableView);
    }

    private void initParams() {
        VBox vBox = new VBox();
        vBox.setPrefWidth(250);
        vBox.setSpacing(15);
        vBox.setPadding(new Insets(7));
        vBox.setStyle("-fx-border-color: green; -fx-border-radius: 25px; -fx-border-size: 1px;");
        keyField = new TCHTextField(TCHComponentSize.SMALL);
        valueField = new TCHTextField(TCHComponentSize.SMALL);
        typeComboBox = new TCHComboBox(Arrays.asList(SystemParameterTypeDTO.values()));
        TCHFieldLabel keyFieldLabel = new TCHFieldLabel(Messages.get("key"), keyField);
        TCHFieldLabel valueFieldLabel = new TCHFieldLabel(Messages.get("value"), valueField);
        TCHFieldLabel typeFieldLabel = new TCHFieldLabel(Messages.get("type"), typeComboBox);
        HBox hBox = new HBox(10);
        TCHButton cleanButton = new TCHButton(Messages.get("clean"));
        cleanButton.setOnAction(event -> clearFields());
        TCHButton saveButton = new TCHButton(Messages.get("save"));
        saveButton.setOnAction(event -> {
            String key = keyField.getText();
            String value = valueField.getText();
            String type = typeComboBox.getValue().toString();
            SystemParameterDTO systemParameter = new SystemParameterDTO(0, key, value, SystemParameterTypeDTO.valueOf(type));
            if (!key.isEmpty() && !value.isEmpty()) {
                try {
                    if (keyField.isDisabled()) {
                        systemParameterManager.editSystemParameter(systemParameter);
                    } else {
                        systemParameterManager.addSystemParameter(systemParameter);
                    }
                    clearFields();
                    loadSystemParameters();
                } catch (DAIConsoleException ex) {
                    ex.printStackTrace();
                }
            }
        });
        hBox.getChildren().addAll(saveButton, cleanButton);
        hBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(keyFieldLabel, valueFieldLabel, typeFieldLabel, hBox);
        this.getChildren().add(vBox);
    }

    @SuppressWarnings("unchecked")
    private void clearFields() {
        keyField.setText("");
        valueField.setText("");
        typeComboBox.setValue(typeComboBox.getItems().get(0));
        keyField.setDisable(false);
    }

    private class DeleteButtonCell extends TableCell<SystemParameterProperty, Boolean> {
        final ImageView imageView;
        final Button cellButton;
        DeleteButtonCell(){
            imageView = new ImageView(ImageFactory.getImage("delete_blue.png"));
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            cellButton = new Button("", imageView);
            cellButton.setPrefHeight(25);
            cellButton.setPrefWidth(25);
            cellButton.setOnAction(t -> {
                SystemParameterProperty systemParameterProperty = DeleteButtonCell.this.getTableView().getItems().get(DeleteButtonCell.this.getIndex());
                try {
                    systemParameterManager.deleteSystemParameter(systemParameterProperty.getKey());
                    loadSystemParameters();
                } catch (DAIConsoleException ex) {
                    ex.printStackTrace();
                }
            });
        }
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            }
        }
    }

    private void loadSystemParameters() {
        List<SystemParameterDTO> systemParameters = systemParameterManager.getSystemParameters(null, null);
        List<SystemParameterProperty> systemParameterProperties = new ArrayList<>();
        for (SystemParameterDTO systemParameter : systemParameters) {
            systemParameterProperties.add(new SystemParameterProperty(systemParameter));
        }
        ObservableList<SystemParameterProperty> data = FXCollections.observableArrayList(systemParameterProperties);
        tableView.setItems(data);
    }
}
