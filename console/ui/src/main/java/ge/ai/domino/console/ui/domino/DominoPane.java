package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.ui.TCHcomponents.TCHLabel;
import ge.ai.domino.console.ui.control_panel.ControlPanel;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.domino.GameInfo;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.util.domino.DominoService;
import ge.ai.domino.util.domino.DominoServiceImpl;
import ge.ai.domino.util.tile.TileUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class DominoPane extends BorderPane {

    private static final DominoService dominoService = new DominoServiceImpl();

    private PlayType playType;

    private int startedTiles = 0;

    private Hand hand;

    private HimTilesPane himTilesPane;

    private MyTilesPane myTilesPane;

    private boolean pressedOnCtrl;

    private Integer firstPressedNumber;

    public DominoPane(Hand hand) {
        this.hand = hand;
        this.playType = PlayType.GET_STARTED_TILES;
        reload();
    }

    private void reload() {
        initUI();
        initTopPane();
        initCenterPane();
        initBottomPane();
        initKeyboardListener();
    }

    private void initUI() {
        this.setPadding(new Insets(8));
    }

    private void initTopPane() {
        TableInfo tableInfo = hand.getTableInfo();
        GameInfo gameInfo = hand.getGameInfo();
        Label myPointsLabel = new TCHLabel(Messages.get("me") + " - " + gameInfo.getMyPoints() + " (" + tableInfo.getMyTilesCount() + ")");
        Label himPointLabel = new TCHLabel(Messages.get("opponent") + " - " + gameInfo.getHimPoints() + " (" + tableInfo.getHimTilesCount() + ")");
        Label bazaarCountLabel = new TCHLabel(Messages.get("bazaar") + " (" + tableInfo.getBazaarTilesCount() + ")");
        FlowPane flowPane = new FlowPane(30, 10);
        flowPane.setPadding(new Insets(0, 4, 8, 4));
        flowPane.getChildren().addAll(myPointsLabel, himPointLabel, bazaarCountLabel);
        this.setTop(flowPane);
    }

    private void initCenterPane() {
        himTilesPane = new HimTilesPane(DominoPane.this.hand, playType) {
            @Override
            public void onTileEntered(Tile tile, PlayDirection direction) {
                if (direction == PlayDirection.INCORRECT) {
                    showIncorrectTurnMessage();
                    reload();
                } else {
                    if (DominoPane.this.playType == PlayType.GET_STARTED_TILES) {
                        DominoPane.this.hand = dominoService.addTileForMe(DominoPane.this.hand, tile.getX(), tile.getY());
                        startedTiles++;
                        if (startedTiles == 7) {
                            startedTiles = 100;
                            DominoPane.this.playType = hand.getTableInfo().isMyTurn() ? PlayType.ME : PlayType.HIM;
                        }
                        reload();
                    } else if (DominoPane.this.playType == PlayType.ME) {
                        if (DominoPane.this.hand.getTableInfo().getBazaarTilesCount() == 2) {
                            DominoPane.this.playType = PlayType.HIM;
                        }
                        DominoPane.this.hand = dominoService.addTileForMe(DominoPane.this.hand, tile.getX(), tile.getY());
                        reload();
                    } else if (DominoPane.this.playType == PlayType.HIM) {
                        DominoPane.this.hand = dominoService.playForHim(DominoPane.this.hand, tile.getX(), tile.getY(), direction);
                        DominoPane.this.playType = PlayType.ME;
                        reload();
                    }
                }
            }

            @Override
            public void onAddTileEntered() {
                if (DominoPane.this.hand.getTableInfo().getBazaarTilesCount() == 2) {
                    DominoPane.this.playType = PlayType.ME;
                } else {
                    DominoPane.this.hand = dominoService.addTileForHim(DominoPane.this.hand);
                    reload();
                }
            }
        };
        this.setCenter(himTilesPane);
    }

    private void initBottomPane() {
        myTilesPane = new MyTilesPane(DominoPane.this.hand, playType) {
            @Override
            public void onTileEntered(Tile tile, PlayDirection direction) {
                if (direction == PlayDirection.INCORRECT) {
                    showIncorrectTurnMessage();
                    reload();
                } else {
                    DominoPane.this.hand = dominoService.playForMe(DominoPane.this.hand, tile.getX(), tile.getY(), direction);
                    DominoPane.this.playType = PlayType.HIM;
                    reload();
                }
            }
        };
        this.setBottom(myTilesPane);
    }

    private void initKeyboardListener() {
        ControlPanel.getScene().setOnKeyPressed(e -> {
            Integer secondPressedNumber = null;
            if (e.getCode() == KeyCode.CONTROL) {
                pressedOnCtrl = true;
            } else if (pressedOnCtrl && firstPressedNumber == null) {
                try {
                    firstPressedNumber = Integer.parseInt(e.getText());
                } catch (NumberFormatException ignore) {}
            } else if (pressedOnCtrl) {
                try {
                    secondPressedNumber = Integer.parseInt(e.getText());
                } catch (NumberFormatException ignore) {}
            }
            if (pressedOnCtrl && TilesPane.isArrowsVisible()) {
                if (e.getCode() == KeyCode.UP) {
                    TilesPane.onUpArrowPressed();
                }
                if (e.getCode() == KeyCode.LEFT) {
                    TilesPane.onLeftArrowPressed();
                }
                if (e.getCode() == KeyCode.DOWN) {
                    TilesPane.onDownArrowPressed();
                }
                if (e.getCode() == KeyCode.RIGHT) {
                    TilesPane.onRightArrowPressed();
                }
            }
            if (pressedOnCtrl && e.getCode() == KeyCode.ADD) {
                himTilesPane.onAddTileEntered();
            }
            if (secondPressedNumber != null && firstPressedNumber != null) {
                int tmp = secondPressedNumber;
                if (secondPressedNumber > firstPressedNumber) {
                    secondPressedNumber = firstPressedNumber;
                    firstPressedNumber = tmp;
                }
                String uid = TileUtil.getTileUID(firstPressedNumber, secondPressedNumber);
                if (myTilesPane.showTile(uid)) {
                    myTilesPane.onTilePressed(uid);
                } else if (himTilesPane.showTile(uid)) {
                    himTilesPane.onTilePressed(uid);
                }
                firstPressedNumber = null;
                secondPressedNumber = null;
            }
        });
        this.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                pressedOnCtrl = false;
                firstPressedNumber = 0;
            }
        });
    }

    private void showIncorrectTurnMessage() {
        WarnDialog.showWarnDialog(Messages.get("incorrectTurn"));
    }
}
