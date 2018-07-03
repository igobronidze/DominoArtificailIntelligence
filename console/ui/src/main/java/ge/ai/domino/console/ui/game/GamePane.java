package ge.ai.domino.console.ui.game;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.console.ui.controlpanel.ControlPanel;
import ge.ai.domino.console.ui.gameproperties.GamePropertiesPane;
import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHFieldLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHNumberTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.game.ai.AiPrediction;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.serverutil.TileAndMoveHelper;
import ge.ai.domino.service.game.GameService;
import ge.ai.domino.service.game.GameServiceImpl;
import ge.ai.domino.service.imageprocessing.TilesDetectorService;
import ge.ai.domino.service.imageprocessing.TilesDetectorServiceImpl;
import ge.ai.domino.service.sysparam.SystemParameterService;
import ge.ai.domino.service.sysparam.SystemParameterServiceImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePane extends BorderPane {

    private static final int IMAGE_WIDTH = 65;

    private static final int IMAGE_HEIGHT = 110;

    private final GameService gameService = new GameServiceImpl();

    private final SystemParameterService systemParameterService = new SystemParameterServiceImpl();

    private static final SysParam bestMoveAutoPlay = new SysParam("bestMoveAutoPlay", "true");

    private static final SysParam detectAddedTiles = new SysParam("detectAddedTiles", "true");

    private final TilesDetectorService tilesDetectorService = new TilesDetectorServiceImpl();

    private final ControlPanel controlPanel;

    private ImageView upArrow;

    private ImageView rightArrow;

    private ImageView downArrow;

    private ImageView leftArrow;

    private Map<Tile, ImageView> myTilesImages = new HashMap<>();

    private Map<Tile, ImageView> opponentTilesImages = new HashMap<>();

    private Tile pressedTile;

    private boolean pressedOnMyTile;

    private boolean arrowsVisible;

    private boolean pressedOnCtrl;

    private Integer firstPressedNumber;

    private GameProperties gameProperties;

    private AiPrediction bestAiPrediction;

    private boolean hasPrediction;

    private boolean showingAddLeftTilesWindow;

    public GamePane(ControlPanel controlPanel, GameProperties gameProperties) {
        this.controlPanel = controlPanel;
        this.gameProperties = gameProperties;

        showDetectTilesWindow(true);

        reload(false);

        initKeyboardListener();
        initFocusLoseListener();
    }

    private void showDetectTilesWindow(boolean firstRound) {
        controlPanel.getStage().setIconified(true);

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(Messages.get("detectTiles"));
        TCHLabel label = new TCHLabel(Messages.get("executeTilesDetector"));
        TCHButton yesButton = new TCHButton(Messages.get("yes"));
        yesButton.setOnAction(event -> {
            try {
                List<Tile> tiles = tilesDetectorService.detectTiles(AppController.round.getGameInfo().getGameId());
                for (Tile tile : tiles) {
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                }
                controlPanel.getStage().setIconified(false);
                stage.close();
                reload(false);
            } catch (DAIException ex) {
                WarnDialog.showWarnDialog(ex);
            } catch (Exception ex) {
                ex.printStackTrace();
                WarnDialog.showUnexpectedError();
            }
        });
        TCHButton startMeButton = new TCHButton(Messages.get("yesAndStartMe"));
        startMeButton.setOnAction(event -> {
            try {
                List<Tile> tiles = tilesDetectorService.detectTiles(AppController.round.getGameInfo().getGameId());
                for (int i = 0; i < tiles.size() - 1; i++) {
                    Tile tile = tiles.get(i);
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                }
                gameService.specifyRoundBeginner(AppController.round.getGameInfo().getGameId(), true);
                AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(),
                        tiles.get(tiles.size() - 1).getLeft(), tiles.get(tiles.size() - 1).getRight());
                controlPanel.getStage().setIconified(false);
                reload(false);
                stage.close();
            } catch (DAIException ex) {
                WarnDialog.showWarnDialog(ex);
            } catch (Exception ex) {
                ex.printStackTrace();
                WarnDialog.showUnexpectedError();
            }
        });
        TCHButton startHeButton = new TCHButton(Messages.get("yesAndStartHe"));
        startHeButton.setOnAction(event -> {
            try {
                List<Tile> tiles = tilesDetectorService.detectTiles(AppController.round.getGameInfo().getGameId());
                for (int i = 0; i < tiles.size() - 1; i++) {
                    Tile tile = tiles.get(i);
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                }
                gameService.specifyRoundBeginner(AppController.round.getGameInfo().getGameId(), false);
                AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(),
                        tiles.get(tiles.size() - 1).getLeft(), tiles.get(tiles.size() - 1).getRight());
                controlPanel.getStage().setIconified(false);
                stage.close();
                reload(false);
            } catch (DAIException ex) {
                WarnDialog.showWarnDialog(ex);
            } catch (Exception ex) {
                ex.printStackTrace();
                WarnDialog.showUnexpectedError();
            }
        });
        TCHButton noButton = new TCHButton(Messages.get("no"));
        noButton.setOnAction(event -> {
            controlPanel.getStage().setIconified(false);
            stage.close();
        });
        HBox hBox = new HBox(25);
        hBox.setAlignment(Pos.TOP_CENTER);
        if (firstRound) {
            hBox.getChildren().addAll(startMeButton, startHeButton, noButton);
        } else {
            hBox.getChildren().addAll(yesButton, noButton);
        }
        VBox vBox = new VBox(30);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(label, hBox);
        stage.setScene(new Scene(vBox));
        stage.setWidth(350);
        stage.setHeight(140);
        stage.showAndWait();
    }

    private void showAddedTilesDetectWindow() {
        controlPanel.getStage().setIconified(true);

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(Messages.get("detectAddedTiles"));
        TCHLabel label = new TCHLabel(Messages.get("executeAddedTilesDetector"));
        TCHButton yesButton = new TCHButton(Messages.get("yes"));
        yesButton.setOnAction(event -> {
            try {
                List<Tile> tiles = tilesDetectorService.detectTiles(AppController.round.getGameInfo().getGameId());
                AppController.round = gameService.addTilesForMe(AppController.round.getGameInfo().getGameId(), tiles);
                controlPanel.getStage().setIconified(false);
                stage.close();
                reload(false);
            } catch (DAIException ex) {
                WarnDialog.showWarnDialog(ex);
            } catch (Exception ex) {
                ex.printStackTrace();
                WarnDialog.showUnexpectedError();
            }
        });
        TCHButton noButton = new TCHButton(Messages.get("no"));
        noButton.setOnAction(event -> {
            controlPanel.getStage().setIconified(false);
            stage.close();
        });
        HBox hBox = new HBox(25);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().addAll(yesButton, noButton);
        VBox vBox = new VBox(30);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(label, hBox);
        stage.setScene(new Scene(vBox));
        stage.setWidth(390);
        stage.setHeight(140);
        stage.showAndWait();
    }

    private void reload(boolean showDetectTilesWindow) {
        this.setPadding(new Insets(8));
        initTopPane();
        this.setCenter(getOpponentTilesPane());
        this.setBottom(getMyTilesPane());
        if (AppController.round.getGameInfo().isFinished()) {
            new SaveGameWindow() {
                @Override
                public void onSave() {
                    showNextGameWindow();
                }

                @Override
                public void onCancel() {
                    showNextGameWindow();
                }
            }.showWindow();
        }
        if (showDetectTilesWindow && isFirsMove() && AppController.round.getMyTiles().isEmpty()) {
            showDetectTilesWindow(false);
        }
        if (AppController.round != null && systemParameterService.getBooleanParameterValue(detectAddedTiles) && !hasPrediction && AppController.round.getTableInfo().isMyMove()
                && AppController.round.getTableInfo().getBazaarTilesCount() != 2 && AppController.round.getTableInfo().getLeft() != null) {
            showAddedTilesDetectWindow();
        }
    }

    private void initTopPane() {
        TableInfo tableInfo = AppController.round.getTableInfo();
        GameInfo gameInfo = AppController.round.getGameInfo();
        Label myPointsLabel = new TCHLabel(Messages.get("me") + " - " + gameInfo.getMyPoint() + " (" + AppController.round.getMyTiles().size() + ")");
        Label opponentPointLabel = new TCHLabel(Messages.get("opponent") + " - " + gameInfo.getOpponentPoint() + " (" + (int) tableInfo.getOpponentTilesCount() + ")");
        Label bazaarCountLabel = new TCHLabel(Messages.get("bazaar") + " (" + (int) tableInfo.getBazaarTilesCount() + ")");
        Label opponentLabel = new TCHLabel(Messages.get("opponent") + " - " + gameProperties.getOpponentName() + "(" + gameProperties.getWebsite() + ")");
        Label pointWorWinLabel = new TCHLabel(Messages.get("pointForWin") + " - " + gameProperties.getPointsForWin());

        ImageView undoImage = new ImageView(ImageFactory.getImage("undo.png"));
        undoImage.setCursor(Cursor.HAND);
        undoImage.setOnMouseClicked(event -> onUndo());

        ImageView skipImage = new ImageView(ImageFactory.getImage("skip.png"));
        skipImage.setCursor(Cursor.HAND);
        skipImage.setOnMouseClicked(event -> onSkip());

        FlowPane flowPane = new FlowPane(30, 10);
        flowPane.setPadding(new Insets(0, 4, 8, 4));
        flowPane.getChildren().addAll(myPointsLabel, opponentPointLabel, bazaarCountLabel, opponentLabel, pointWorWinLabel, undoImage, skipImage);
        this.setTop(flowPane);
    }

    private void initFocusLoseListener() {
        controlPanel.getStage().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (systemParameterService.getBooleanParameterValue(bestMoveAutoPlay) && oldValue) {
                if (hasPrediction && bestAiPrediction != null && !showingAddLeftTilesWindow) {
                    try {
                        Move move = bestAiPrediction.getMove();
                        if (AppController.round.getMyTiles().size() == 1) {
                            showAddLeftTilesCount(new Tile(move.getLeft(), move.getRight()), move.getDirection());
                        } else {
                            AppController.round = gameService.playForMe(AppController.round.getGameInfo().getGameId(), new Move(move.getLeft(), move.getRight(), move.getDirection()));
                            reload(true);
                        }
                    } catch (DAIException ex) {
                        WarnDialog.showWarnDialog(ex);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        WarnDialog.showUnexpectedError();
                    }
                } else if (AppController.round != null && !hasPrediction && AppController.round.getTableInfo().isMyMove() && AppController.round.getTableInfo().getBazaarTilesCount() == 2) {
                    try {
                        Tile tile = new ArrayList<>(AppController.round.getOpponentTiles().keySet()).get(0);
                        AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                        reload(true);
                    } catch (DAIException ex) {
                        WarnDialog.showWarnDialog(ex);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        WarnDialog.showUnexpectedError();
                    }
                }
            }
        });
    }

    private void initKeyboardListener() {
        controlPanel.getScene().setOnKeyPressed(e -> {
            Integer secondPressedNumber;
            if (e.getCode() == KeyCode.BACK_SPACE) {
                reload(true);
                return;
            }
            if (e.getCode() == KeyCode.CONTROL) {
                pressedOnCtrl = true;
                return;
            }
            if (pressedOnCtrl && firstPressedNumber == null) {
                try {
                    firstPressedNumber = Integer.parseInt(e.getText());
                    return;
                } catch (NumberFormatException ignore) {
                }
            } else if (pressedOnCtrl) {
                try {
                    secondPressedNumber = Integer.parseInt(e.getText());
                    int tmp = secondPressedNumber;
                    if (secondPressedNumber > firstPressedNumber) {
                        secondPressedNumber = firstPressedNumber;
                        firstPressedNumber = tmp;
                    }
                    Tile tile = new Tile(firstPressedNumber, secondPressedNumber);
                    if (AppController.round.getMyTiles().contains(tile)) {
                        onMyTilePressed(tile);
                    } else if (AppController.round.getOpponentTiles().containsKey(tile)) {
                        onOpponentTilePressed(tile);
                    }
                    firstPressedNumber = null;
                    return;
                } catch (NumberFormatException ignore) {
                }
            } else if (e.getCode() == KeyCode.ALT) {
                fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, true, true, true, true));
            }
            if (pressedOnCtrl && arrowsVisible) {
                if (e.getCode() == KeyCode.UP) {
                    onUpArrowPressed();
                    return;
                }
                if (e.getCode() == KeyCode.LEFT) {
                    onLeftArrowPressed();
                    return;
                }
                if (e.getCode() == KeyCode.DOWN) {
                    onDownArrowPressed();
                    return;
                }
                if (e.getCode() == KeyCode.RIGHT) {
                    onRightArrowPressed();
                    return;
                }
            }
            if (pressedOnCtrl && e.getCode() == KeyCode.ADD) {
                ontAddTileEntered();
                return;
            }
            if (pressedOnCtrl && e.getCode() == KeyCode.Z) {
                onUndo();
            }
            if (pressedOnCtrl && e.getCode() == KeyCode.B) {
                onSkip();
            }
        });
        controlPanel.getScene().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                pressedOnCtrl = false;
                firstPressedNumber = null;
            }
        });
    }

    private void onUndo() {
        try {
            AppController.round = gameService.getLastPlayedRound(AppController.round.getGameInfo().getGameId());
        } catch (DAIException ex) {
            WarnDialog.showWarnDialog(ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            WarnDialog.showUnexpectedError();
        }
        reload(true);
    }

    private void onSkip() {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(Messages.get("skipRound"));

        TCHNumberTextField myPointField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
        TCHFieldLabel myPointFieldLabel = new TCHFieldLabel(Messages.get("myPoint"), myPointField);

        TCHNumberTextField opponentPointField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
        TCHFieldLabel opponentPointFieldLabel = new TCHFieldLabel(Messages.get("opponentPoint"), opponentPointField);

        TCHNumberTextField leftTilesField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
        TCHFieldLabel leftTilesFieldLabel = new TCHFieldLabel(Messages.get("leftTilesCount"), leftTilesField);

        CheckBox startMeCheckBox = new CheckBox();
        TCHFieldLabel startMeFieldLabel = new TCHFieldLabel(Messages.get("startMe"), startMeCheckBox);

        TCHButton skipButton = new TCHButton(Messages.get("skip"));
        skipButton.setOnAction(event -> {
            AppController.round = gameService.skipRound(AppController.round.getGameInfo().getGameId(), myPointField.getNumber().intValue(),
                    opponentPointField.getNumber().intValue(), leftTilesField.getNumber().intValue(), startMeCheckBox.isSelected());
            stage.close();
            reload(true);
        });
        TCHButton cancelButton = new TCHButton(Messages.get("cancel"));
        cancelButton.setOnAction(event -> stage.close());
        HBox hBox = new HBox(25);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().addAll(skipButton, cancelButton);

        VBox vBox = new VBox(15);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(myPointFieldLabel, opponentPointFieldLabel, leftTilesFieldLabel, startMeFieldLabel, hBox);

        stage.setScene(new Scene(vBox));
        stage.setWidth(400);
        stage.setHeight(350);
        stage.showAndWait();
    }

    private void showAddLeftTilesCount(final Tile playedTile, final MoveDirection direction) {
        showingAddLeftTilesWindow = true;
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(Messages.get("addLeftCount"));
        TCHNumberTextField countField = new TCHNumberTextField(TCHComponentSize.MEDIUM);
        TCHButton button = new TCHButton(Messages.get("add"));
        button.setOnAction(event -> {
            try {
                gameService.specifyOpponentLeftTiles(AppController.round.getGameInfo().getGameId(), countField.getNumber().intValue());
                if (playedTile == null && direction == null) {
                    AppController.round = gameService.addTileForOpponent(AppController.round.getGameInfo().getGameId());
                } else if (direction == null) {
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), playedTile.getLeft(), playedTile.getRight());
                } else {
                    AppController.round = gameService.playForMe(AppController.round.getGameInfo().getGameId(), new Move(playedTile.getLeft(), playedTile.getRight(), direction));
                }
            } catch (DAIException ex) {
                WarnDialog.showWarnDialog(ex);
            } catch (Exception ex) {
                ex.printStackTrace();
                WarnDialog.showUnexpectedError();
            }
            showingAddLeftTilesWindow = false;
            stage.close();
            reload(true);
        });
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(countField, button);
        stage.setScene(new Scene(vBox));
        stage.setWidth(300);
        stage.setHeight(100);
        stage.showAndWait();
    }

    private void showNextGameWindow() {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(Messages.get("newGame"));
        TCHLabel label = new TCHLabel(Messages.get("startNewRound"));
        TCHButton yesButton = new TCHButton(Messages.get("yes"));
        yesButton.setOnAction(event -> {
            try {
                AppController.round = gameService.startGame(null, AppController.round.getGameInfo().getGameId());
                reload(true);
            } catch (DAIException ex) {
                WarnDialog.showWarnDialog(ex);
            } catch (Exception ex) {
                ex.printStackTrace();
                WarnDialog.showUnexpectedError();
            }
            stage.close();
        });
        TCHButton noButton = new TCHButton(Messages.get("no"));
        noButton.setOnAction(event -> {
            AppController.round = null;
            controlPanel.getRoot().setCenter(new GamePropertiesPane(controlPanel));
            stage.close();
        });
        HBox hBox = new HBox(25);
        hBox.setAlignment(Pos.TOP_CENTER);
        hBox.getChildren().addAll(yesButton, noButton);
        VBox vBox = new VBox(30);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.getChildren().addAll(label, hBox);
        stage.setScene(new Scene(vBox));
        stage.setWidth(400);
        stage.setHeight(140);
        stage.showAndWait();
    }

    private FlowPane getMyTilesPane() {
        FlowPane myTilesPane = new FlowPane();
        myTilesPane.setHgap(25);
        myTilesPane.setVgap(10);
        myTilesPane.setStyle("-fx-border-color: green; -fx-border-radius: 25px; -fx-border-size: 2px;");
        myTilesPane.setPadding(new Insets(8));
        initArrows();
        initMyTilesComponents(myTilesPane);
        return myTilesPane;
    }

    private FlowPane getOpponentTilesPane() {
        FlowPane opponentTilesPane = new FlowPane();
        opponentTilesPane.setHgap(25);
        opponentTilesPane.setVgap(10);
        initOpponentTilesComponents(opponentTilesPane);
        return opponentTilesPane;
    }

    private void initArrows() {
        upArrow = new ImageView(ImageFactory.getImage("arrows/up.png"));
        setImageStyle(upArrow);
        upArrow.setOnMouseClicked(e -> onUpArrowPressed());
        rightArrow = new ImageView(ImageFactory.getImage("arrows/right.png"));
        setImageStyle(rightArrow);
        rightArrow.setOnMouseClicked(e -> onRightArrowPressed());
        downArrow = new ImageView(ImageFactory.getImage("arrows/down.png"));
        setImageStyle(downArrow);
        downArrow.setOnMouseClicked(e -> onDownArrowPressed());
        leftArrow = new ImageView(ImageFactory.getImage("arrows/left.png"));
        leftArrow.setOnMouseClicked(e -> onLeftArrowPressed());
        setImageStyle(leftArrow);
        setImageVisibility(false);
    }

    private void setImageStyle(ImageView imageView) {
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        imageView.setOnMouseEntered(event -> this.setCursor(Cursor.HAND));
        imageView.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));
    }

    private void setImageVisibility(boolean visible) {
        upArrow.setVisible(visible);
        rightArrow.setVisible(visible);
        downArrow.setVisible(visible);
        leftArrow.setVisible(visible);
        arrowsVisible = visible;
    }

    private void initMyTilesComponents(FlowPane flowPane) {
        if (AppController.round.getAiPredictions() != null) {
            String warnMsgKey = AppController.round.getAiPredictions().getWarnMsgKey();
            if (warnMsgKey != null) {
                WarnDialog.showWarnDialog(Messages.get(warnMsgKey));
            }
        }
        hasPrediction = false;
        AppController.round.getMyTiles().stream().filter(tile -> AppController.round.getMyTiles().contains(tile)).forEach(tile -> {
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.TOP_CENTER);
            ImageView imageView = getImageView(tile, AppController.round.getTableInfo().isMyMove());
            myTilesImages.put(tile, imageView);
            List<AiPrediction> tilePredictions = getAiPredictionByTile(AppController.round.getAiPredictions() == null ? null : AppController.round.getAiPredictions().getAiPredictions(), tile);
            if (!tilePredictions.isEmpty()) {
                NumberFormat formatter = new DecimalFormat("#0.0000");
                for (AiPrediction aiPrediction : tilePredictions) {
                    String heuristic = aiPrediction.getHeuristicValue() == Integer.MIN_VALUE ? "NAN" : formatter.format(aiPrediction.getHeuristicValue());
                    Label label = new Label(aiPrediction.getMove().getDirection().name() + "(" + heuristic + ")");
                    if (aiPrediction.isBestMove()) {
                        bestAiPrediction = aiPrediction;
                        hasPrediction = true;
                        label.setStyle("-fx-font-size: 14px; -fx-text-fill: green; -fx-font-weight: bold");
                    }
                    vBox.getChildren().add(label);
                }
            }
            imageView.setOnMouseClicked(e -> onMyTilePressed(tile));
            vBox.getChildren().add(imageView);
            flowPane.getChildren().add(vBox);
        });

        flowPane.getChildren().addAll(leftArrow, upArrow, downArrow, rightArrow);
    }

    private List<AiPrediction> getAiPredictionByTile(List<AiPrediction> aiPredictions, Tile tile) {
        List<AiPrediction> result = new ArrayList<>();
        if (aiPredictions == null) {
            return result;
        }
        for (AiPrediction aiPrediction : aiPredictions) {
            if (aiPrediction.getMove().getLeft() == tile.getLeft() && aiPrediction.getMove().getRight() == tile.getRight()) {
                result.add(aiPrediction);
            }
        }
        return result;
    }

    private void initOpponentTilesComponents(FlowPane flowPane) {
        NumberFormat formatter = new DecimalFormat("#0.0000");
        for (int i = 6; i >= 0; i--) {
            HBox hBox = new HBox();
            hBox.setStyle("-fx-border-color: green; -fx-border-radius: 25px; -fx-border-size: 1px;");
            hBox.setSpacing(10);
            hBox.setPadding(new Insets(8));
            for (int j = i; j >= 0; j--) {
                Tile tile = new Tile(i, j);
                Double prob = AppController.round.getOpponentTiles().get(tile);
                if (AppController.round.getOpponentTiles().containsKey(tile)) {
                    ImageView imageView = getImageView(tile, true);
                    Label opponentLabel = new Label("" + (prob == null ? "N" : formatter.format(prob)));
                    opponentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold");
                    VBox vBox = new VBox(5);
                    vBox.setAlignment(Pos.TOP_CENTER);
                    vBox.getChildren().addAll(opponentLabel, imageView);
                    hBox.getChildren().add(vBox);
                    opponentTilesImages.put(tile, imageView);
                    imageView.setOnMouseClicked(e -> onOpponentTilePressed(tile));
                } else {
                    VBox vBox = new VBox();
                    vBox.setPrefHeight(IMAGE_HEIGHT);
                    vBox.setPrefWidth(IMAGE_WIDTH);
                    hBox.getChildren().addAll(vBox);
                }
            }
            flowPane.getChildren().add(hBox);
        }
        if (!AppController.round.getTableInfo().isMyMove()) {
            ImageView addImageView = new ImageView(ImageFactory.getImage("add_black.png"));
            addImageView.setOnMouseEntered(event -> this.setCursor(Cursor.HAND));
            addImageView.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));
            setImageStyle(addImageView);
            flowPane.getChildren().add(addImageView);
            addImageView.setOnMouseClicked(e -> ontAddTileEntered());
        }
    }

    private void onMyTilePressed(Tile tile) {
        if (AppController.round.getTableInfo().isMyMove()) {
            if (isFirsMove()) {
                onMyTileEntered(tile, MoveDirection.LEFT);
            } else {
                myTilesImages.get(tile).setFitHeight(IMAGE_HEIGHT + 10);
                myTilesImages.get(tile).setFitWidth(IMAGE_WIDTH + 10);
                pressedTile = tile;
                pressedOnMyTile = true;
                setImageVisibility(true);
            }
        }
    }

    private void onOpponentTilePressed(Tile tile) {
        if (!AppController.round.getTableInfo().isMyMove()) {
            if (isFirsMove()) {
                ontOpponentTileEntered(tile, MoveDirection.LEFT);
            } else {
                opponentTilesImages.get(tile).setFitHeight(IMAGE_HEIGHT + 10);
                opponentTilesImages.get(tile).setFitWidth(IMAGE_WIDTH + 10);
                pressedTile = tile;
                pressedOnMyTile = false;
                setImageVisibility(true);
            }
        } else {
            ontOpponentTileEntered(tile, MoveDirection.LEFT);
        }
    }

    private ImageView getImageView(Tile tile, boolean clickable) {
        ImageView imageView = new ImageView(ImageFactory.getImage("tiles/" + tile.getLeft() + "-" + tile.getRight() + ".png"));
        imageView.setFitHeight(IMAGE_HEIGHT);
        imageView.setFitWidth(IMAGE_WIDTH);
        if (clickable) {
            imageView.setOnMouseEntered(event -> this.setCursor(Cursor.HAND));
            imageView.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));
        }
        return imageView;
    }

    private boolean isFirsMove() {
        if (AppController.round == null) {
            return false;
        }
        TableInfo tableInfo = AppController.round.getTableInfo();
        return tableInfo.getTop() == null && tableInfo.getRight() == null && tableInfo.getBottom() == null && tableInfo.getLeft() == null;
    }

    private void onMyTileEntered(Tile tile, MoveDirection direction) {
        try {
            if (AppController.round.getMyTiles().size() == 1) {
                showAddLeftTilesCount(tile, direction);
            } else {
                AppController.round = gameService.playForMe(AppController.round.getGameInfo().getGameId(), TileAndMoveHelper.getMove(tile, direction));
                reload(true);
            }
        } catch (DAIException ex) {
            WarnDialog.showWarnDialog(ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            WarnDialog.showUnexpectedError();
        }
    }

    private void ontOpponentTileEntered(Tile tile, MoveDirection direction) {
        if (AppController.round.getTableInfo().isMyMove()) {
            TableInfo tableInfo = AppController.round.getTableInfo();
            if (tableInfo.getRoundBlockingInfo().isOmitOpponent() && tableInfo.getBazaarTilesCount() == 2) {
                showAddLeftTilesCount(tile, null);
            } else if (tableInfo.getLeft() == null && tableInfo.isFirstRound() && AppController.round.getMyTiles().size() == 6) {
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setTitle(Messages.get("gameStarter"));
                TCHLabel label = new TCHLabel(Messages.get("whoStartGame"));
                TCHButton meButton = new TCHButton(Messages.get("me"));
                meButton.setOnAction(event -> {
                    try {
                        gameService.specifyRoundBeginner(AppController.round.getGameInfo().getGameId(), true);
                        AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                        reload(true);
                        stage.close();
                    } catch (DAIException ex) {
                        WarnDialog.showWarnDialog(ex);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        WarnDialog.showUnexpectedError();
                    }
                });
                TCHButton opponentButton = new TCHButton(Messages.get("he"));
                opponentButton.setOnAction(event -> {
                    try {
                        gameService.specifyRoundBeginner(AppController.round.getGameInfo().getGameId(), false);
                        AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                        reload(true);
                        stage.close();
                    } catch (DAIException ex) {
                        WarnDialog.showWarnDialog(ex);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        WarnDialog.showUnexpectedError();
                    }
                });
                HBox hBox = new HBox(25);
                hBox.setAlignment(Pos.TOP_CENTER);
                hBox.getChildren().addAll(meButton, opponentButton);
                VBox vBox = new VBox(30);
                vBox.setPadding(new Insets(20));
                vBox.setAlignment(Pos.TOP_CENTER);
                vBox.getChildren().addAll(label, hBox);
                stage.setScene(new Scene(vBox));
                stage.setWidth(350);
                stage.setHeight(140);
                stage.showAndWait();
            } else {
                try {
                    AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
                    reload(true);
                } catch (DAIException ex) {
                    WarnDialog.showWarnDialog(ex);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    WarnDialog.showUnexpectedError();
                }
            }
        } else {
            try {
                AppController.round = gameService.playForOpponent(AppController.round.getGameInfo().getGameId(), TileAndMoveHelper.getMove(tile, direction));
            } catch (DAIException ex) {
                WarnDialog.showWarnDialog(ex);
            } catch (Exception ex) {
                ex.printStackTrace();
                WarnDialog.showUnexpectedError();
            }
            reload(true);
        }

    }

    private void onUpArrowPressed() {
        if (pressedOnMyTile) {
            onMyTileEntered(pressedTile, MoveDirection.TOP);
        } else {
            ontOpponentTileEntered(pressedTile, MoveDirection.TOP);
        }
    }

    private void onRightArrowPressed() {
        if (pressedOnMyTile) {
            onMyTileEntered(pressedTile, MoveDirection.RIGHT);
        } else {
            ontOpponentTileEntered(pressedTile, MoveDirection.RIGHT);
        }
    }

    private void onDownArrowPressed() {
        if (pressedOnMyTile) {
            onMyTileEntered(pressedTile, MoveDirection.BOTTOM);
        } else {
            ontOpponentTileEntered(pressedTile, MoveDirection.BOTTOM);
        }
    }

    private void onLeftArrowPressed() {
        if (pressedOnMyTile) {
            onMyTileEntered(pressedTile, MoveDirection.LEFT);
        } else {
            ontOpponentTileEntered(pressedTile, MoveDirection.LEFT);
        }
    }

    private void ontAddTileEntered() {
        try {
            if (AppController.round.getTableInfo().getRoundBlockingInfo().isOmitMe() && AppController.round.getTableInfo().getBazaarTilesCount() == 2) {
                showAddLeftTilesCount(null, null);
            } else {
                AppController.round = gameService.addTileForOpponent(AppController.round.getGameInfo().getGameId());
            }
        } catch (DAIException ex) {
            WarnDialog.showWarnDialog(ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            WarnDialog.showUnexpectedError();
        }
        reload(true);
    }
}
