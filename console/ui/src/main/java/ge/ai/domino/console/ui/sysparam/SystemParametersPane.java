package ge.ai.domino.console.ui.sysparam;

import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComboBox;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHFieldLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.domain.sysparam.SystemParameterType;
import ge.ai.domino.service.sysparam.SystemParameterService;
import ge.ai.domino.service.sysparam.SystemParameterServiceImpl;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SystemParametersPane extends HBox {

    private final SystemParameterService systemParameterService = new SystemParameterServiceImpl();

    private TableView<SystemParameterProperty> tableView;

    private TCHTextField keyField;

    private TCHTextField valueField;

    private TCHComboBox typeComboBox;

    private DoubleBinding doubleBinding;

    public SystemParametersPane(DoubleBinding doubleBinding) {
        this.doubleBinding = doubleBinding;
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
        doubleBinding = doubleBinding.subtract(250 + 90 + 40 + 50);
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-family: sylfaen; -fx-text-alignment: center; -fx-font-size: 16px;");
        TableColumn<SystemParameterProperty, Boolean> idColumn = new TableColumn<>(Messages.get("id"));
        idColumn.setPrefWidth(40);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<SystemParameterProperty, Boolean> keyColumn = new TableColumn<>(Messages.get("key"));
        keyColumn.prefWidthProperty().bind(doubleBinding.divide(8).multiply(3));
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        TableColumn<SystemParameterProperty, Boolean> valueColumn = new TableColumn<>(Messages.get("value"));
        valueColumn.prefWidthProperty().bind(doubleBinding.divide(8).multiply(3));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        TableColumn<SystemParameterProperty, Boolean> typeColumn = new TableColumn<>(Messages.get("type"));
        typeColumn.prefWidthProperty().bind(doubleBinding.divide(8).multiply(2));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn deleteColumn = new TableColumn<>("");
        deleteColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<SystemParameterProperty, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));
        deleteColumn.setCellFactory((Callback<TableColumn<SystemParameterProperty, Boolean>, TableCell<SystemParameterProperty, Boolean>>) p -> new DeleteButtonCell());
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
        typeComboBox = new TCHComboBox(Arrays.asList(SystemParameterType.values()));
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
            SystemParameter systemParameter = new SystemParameter(0, key, value, SystemParameterType.valueOf(type));
            if (!key.isEmpty() && !value.isEmpty()) {
                ServiceExecutor.execute(() -> {
                    if (keyField.isDisabled()) {
                        systemParameterService.editSystemParameter(systemParameter);
                    } else {
                        systemParameterService.addSystemParameter(systemParameter);
                    }
                    clearFields();
                    loadSystemParameters();
                });
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
                ServiceExecutor.execute(() -> {
                    systemParameterService.deleteSystemParameter(systemParameterProperty.getKey());
                    loadSystemParameters();
                });
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
        List<SystemParameter> systemParameters = systemParameterService.getSystemParameters(null, null);
        List<SystemParameterProperty> systemParameterProperties = systemParameters.stream().map(SystemParameterProperty::new).collect(Collectors.toList());
        ObservableList<SystemParameterProperty> data = FXCollections.observableArrayList(systemParameterProperties);
        tableView.setItems(data);
    }
}
