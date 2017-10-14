package ge.ai.domino.server.manager.domino.processor;

import ge.ai.domino.domain.ai.AIPrediction;
import ge.ai.domino.domain.domino.game.Game;
import ge.ai.domino.domain.domino.game.Hand;
import ge.ai.domino.domain.domino.game.PlayDirection;
import ge.ai.domino.domain.domino.game.TableInfo;
import ge.ai.domino.domain.domino.game.Tile;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.server.caching.domino.CachedDominoGames;
import ge.ai.domino.server.manager.domino.DominoHelper;
import ge.ai.domino.server.manager.domino.logging.DominoLoggingProcessor;
import ge.ai.domino.server.manager.playedgame.TurnHelper;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.server.manager.util.CloneUtil;
import ge.ai.domino.util.tile.TileUtil;

import java.util.Map;

public class MyTurnProcessor extends TurnProcessor {

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam minMaxOnFirstTile = new SysParam("minMaxOnFirstTile", "false");

    @Override
    public Hand addTile(Hand hand, int x, int y, boolean virtual) throws DAIException {
        if (virtual) {
            DominoLoggingProcessor.logInfoOnTurn("<<<<<<<Virtual Mode>>>>>>>", true);
        } else {
            DominoLoggingProcessor.logInfoOnTurn("<<<<<<<Real Mode<<<<<<<", false);
        }
        int gameId = hand.getGameInfo().getGameId();
        DominoLoggingProcessor.logInfoOnTurn("Start add tile for me method for tile [" + x + "-" + y + "], gameId[" + gameId + "]", virtual);
        TableInfo tableInfo = hand.getTableInfo();
        // ისტორიაში დამატება
        Game game = CachedDominoGames.getGame(gameId);
        if (!virtual) {
            game.getHistory().push(CloneUtil.getClone(hand));
        }
        // თუ გავიარე -> ა) უკვე გავლილი აქვს მოწინააღმდეგეს და ვამთავრებთ  ბ) გადაეცა სვლა მოწინააღმდეგეს
        if (tableInfo.getBazaarTilesCount() == 2) {
            tableInfo.setOmittedMe(true);
            if (!virtual) {
                CachedDominoGames.addTurn(gameId, TurnHelper.getOmittedMeTurn(), false);
            }
            if (tableInfo.isOmittedHim()) {
                hand.getTableInfo().setNeedToAddLeftTiles(true);
                return hand;
            } else {
                hand.getTableInfo().setMyTurn(false);
                return hand;
            }
        }
        // აღებული ქვის ალბათობების შეცვლა და ცვლილების სხვებისთვის გადანაწილება
        Map<String, Tile> tiles = hand.getTiles();
        Tile tile = tiles.get(TileUtil.getTileUID(x, y));
        double him = tile.getHim();
        makeTileAsMine(tile);
        addProbabilitiesForHimProportional(tiles, tileSelection(tiles, true, true, true), him);
        updateTileCountBeforeAddMe(hand);
        // თუ ეხლა დავამთავრე საწყისი 7 ქვის აღება ვითხოვთ რჩევას პროგრამისგან
        if (tableInfo.getLeft() == null && hand.getTableInfo().getMyTilesCount() == 7) {
            if (CachedDominoGames.startHim(gameId) && !virtual) {
                hand.getTableInfo().setMyTurn(false);
            }
            if (sysParamManager.getBooleanParameterValue(minMaxOnFirstTile) && !hand.getTableInfo().isFirstHand() && hand.getTableInfo().isMyTurn() && !virtual) {
                AIPrediction aiPrediction = minMax.minMax(hand);
                hand.setAiPrediction(aiPrediction);
            }
        }
        // თუ უკვე ჩამოსულია ქვა, ვითხოვთ რჩევას პროგრამისგან
        if (hand.getTableInfo().getLeft() != null && !virtual) {
            AIPrediction aiPrediction = minMax.minMax(hand);
            hand.setAiPrediction(aiPrediction);
        }

        if (!virtual) {
            if (tableInfo.getLeft() == null) {
                CachedDominoGames.addTurn(gameId, TurnHelper.getAddInitialTileForMeTurn(x, y), tableInfo.getMyTilesCount() == 1);
            } else {
                CachedDominoGames.addTurn(gameId, TurnHelper.getAddTileForMeTurn(x, y), false);
            }
        }

        DominoLoggingProcessor.logInfoOnTurn("Added tile for me, gameId[" + gameId + "]", virtual);
        DominoLoggingProcessor.logHandFullInfo(hand, virtual);
        return hand;
    }

    @Override
    public Hand play(Hand hand, int x, int y, PlayDirection direction, boolean virtual) {
        if (virtual) {
            DominoLoggingProcessor.logInfoOnTurn("<<<<<<<Virtual Mode>>>>>>>", true);
        } else {
            DominoLoggingProcessor.logInfoOnTurn("<<<<<<<Real Mode<<<<<<<", false);
        }
        hand.getTableInfo().setOmittedMe(false);
        int gameId = hand.getGameInfo().getGameId();
        DominoLoggingProcessor.logInfoOnTurn("Start play for me method for tile [" + x + "-" + y + "] direction [" + direction.name() + "], gameId[" + gameId + "]", virtual);
        // ისტორიაში დამატება
        Game game = CachedDominoGames.getGame(gameId);
        if (!virtual) {
            game.getHistory().push(CloneUtil.getClone(hand));
        }
        // თუ პირველი ხელის პირველი ჩამოსვლაა, ვაანალიზებთ არ ჩამოსულ მაღალ წყვილებს
        if (hand.getTableInfo().isFirstHand() && hand.getTableInfo().getLeft() == null) {
            makeDoubleTilesAsInBazaar(hand, (x == y ? x : -1));
        }
        // ქვის ჩამოსვლა
        makeTileAsPlayed(hand.getTiles(), TileUtil.getTileUID(x, y));
        playTile(hand.getTableInfo(), x, y, direction);
        updateTileCountBeforePlayMe(hand);
        DominoHelper.addLeftTiles(hand.getGameInfo(), countScore(hand), true, gameId, virtual);
        hand.getTableInfo().setMyTurn(false);

        if (!virtual) {
            CachedDominoGames.addTurn(gameId, TurnHelper.getPlayForMeTurn(x, y, direction),false);
        }

        // თუ ქვები აღარ მაქვს ვამთავრებთ
        if (hand.getTableInfo().getMyTilesCount() == 0) {
            hand.getTableInfo().setNeedToAddLeftTiles(true);
            return hand;
        }

        DominoLoggingProcessor.logInfoOnTurn("Played tile for me, gameId[" + gameId + "]", virtual);
        DominoLoggingProcessor.logHandFullInfo(hand, virtual);
        return hand;
    }

    /**
     * ქვის ჩემ ქვად გამოცხადება
     * @param tile კონკრეტული ქვა
     */
    private void makeTileAsMine(Tile tile) {
        tile.setMine(true);
        tile.setHim(0.0);
    }

    /**
     * ჩემთვის ბაზარში გასვლის შემდეგ ქვების რაოდენობის გადათვლა
     * @param hand კონკრეტული ხელი
     */
    private void updateTileCountBeforeAddMe(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setMyTilesCount(tableInfo.getMyTilesCount() + 1);
        tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
    }

    /**
     * ჩემთვის თამაშის შემდეგ ქვების რაოდენობის გადათვლა
     * @param hand კონკრეტული ხელი
     */
    private void updateTileCountBeforePlayMe(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setMyTilesCount(tableInfo.getMyTilesCount() - 1);
    }
}
