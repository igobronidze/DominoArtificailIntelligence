package ge.ai.domino.server.manager.domino.processor;

import ge.ai.domino.domain.domino.AIPrediction;
import ge.ai.domino.domain.domino.Game;
import ge.ai.domino.domain.domino.Hand;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.domain.domino.TableInfo;
import ge.ai.domino.domain.domino.Tile;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.server.caching.domino.CachedDominoGames;
import ge.ai.domino.server.manager.domino.DominoHelper;
import ge.ai.domino.server.manager.domino.logging.DominoLoggingProcessor;
import ge.ai.domino.server.manager.util.CloneUtil;
import ge.ai.domino.util.tile.TileUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HimTurnProcessor extends TurnProcessor {

    @Override
    public Hand addTile(Hand hand, int x, int y, boolean virtual) throws DAIException {
        if (virtual) {
            DominoLoggingProcessor.logInfoOnTurn("<<<<<<<Virtual Mode>>>>>>>", true);
        } else {
            DominoLoggingProcessor.logInfoOnTurn("<<<<<<<Real Mode<<<<<<<", false);
        }
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
            tableInfo.setOmittedHim(true);
            // თუ მე უკვე გავლილი მაქვს ვითხოვთ მოწინააღმდეგის დარჩენილი ქულების დათვლას
            if (tableInfo.isOmittedMe()) {
                hand.getTableInfo().setNeedToAddLeftTiles(true);
                return hand;
            }
            // ბოლოს აღებული ქვების რაოდენობის მიხედვით ნაწილდება ალბათობები
            if (tableInfo.getTileFromBazaar() > 0) {
                updateProbabilitiesForLastPickedTiles(hand, false);
            }
            // გავლის დაფიქსირება
            hand.getTableInfo().setMyTurn(true);
            if (!virtual) {
                AIPrediction aiPrediction = minMax.minMax(hand);
                hand.setAiPrediction(aiPrediction);
            }
            return hand;
        }
        // ქვის აღების დაფიქსირება
        tableInfo.setTileFromBazaar(tableInfo.getTileFromBazaar() + 1);
        updateTileCountBeforeAddHim(hand);

        DominoLoggingProcessor.logInfoOnTurn("Added tile for him, gameId[" + gameId + "]", virtual);
        DominoLoggingProcessor.logHandFullInfo(hand, virtual);
        return hand;
    }

    @Override
    public Hand play(Hand hand, int x, int y, PlayDirection direction, boolean virtual) throws DAIException {
        if (virtual) {
            DominoLoggingProcessor.logInfoOnTurn("<<<<<<<Virtual Mode>>>>>>>", true);
        } else {
            DominoLoggingProcessor.logInfoOnTurn("<<<<<<<Real Mode<<<<<<<", false);
        }
        hand.getTableInfo().setOmittedHim(false);
        int gameId = hand.getGameInfo().getGameId();
        DominoLoggingProcessor.logInfoOnTurn("Start play for him method for tile [" + x + "-" + y + "] direction [" + direction.name() + "], gameId[" + gameId + "]", virtual);
        // ისტორიაში დამატება
        Game game = CachedDominoGames.getGame(gameId);
        if (!virtual) {
            game.getHistory().push(CloneUtil.getClone(hand));
        }
        // თუ პირველი ხელის პირველი სვლაა, ანალიზდება არ ჩამოსული წყვილები
        if (hand.getTableInfo().isFirstHand() && hand.getTableInfo().getLeft() == null) {
            makeDoubleTilesAsInBazaar(hand, (x == y ? x : -1));
        }
        // თუ წინა სვლაზე იყო ბაზარში, აღებული ქვების რაოდენობით ნაწილდება ალბათობები
        Map<String, Tile> tiles = hand.getTiles();
        String uid = TileUtil.getTileUID(x, y);
        Tile playedTile = tiles.get(uid);
        if (hand.getTableInfo().getTileFromBazaar() > 0) {
            updateProbabilitiesForLastPickedTiles(hand, true);
            makeTileAsPlayed(tiles, uid);
        } else {
            // წინააღმდეგ შემთხვევაში, ჩამოსული ქვის ალბათობები ნაწილდბეა სხვებზე
            double him = playedTile.getHim();
            makeTileAsPlayed(tiles, uid);
            addProbabilitiesForHimProportional(tiles, tileSelection(tiles, true, true, true), him - 1);
        }
        // ქვის ჩამოსვლა
        playTile(hand.getTableInfo(), x, y, direction);
        updateTileCountBeforePlayHim(hand);
        DominoHelper.addLeftTiles(hand.getGameInfo(), countScore(hand), false, gameId, virtual);
        hand.getTableInfo().setMyTurn(true);
        // თუ ქვები აღარ აქვს, ვამთავრებთ ხელს
        if (hand.getTableInfo().getHimTilesCount() == 0) {
            return DominoHelper.finishedLastAndGetNewHand(hand, false, true, virtual);
        }
        // რჩევის მიღება
        if (!virtual) {
            AIPrediction aiPrediction = minMax.minMax(hand);
            hand.setAiPrediction(aiPrediction);
        }

        DominoLoggingProcessor.logInfoOnTurn("Played tile for him, gameId[" + gameId + "]", virtual);
        DominoLoggingProcessor.logHandFullInfo(hand, virtual);
        return hand;
    }

    /**
     * ყველა ქვა, რომლის ჩამოსვლაც შესაძლებელია კონკრეტულ მომენტში, ცხადდება როგორც ბაზარში არსებული და მისი ალბათობები უნაწილდება სხვებს
     * @param hand კონკრეტული ხელი
     */
    private void makeTilesAsInBazaarAndUpdateProbabilitiesForOther(Hand hand) {
        Set<Integer> possiblePlayNumbers = getPossiblePlayNumbers(hand);
        double himSum = 0.0;
        Set<String> mayHaveTiles = new HashSet<>();
        for (Tile tile : hand.getTiles().values()) {
            if (!tile.isMine()) {
                if (possiblePlayNumbers.contains(tile.getX()) || possiblePlayNumbers.contains(tile.getY())) {
                    himSum += tile.getHim();
                    tile.setHim(0);
                    tile.setMine(false);
                } else {
                    mayHaveTiles.add(TileUtil.getTileUID(tile.getX(), tile.getY()));
                }
            }
        }
        addProbabilitiesForHimProportional(hand.getTiles(), mayHaveTiles, himSum);
    }

    /**
     * ბოლოს აღებული ქვების რაოდენობით ალბათობა უნაწილდება ყველა იმ ქვას, რომელიც შეიძლება ქონოდა მოწინააღმდეგეს
     * @param hand კონკრეტული ხელი
     * @param played აღნიშნავს მოწინააღმდეგემ ითამაშა თუ არა(გამოძახება შეიძლება მომხდარიყო თამაშის ან გავლის შემდეგ)
     */
    private void updateProbabilitiesForLastPickedTiles(Hand hand, boolean played) {
        Set<Integer> notUsedNumbers = getPossiblePlayNumbers(hand);
        Set<String> usefulUIDs = new HashSet<>();
        for (Tile tile : hand.getTiles().values()) {
            if (!notUsedNumbers.contains(tile.getX()) && !notUsedNumbers.contains(tile.getY()) && !tile.isMine()) {
                usefulUIDs.add(TileUtil.getTileUID(tile.getX(), tile.getY()));
            }
        }
        double bazaarTilesCount = hand.getTableInfo().getTileFromBazaar();
        addProbabilitiesForBazaarProportional(hand.getTiles(), usefulUIDs, played ? bazaarTilesCount - 1 : bazaarTilesCount);
        hand.getTableInfo().setTileFromBazaar(0);
    }

    /**
     * ითვლება ის რიცხვები, რომელიც არის მაგიდის რომელიმე კუთხეში
     * @param hand კონკრეტული ხელი
     * @return მაგიდის კუთხის ქვების ბოლო ნაწილების სეტი
     */
    private Set<Integer> getPossiblePlayNumbers(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        Set<Integer> possiblePlayNumbers = new HashSet<>();
        if (tableInfo.getTop() != null) {
            possiblePlayNumbers.add(tableInfo.getTop().getOpenSide());
        }
        if (tableInfo.getRight() != null) {
            possiblePlayNumbers.add(tableInfo.getRight().getOpenSide());
        }
        if (tableInfo.getBottom() != null) {
            possiblePlayNumbers.add(tableInfo.getBottom().getOpenSide());
        }
        if (tableInfo.getLeft() != null) {
            possiblePlayNumbers.add(tableInfo.getLeft().getOpenSide());
        }
        return possiblePlayNumbers;
    }

    /**
     * მოწინააღმდეგის ბაზარში გასვლის შემდეგ ქვების რაოდენობის გადათვლა
     * @param hand კონკრეტული ხელი
     */
    private void updateTileCountBeforeAddHim(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setHimTilesCount(tableInfo.getHimTilesCount() + 1);
        tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
    }

    /**
     * მოწინააღმდეგის თამაშის შემდეგ ქვების რაოდენობის გადათვლა
     * @param hand კონკრეტული ხელი
     */
    private void updateTileCountBeforePlayHim(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setHimTilesCount(tableInfo.getHimTilesCount() - 1);
    }
}
