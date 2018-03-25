package ge.ai.domino.console.ui.game;

import ge.ai.domino.console.ui.controlpanel.AppController;
import ge.ai.domino.console.ui.controlpanel.ControlPanel;
import ge.ai.domino.console.ui.gameproperties.GamePropertiesPane;
import ge.ai.domino.console.ui.tchcomponents.TCHButton;
import ge.ai.domino.console.ui.tchcomponents.TCHComponentSize;
import ge.ai.domino.console.ui.tchcomponents.TCHLabel;
import ge.ai.domino.console.ui.tchcomponents.TCHNumberTextField;
import ge.ai.domino.console.ui.util.ImageFactory;
import ge.ai.domino.console.ui.util.Messages;
import ge.ai.domino.console.ui.util.dialog.WarnDialog;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.service.game.GameService;
import ge.ai.domino.service.game.GameServiceImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class GamePane extends BorderPane {

	private static final int IMAGE_WIDTH = 65;

	private static final int IMAGE_HEIGHT = 110;

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

	private final GameService gameService = new GameServiceImpl();

	public GamePane(ControlPanel controlPanel) {
		this.controlPanel = controlPanel;
		reload();
	}

	private void reload() {
		this.setPadding(new Insets(8));
		initTopPane();
		this.setCenter(getOpponentTilesPane());
		this.setBottom(getMyTilesPane());
		initKeyboardListener();
		if (AppController.round != null && (AppController.round.getTableInfo().getOpponentTilesCount() == 0.0F || AppController.round.getMyTiles().isEmpty())
				&& AppController.round.getTableInfo().getLeft() != null) {
			new SaveGameWindow() {
				@Override
				public void onYes() {
					showNextGameWindow();
				}

				@Override
				public void onNo() {
					showNextGameWindow();
				}

				@Override
				public void onCancel() {
					showNextGameWindow();
				}
			}.showWindow();
		}
	}

	private void initTopPane() {
		TableInfo tableInfo = AppController.round.getTableInfo();
		GameInfo gameInfo = AppController.round.getGameInfo();
		Label myPointsLabel = new TCHLabel(Messages.get("me") + " - " + gameInfo.getMyPoint() + " (" + AppController.round.getMyTiles().size() + ")");
		Label opponentPointLabel = new TCHLabel(Messages.get("opponent") + " - " + gameInfo.getOpponentPoint() + " (" + (int) tableInfo.getOpponentTilesCount() + ")");
		Label bazaarCountLabel = new TCHLabel(Messages.get("bazaar") + " (" + (int) tableInfo.getBazaarTilesCount() + ")");
		ImageView undoImage = new ImageView(ImageFactory.getImage("undo.png"));
		undoImage.setOnMouseClicked(event -> onUndo());
		FlowPane flowPane = new FlowPane(30, 10);
		flowPane.setPadding(new Insets(0, 4, 8, 4));
		flowPane.getChildren().addAll(myPointsLabel, opponentPointLabel, bazaarCountLabel, undoImage);
		this.setTop(flowPane);
	}

	private void initKeyboardListener() {
		controlPanel.getScene().setOnKeyPressed(e -> {
			Integer secondPressedNumber;
			if (e.getCode() == KeyCode.BACK_SPACE) {
				reload();
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
		}
		reload();
	}

	private void showAddLeftTilesCount(final Tile playedTile, final MoveDirection direction) {
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
			}
			stage.close();
			reload();
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
				reload();
			} catch (DAIException ex) {
				WarnDialog.showWarnDialog(ex);
			}
			stage.close();
		});
		TCHButton noButton = new TCHButton(Messages.get("no"));
		noButton.setOnAction(event -> {
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
		AppController.round.getMyTiles().stream().filter(tile -> AppController.round.getMyTiles().contains(tile)).forEach(tile -> {
			VBox vBox = new VBox();
			vBox.setAlignment(Pos.TOP_CENTER);
			ImageView imageView = getImageView(tile, AppController.round.getTableInfo().isMyMove());
			myTilesImages.put(tile, imageView);
			Move aiPrediction = AppController.round.getAiPrediction();
			if (aiPrediction != null) {
				int bestX = aiPrediction.getLeft();
				int bestY = aiPrediction.getRight();
				if (tile.getLeft() == bestX && tile.getRight() == bestY) {
					NumberFormat formatter = new DecimalFormat("#0.0000");
					Label label = new Label(aiPrediction.getDirection().name() + "(" + formatter.format(AppController.round.getHeuristicValue()) + ")");
					vBox.getChildren().add(label);
				}
			}
			imageView.setOnMouseClicked(e -> onMyTilePressed(tile));
			vBox.getChildren().add(imageView);
			flowPane.getChildren().add(vBox);
		});
		flowPane.getChildren().addAll(leftArrow, upArrow, downArrow, rightArrow);
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
				Float prob = AppController.round.getOpponentTiles().get(tile);
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
		TableInfo tableInfo = AppController.round.getTableInfo();
		return tableInfo.getTop() == null && tableInfo.getRight() == null && tableInfo.getBottom() == null && tableInfo.getLeft() == null;
	}

	private void onMyTileEntered(Tile tile, MoveDirection direction) {
		try {
			if (AppController.round.getMyTiles().size() == 1) {
				showAddLeftTilesCount(tile, direction);
			} else {
				AppController.round = gameService.playForMe(AppController.round.getGameInfo().getGameId(), new Move(tile.getLeft(), tile.getRight(), direction));
				AppController.round.setAiPrediction(null);  // TODO will not make any change
			}
		} catch (DAIException ex) {
			WarnDialog.showWarnDialog(ex);
		}
		reload();
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
						reload();
						stage.close();
					} catch (DAIException ex) {
						WarnDialog.showWarnDialog(ex);
					}
				});
				TCHButton opponentButton = new TCHButton(Messages.get("he"));
				opponentButton.setOnAction(event -> {
					try {
						gameService.specifyRoundBeginner(AppController.round.getGameInfo().getGameId(), false);
						AppController.round = gameService.addTileForMe(AppController.round.getGameInfo().getGameId(), tile.getLeft(), tile.getRight());
						reload();
						stage.close();
					} catch (DAIException ex) {
						WarnDialog.showWarnDialog(ex);
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
					reload();
				} catch (DAIException ex) {
					WarnDialog.showWarnDialog(ex);
				}
			}
		} else {
			try {
				AppController.round = gameService.playForOpponent(AppController.round.getGameInfo().getGameId(), new Move(tile.getLeft(), tile.getRight(), direction));
			} catch (DAIException ex) {
				WarnDialog.showWarnDialog(ex);
			}
			reload();
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
		}
		reload();
	}
}
