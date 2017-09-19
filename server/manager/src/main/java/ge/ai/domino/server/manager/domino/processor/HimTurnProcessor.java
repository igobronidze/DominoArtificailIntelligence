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
import ge.ai.domino.server.manager.domino.MinMax;
import ge.ai.domino.server.manager.domino.logging.DominoLoggingProcessor;
import ge.ai.domino.server.manager.util.CloneUtil;
import ge.ai.domino.util.tile.TileUtil;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HimTurnProcessor extends TurnProcessor {

    private static final Logger logger = Logger.getLogger(HimTurnProcessor.class);

    @Override
    public Hand addTile(Hand hand, int x, int y) {
        int gameId = hand.getGameInfo().getGameId();
        logger.info("Start add tile for him method, gameId[" + gameId + "]");
        // ისტორაიში დამატება
        CachedDominoGames.getGame(hand.getGameInfo().getGameId()).getHistory().push(CloneUtil.getClone(hand));
        // თუ პირველად გადის ბაზარში, ყველა შესაძლო ჩამოსვლადი ქვა არის ბაზარში და ნაწილდება მათი ალბათობები სხვებზე
        if (CachedDominoGames.getTileFromBazaar(gameId) == null || CachedDominoGames.getTileFromBazaar(gameId) == 0) {
            makeTilesAsInBazaarAndUpdateProbabilitiesForOther(hand);
        }
        // თუ გაიარა
        if (hand.getTableInfo().getBazaarTilesCount() == 2) {
            // თუ მე უკვე გავლილი მაქვს ვითხოვთ მოწინააღმდეგის დარჩენილი ქულების დათვლას
            if (TileOwner.ME.equals(CachedDominoGames.getOmittedValue(hand.getGameInfo().getGameId()))) {
                hand.getTableInfo().setNeedToAddLeftTiles(true);
                return hand;
            }
            // ბოლოს აღებულიქ ვების რაოდენობის მიხედვით ნაწილდება ალბათობები
            if (CachedDominoGames.getTileFromBazaar(gameId) != null && CachedDominoGames.getTileFromBazaar(gameId) > 0) {
                updateProbabilitiesForLastPickedTiles(hand, gameId);
            }
            // გავლის დაფიქსირება
            CachedDominoGames.addOmitted(hand.getGameInfo().getGameId(), TileOwner.HIM);
            hand.getTableInfo().setMyTurn(true);
            AIPrediction aiPrediction = MinMax.minMax(CloneUtil.getClone(hand));
            hand.setAiPrediction(aiPrediction);
            return hand;
        }
        // ქვის აღების დაფიქსირება
        CachedDominoGames.addTileFromBazaar(gameId, CachedDominoGames.getTileFromBazaar(gameId) == null ? 1 : CachedDominoGames.getTileFromBazaar(gameId) + 1);
        updateTileCountBeforeAddHim(hand);

        logger.info("Added tile for him, gameId[" + gameId + "]");
        DominoLoggingProcessor.logTilesFullInfo(hand);
        return hand;
    }

    @Override
    public Hand play(Hand hand, int x, int y, PlayDirection direction) {
        int gameId = hand.getGameInfo().getGameId();
        logger.info("Start play for him method for tile [" + x + "-" + y + "] direction [" + direction.name() + "], gameId[" + gameId + "]");
        // ისტორიაში დამატება
        Game game = CachedDominoGames.getGame(gameId);
        game.getHistory().push(CloneUtil.getClone(hand));
        // თუ პირველი ხელის პირველი სვლაა, ანალიზდება არ ჩამოსული წყვილები
        if (game.getGameProperties().isFirstHand() && hand.getTableInfo().getLeft() == null) {
            makeDoubleTilesAsInBazaar(hand, (x == y ? x : -1));
        }
        // თუ წინა სვლაზე იყო ბაზარში, აღებული ქვების რაოდენობით ნაწილდება ალბათობები
        if (CachedDominoGames.getTileFromBazaar(gameId) != null && CachedDominoGames.getTileFromBazaar(gameId) > 0) {
            updateProbabilitiesForLastPickedTiles(hand, gameId);
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
            return DominoHelper.finishedLastAndGetNewHand(hand, false, true);
        }
        // რჩევის მიღებაs
        AIPrediction aiPrediction = MinMax.minMax(CloneUtil.getClone(hand));
        hand.setAiPrediction(aiPrediction);

        logger.info("Played tile for him, gameId[" + gameId + "]");
        DominoLoggingProcessor.logTilesFullInfo(hand);
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

    private void updateProbabilitiesForLastPickedTiles(Hand hand, int gameId) {
        Set<Integer> notUsedNumbers = getNotUsedNumbers(hand);
        Set<String> usefulUIDs = new HashSet<>();
        for (Tile tile : hand.getTiles().values()) {
            if (!notUsedNumbers.contains(tile.getX()) && !notUsedNumbers.contains(tile.getY()) && !tile.isPlayed() && tile.getMe() != 1.0) {
                usefulUIDs.add(TileUtil.getTileUID(tile.getX(), tile.getY()));
            }
        }
        double bazaarTilesCount = CachedDominoGames.getTileFromBazaar(gameId);
        DominoHelper.addProbabilitiesProportional(hand.getTiles(), usefulUIDs, bazaarTilesCount, TileOwner.HIM);
        DominoHelper.addProbabilitiesProportional(hand.getTiles(), usefulUIDs, -1 * bazaarTilesCount, TileOwner.BAZAAR);
        CachedDominoGames.addTileFromBazaar(gameId, 0);
    }

    private void updateTileCountBeforeAddHim(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setHimTilesCount(tableInfo.getHimTilesCount() + 1);
        tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
    }
}
