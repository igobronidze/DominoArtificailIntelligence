package ge.ai.domino.console.ui.shortcut;

import ge.ai.domino.console.ui.util.Messages;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class ShortcutPane extends HBox {

    private TableView<ShortcutProperty> tableView;

    private DoubleBinding doubleBinding;

    public ShortcutPane(DoubleBinding doubleBinding) {
        this.doubleBinding = doubleBinding;
        initUI();
    }

    private void initUI() {
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        initTable();
    }

    @SuppressWarnings("unchecked")
    private void initTable() {
        doubleBinding = doubleBinding.subtract(20);
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-family: sylfaen; -fx-text-alignment: center; -fx-font-size: 16px;");
        TableColumn<ShortcutProperty, Boolean> keyColumn = new TableColumn<>(Messages.get("key"));
        keyColumn.prefWidthProperty().bind(doubleBinding.divide(5));
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        TableColumn<ShortcutProperty, Boolean> valueColumn = new TableColumn<>(Messages.get("value"));
        valueColumn.prefWidthProperty().bind(doubleBinding.divide(5).multiply(4));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        tableView.getColumns().addAll(keyColumn, valueColumn);
        loadShortcuts();
        this.getChildren().add(tableView);
    }

    private void loadShortcuts() {
        List<ShortcutProperty> shortcuts = new ArrayList<>();
        shortcuts.add(new ShortcutProperty("ctrl_x_y", Messages.get("ctrl_x_y")));
        shortcuts.add(new ShortcutProperty("ctrl_backspace", Messages.get("ctrl_backspace")));
        shortcuts.add(new ShortcutProperty("ctrl_arrow", Messages.get("ctrl_arrow")));
        shortcuts.add(new ShortcutProperty("ctrl_+", Messages.get("ctrl_+")));
        shortcuts.add(new ShortcutProperty("ctrl_Z", Messages.get("ctrl_Z")));
        shortcuts.add(new ShortcutProperty("ctrl_B", Messages.get("ctrl_B")));
        shortcuts.add(new ShortcutProperty("ctrl_N", Messages.get("ctrl_N")));
        ObservableList<ShortcutProperty> data = FXCollections.observableArrayList(shortcuts);
        tableView.setItems(data);
    }
}
