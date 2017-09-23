package ge.ai.domino.server.manager.domino.processor;

import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.domino.TileOwner;
import ge.ai.domino.server.caching.domino.CachedDominoGames;
import ge.ai.domino.server.manager.domino.DominoHelper;
import ge.ai.domino.server.manager.domino.logging.DominoLoggingProcessor;
import ge.ai.domino.server.manager.domino.minmax.MinMax;
import ge.ai.domino.server.manager.util.CloneUtil;
import ge.ai.domino.util.tile.TileUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HimTurnProcessor extends TurnProcessor {

    @Override
    public Hand addTile(Hand hand, int x, int y, boolean virtual) {
        int gameId = hand.getGameInfo().getGameId();
        DominoLoggingProcessor.logInfoOnTurn("Start add tile for him method, gameId[" + gameId + "]", virtual);
        TableInfo tableInfo = hand.getTableInfo();
        // ისტორაიში დამატება
        if (!virtual) {
            CachedDominoGames.getGame(hand.getGameInfo().getGameId()).getHistory().push(CloneUtil.getClone(hand));
        }
        // თუ პირველად გადის ბაზარში, ყველა შესაძლო ჩამოსვლადი ქვა არის ბაზარში და ნაწილდება მათი ალბათობები სხვებზე
        if (tableInfo.getTileFromBazaar() == 0) {
            makeTilesAsInBazaarAndUpdateProbabilitiesForOther(hand);
        }
        // თუ გაიარა
        if (tableInfo.getBazaarTilesCount() == 2) {
            // თუ მე უკვე გავლილი მაქვს ვითხოვთ მოწინააღმდეგის დარჩენილი ქულების დათვლას
            if (tableInfo.isOmittedMe()) {
                hand.getTableInfo().setNeedToAddLeftTiles(true);
                return hand;
            }
            // ბოლოს აღებული ქვების რაოდენობის მიხედვით ნაწილდება ალბათობები
            if (tableInfo.getTileFromBazaar() > 0) {
                updateProbabilitiesForLastPickedTiles(hand);
            }
            // გავლის დაფიქსირება
            tableInfo.setOmittedHim(true);
            hand.getTableInfo().setMyTurn(true);
            if (!virtual) {
                AIPrediction aiPrediction = MinMax.minMax(hand);
                hand.setAiPrediction(aiPrediction);
            }
            return hand;
        }
        // ქვის აღების დაფიქსირება
        tableInfo.setTileFromBazaar(tableInfo.getTileFromBazaar() + 1);
        updateTileCountBeforeAddHim(hand);

        DominoLoggingProcessor.logInfoOnTurn("Added tile for him, gameId[" + gameId + "]", virtual);
        DominoLoggingProcessor.logTilesFullInfo(hand, virtual);
        return hand;
    }

    @Override
    public Hand play(Hand hand, int x, int y, PlayDirection direction, boolean virtual) {
        int gameId = hand.getGameInfo().getGameId();
        DominoLoggingProcessor.logInfoOnTurn("Start play for him method for tile [" + x + "-" + y + "] direction [" + direction.name() + "], gameId[" + gameId + "]", virtual);
        // ისტორიაში დამატება
        Game game = CachedDominoGames.getGame(gameId);
        if (!virtual) {
            game.getHistory().push(CloneUtil.getClone(hand));
        }
        // თუ პირველი ხელის პირველი სვლაა, ანალიზდება არ ჩამოსული წყვილები
        if (game.getGameProperties().isFirstHand() && hand.getTableInfo().getLeft() == null) {
            makeDoubleTilesAsInBazaar(hand, (x == y ? x : -1));
        }
        // თუ წინა სვლაზე იყო ბაზარში, აღებული ქვების რაოდენობით ნაწილდება ალბათობები
        if (hand.getTableInfo().getTileFromBazaar() > 0) {
            updateProbabilitiesForLastPickedTiles(hand);
        } else {
            // წინააღმდეგ შემთხვევაში, ჩამოსული ქვის ალბათობები ნაწილდბეა სხვებზე
            Map<String, Tile> tiles = hand.getTiles();
            Tile playedTile = tiles.get(TileUtil.getTileUID(x, y));
            double him = playedTile.getHim();
            double bazaar = playedTile.getBazaar();
            DominoHelper.makeTileAsPlayed(playedTile);
            DominoHelper.addProbabilitiesProportional(tiles, DominoHelper.getNotPlayedMineOrBazaarTiles(tiles), him - 1, TileOwner.HIM);
            DominoHelper.addProbabilitiesProportional(tiles, DominoHelper.getNotPlayedMineOrBazaarTiles(tiles), bazaar, TileOwner.BAZAAR);
        }
        // ქვის ჩამოსვლა
        DominoHelper.playTile(hand.getTableInfo(), x, y, direction);
        DominoHelper.updateTileCountBeforePlayHim(hand);
        hand.getGameInfo().setHimPoints(hand.getGameInfo().getHimPoints() + DominoHelper.countScore(hand));
        hand.getTableInfo().setMyTurn(true);
        // თუ ქვები აღარ აქვს, ვამთავრებთ ხელს
        if (hand.getTableInfo().getHimTilesCount() == 0) {
            hand.getTableInfo().setNeedToAddLeftTiles(true);
            return hand;
        }
        // რჩევის მიღება
        if (!virtual) {
            AIPrediction aiPrediction = MinMax.minMax(hand);
            hand.setAiPrediction(aiPrediction);
        }

        DominoLoggingProcessor.logInfoOnTurn("Played tile for him, gameId[" + gameId + "]", virtual);
        DominoLoggingProcessor.logTilesFullInfo(hand, virtual);
        return hand;
    }

    @SuppressWarnings("Duplicates")
    private void makeTilesAsInBazaarAndUpdateProbabilitiesForOther(Hand hand) {
        Set<Integer> notUsedNumbers = getNotUsedNumbers(hand);
        double himSum = 0.0;
        double bazaarSum = 0.0;
        Set<String> mayHaveTiles = new HashSet<>();
        for (Tile tile : hand.getTiles().values()) {
            if (!tile.isPlayed() && tile.getMe() != 1.0) {
                if (notUsedNumbers.contains(tile.getX()) || notUsedNumbers.contains(tile.getY())) {
                    himSum += tile.getHim();
                    bazaarSum += (1.0 - tile.getBazaar());
                    tile.setHim(0);
                    tile.setMe(0);
                    tile.setBazaar(1.0);
                } else {
                    mayHaveTiles.add(TileUtil.getTileUID(tile.getX(), tile.getY()));
                }
            }
        }
        DominoHelper.addProbabilitiesProportional(hand.getTiles(), mayHaveTiles, himSum, TileOwner.HIM);
        DominoHelper.addProbabilitiesProportional(hand.getTiles(), mayHaveTiles, -1 * bazaarSum, TileOwner.BAZAAR);
    }

    private Set<Integer> getNotUsedNumbers(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        Set<Integer> notUsedNumbers = new HashSet<>();
        if (tableInfo.getTop() != null) {
            notUsedNumbers.add(tableInfo.getTop().getOpenSide());
        }
        if (tableInfo.getRight() != null) {
            notUsedNumbers.add(tableInfo.getRight().getOpenSide());
        }
        if (tableInfo.getBottom() != null) {
            notUsedNumbers.add(tableInfo.getBottom().getOpenSide());
        }
        if (tableInfo.getLeft() != null) {
            notUsedNumbers.add(tableInfo.getLeft().getOpenSide());
        }
        return notUsedNumbers;
    }

    private void updateProbabilitiesForLastPickedTiles(Hand hand) {
        Set<Integer> notUsedNumbers = getNotUsedNumbers(hand);
        Set<String> usefulUIDs = new HashSet<>();
        for (Tile tile : hand.getTiles().values()) {
            if (!notUsedNumbers.contains(tile.getX()) && !notUsedNumbers.contains(tile.getY()) && !tile.isPlayed() && tile.getMe() != 1.0) {
                usefulUIDs.add(TileUtil.getTileUID(tile.getX(), tile.getY()));
            }
        }
        double bazaarTilesCount = hand.getTableInfo().getTileFromBazaar();
        DominoHelper.addProbabilitiesProportional(hand.getTiles(), usefulUIDs, bazaarTilesCount, TileOwner.HIM);
        DominoHelper.addProbabilitiesProportional(hand.getTiles(), usefulUIDs, -1 * bazaarTilesCount, TileOwner.BAZAAR);
        hand.getTableInfo().setTileFromBazaar(0);
    }

    private void updateTileCountBeforeAddHim(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setHimTilesCount(tableInfo.getHimTilesCount() + 1);
        tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
    }
}
