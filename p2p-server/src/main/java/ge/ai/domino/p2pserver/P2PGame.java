package ge.ai.domino.p2pserver;

import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveType;
import ge.ai.domino.domain.command.P2PCommand;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class P2PGame implements Runnable {

    private static final Logger logger = Logger.getLogger(P2PGame.class);

    private Socket player1;

    private Socket player2;

    private GameProperties gameProperties;

    private ObjectInputStream ois1;

    private ObjectInputStream ois2;

    private ObjectOutputStream oos1;

    private ObjectOutputStream oos2;

    private GameData gameData;

    private boolean firstPlayerCanListenPlayCommand;

    private boolean secondPlayerCanListenPlayCommand;

    public P2PGame(Socket player1, Socket player2, GameProperties gameProperties) throws IOException {
        this.player1 = player1;
        this.player2 = player2;
        this.gameProperties = gameProperties;

        this.oos1 = new ObjectOutputStream(player1.getOutputStream());
        this.oos2 = new ObjectOutputStream(player2.getOutputStream());
        this.ois1 = new ObjectInputStream(player1.getInputStream());
        this.ois2 = new ObjectInputStream(player2.getInputStream());
    }

    @Override
    public void run() {
        gameData = new GameData();
        gameData.initTiles();

        listenPlayer(true, ois1, oos1, oos2);
        listenPlayer(false, ois2, oos2, oos1);
    }

    private void listenPlayer(boolean firstPlayer, ObjectInputStream ois, ObjectOutputStream myOutputStream, ObjectOutputStream opponentOutputStream) {
        new Thread(() -> {
            try {
                boolean finished = false;
                while (!finished) {
                    P2PCommand command = (P2PCommand) ois.readObject();
                    logger.info("Get command " + command.name() + ", firstPlayer[" + firstPlayer + "]");
                    switch (command) {
                        case GET_GAME_PROPERTIES:
                            synchronized (P2PCommand.GET_GAME_PROPERTIES) {
                                myOutputStream.writeObject(gameProperties);
                                break;
                            }
                        case CAN_NOT_LISTEN_PLAY_COMMAND:
                            synchronized (P2PCommand.CAN_NOT_LISTEN_PLAY_COMMAND) {
                                if (firstPlayer) {
                                    firstPlayerCanListenPlayCommand = false;
                                } else {
                                    secondPlayerCanListenPlayCommand = false;
                                }
                            }
                            break;
                        case CAN_LISTEN_PLAY_COMMAND:
                            synchronized (P2PCommand.CAN_LISTEN_PLAY_COMMAND) {
                                if (firstPlayer) {
                                    firstPlayerCanListenPlayCommand = true;
                                } else {
                                    secondPlayerCanListenPlayCommand = true;
                                }
                            }
                            break;
                        case PLAY:
                            synchronized (P2PCommand.PLAY) {
                                sleepWhileCantListen(firstPlayer);
                                MoveType moveType = (MoveType) ois.readObject();
                                Move move = (Move) ois.readObject();
                                opponentOutputStream.writeObject(moveType);
                                opponentOutputStream.writeObject(move);
                            }
                            break;
                        case GET_RANDOM_TILE:
                            synchronized (P2PCommand.GET_RANDOM_TILE) {
                                Tile tile = gameData.getRandomTileAndAddInSet(firstPlayer);
                                logger.info("Random tile is " + tile);
                                myOutputStream.writeObject(tile);
                            }
                            break;
                        case GET_RANDOM_TILE_ADDITIONAL_TRY:
                            synchronized (P2PCommand.GET_RANDOM_TILE_ADDITIONAL_TRY) {
                                gameData.addLastDeletedTile();
                                Tile tileForAdd = gameData.getRandomTileAndAddInSet(firstPlayer);
                                logger.info("Random tile is " + tileForAdd);
                                myOutputStream.writeObject(tileForAdd);
                            }
                            break;
                        case GET_GAME_BEGINNER:
                            synchronized (P2PCommand.GET_GAME_BEGINNER) {
                                sleepWhileNotPickAllInitialTile();
                                boolean isFirstStarter = gameData.isFirstStarter();
                                if (firstPlayer) {
                                    myOutputStream.writeObject(isFirstStarter);
                                } else {
                                    myOutputStream.writeObject(!isFirstStarter);
                                }
                            }
                            break;
                        case RESET_GAME_DATA_BAZAAR:
                            synchronized (P2PCommand.RESET_GAME_DATA_BAZAAR) {
                                gameData.initTiles();
                            }
                            break;
                        case RESET_GAME_DATA_OPPONENT_TILES:
                            synchronized (P2PCommand.RESET_GAME_DATA_OPPONENT_TILES) {
                                if (firstPlayer) {
                                    gameData.setTiles2(new HashSet<>());
                                } else {
                                    gameData.setTiles1(new HashSet<>());
                                }
                            }
                            break;
                        case GET_OPPONENT_TILES:
                            synchronized (P2PCommand.GET_OPPONENT_TILES) {
                                Set<Tile> tiles;
                                if (firstPlayer) {
                                    tiles = gameData.getTiles2();
                                } else {
                                    tiles = gameData.getTiles1();
                                }
                                logger.info("Opponent tiles: " + tiles + ", firstPlayer: " + firstPlayer);
                                myOutputStream.writeObject(tiles);
                            }
                            break;
                        case FINISH:
                            synchronized (P2PCommand.FINISH) {
                                finished = true;
                                if (firstPlayer) {
                                    closeConnection(player1, ois1, oos1);
                                } else {
                                    closeConnection(player2, ois2, oos2);
                                }
                            }
                            break;
                    }
                    logger.info("Processed command " + command.name() + ", firstPlayer[" + firstPlayer + "]");
                }
            } catch (ClassNotFoundException | IOException | InterruptedException ex) {
                logger.error("Error occurred while play p2p game", ex);
                closeConnection(player1, ois1, oos1);
                closeConnection(player2, ois2, oos2);
            }
        }).start();
    }

    private void sleepWhileNotPickAllInitialTile() throws InterruptedException {
        while (gameData.getTiles1().size() != 7 || gameData.getTiles2().size() != 7) {
            Thread.sleep(100);
        }
    }

    private void sleepWhileCantListen(boolean firstPlayer) throws InterruptedException {
        if (firstPlayer) {
            while (!secondPlayerCanListenPlayCommand) {
                Thread.sleep(100);
            }
        } else {
            while (!firstPlayerCanListenPlayCommand) {
                Thread.sleep(100);
            }
        }
    }

    private void closeConnection(Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        try {
            oos.close();
            ois.close();
            socket.close();
        } catch (IOException ex) {
            logger.error("Can't close connections", ex);
        }
    }
}
