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
import java.util.Arrays;
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
        tableView.getColumns().addAll(Arrays.asList(keyColumn, valueColumn));
        loadShortcuts();
        this.getChildren().add(tableView);
    }

    private void loadShortcuts() {
        List<ShortcutProperty> shortcuts = new ArrayList<>();
        shortcuts.add(new ShortcutProperty("x_y", Messages.get("shortcut_x_y")));
        shortcuts.add(new ShortcutProperty("backspace", Messages.get("shortcut_backspace")));
        shortcuts.add(new ShortcutProperty("arrow", Messages.get("shortcut_arrow")));
        shortcuts.add(new ShortcutProperty("+", Messages.get("shortcut_+")));
        shortcuts.add(new ShortcutProperty("Z", Messages.get("shortcut_Z")));
        shortcuts.add(new ShortcutProperty("B", Messages.get("shortcut_B")));
        shortcuts.add(new ShortcutProperty("N", Messages.get("shortcut_N")));
        shortcuts.add(new ShortcutProperty("H", Messages.get("shortcut_H")));
        ObservableList<ShortcutProperty> data = FXCollections.observableArrayList(shortcuts);
        tableView.setItems(data);
    }
}
