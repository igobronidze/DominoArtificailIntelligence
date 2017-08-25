package ge.ai.domino.console.ui.domino;

import ge.ai.domino.console.transfer.dto.domino.GameDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayDirectionDTO;
import ge.ai.domino.console.transfer.dto.domino.TileDTO;
import ge.ai.domino.console.transfer.manager.domino.DominoManager;
import ge.ai.domino.console.transfer.manager.domino.DominoMangerImpl;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.console.transfer.dto.domino.PlayTypeDTO;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

public class DominoPane extends BorderPane {

    private static final DominoManager dominoManager = new DominoMangerImpl();

    private PlayTypeDTO playType;

    private int startedTiles = 0;

    private GameDTO game;

    private HimTilesPane himTilesPane;

    private MyTilesPane myTilesPane;

    public DominoPane(GameDTO game) {
        this.game = game;
        this.playType = PlayTypeDTO.GET_STARTED_TILES;
        reload();
    }

    private void reload() {
        initUI();
        initTopPane();
        initBottomPane();
    }

    private void initUI() {
        this.setPadding(new Insets(10));
    }

    private void initTopPane() {
        himTilesPane = new HimTilesPane(DominoPane.this.game, playType) {
            @Override
            public void onTileClick(TileDTO tile, PlayDirectionDTO direction) {
                if (direction == PlayDirectionDTO.INCORRECT) {
                    showIncorrectTurnMessage();
                    reload();
                } else {
                    if (DominoPane.this.playType == PlayTypeDTO.GET_STARTED_TILES) {
                        DominoPane.this.game = dominoManager.addTileForMe(DominoPane.this.game, tile.getX(), tile.getY());
                        startedTiles++;
                        if (startedTiles == 7) {
                            startedTiles = 100;
                            DominoPane.this.playType = game.getCurrHand().isMyTurn() ? PlayTypeDTO.ME : PlayTypeDTO.HIM;
                        }
                        reload();
                    } else if (DominoPane.this.playType == PlayTypeDTO.ME) {
                        if (DominoPane.this.game.getCurrHand().getTilesInBazaar() == 2) {
                            DominoPane.this.playType = PlayTypeDTO.HIM;
                        }
                        DominoPane.this.game = dominoManager.addTileForMe(DominoPane.this.game, tile.getX(), tile.getY());
                        reload();
                    } else if (DominoPane.this.playType == PlayTypeDTO.HIM) {
                        DominoPane.this.game = dominoManager.playForHim(DominoPane.this.game, tile.getX(), tile.getY(), direction);
                        DominoPane.this.playType = PlayTypeDTO.ME;
                        reload();
                    }
                }
            }

            @Override
            public void onAddTileClick() {
                if (DominoPane.this.game.getCurrHand().getTilesInBazaar() == 2) {
                    DominoPane.this.playType = PlayTypeDTO.ME;
                } else {
                    DominoPane.this.game = dominoManager.addTileForHim(DominoPane.this.game);
                    reload();
                }
            }
        };
        this.setCenter(himTilesPane);
    }

    private void initBottomPane() {
        myTilesPane = new MyTilesPane(DominoPane.this.game, playType) {
            @Override
            public void onTileClick(TileDTO tile, PlayDirectionDTO direction) {
                if (direction == PlayDirectionDTO.INCORRECT) {
                    showIncorrectTurnMessage();
                    reload();
                } else {
                    DominoPane.this.game = dominoManager.playForMe(DominoPane.this.game, tile.getX(), tile.getY(), direction);
                    DominoPane.this.playType = PlayTypeDTO.HIM;
                    reload();
                }
            }
        };
        this.setBottom(myTilesPane);
    }

    private void showIncorrectTurnMessage() {
        WarnDialog.showWarnDialog(Messages.get("incorrectTurn"));
    }
}
