package ge.ai.domino.server.manager.game.processor;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.tile.OpponentTile;
import ge.ai.domino.domain.tile.Tile;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.GameHelper;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.played.MoveHelper;
import ge.ai.domino.server.manager.util.CloneUtil;

import java.util.*;

public class OpponentMoveProcessor extends MoveProcessor {

    @Override
    public Round addTile(Round round, int left, int right, boolean virtual) throws DAIException {
        if (virtual) {
            GameLoggingProcessor.logInfoAboutMove("<<<<<<<Virtual Mode>>>>>>>", true);
        } else {
            GameLoggingProcessor.logInfoAboutMove("<<<<<<<Real Mode<<<<<<<", false);
        }
        int gameId = round.getGameInfo().getGameId();
        GameLoggingProcessor.logInfoAboutMove("Start add tile for opponent method, gameId[" + gameId + "]", virtual);
        TableInfo tableInfo = round.getTableInfo();
        Game game = CachedGames.getGame(gameId);
        // ისტორაიში დამატება
        if (!virtual) {
            game.getHistory().push(CloneUtil.getClone(round));
        }
        // თუ პირველად გადის ბაზარში, ყველა შესაძლო ჩამოსვლადი ქვა არის ბაზარში და ნაწილდება მათი ალბათობები სხვებზე
        if (tableInfo.getTilesFromBazaar() == 0) {
            makeTilesAsInBazaarAndUpdateProbabilitiesForOther(round);
        }
        // თუ გაიარა
        if (tableInfo.getBazaarTilesCount() == 2) {
            tableInfo.setOmittedOpponent(true);
            if (!virtual) {
                CachedGames.addMove(gameId, MoveHelper.getOmittedOpponentMove(), false);
            }
            // თუ მე უკვე გავლილი მაქვს ვითხოვთ მოწინააღმდეგის დარჩენილი ქულების დათვლას
            if (tableInfo.isOmittedMe()) {
                round.getTableInfo().setNeedToAddLeftTiles(true);
                return round;
            }
            // ბოლოს აღებული ქვების რაოდენობის მიხედვით ნაწილდება ალბათობები
            if (tableInfo.getTilesFromBazaar() > 0) {
                updateProbabilitiesForLastPickedTiles(round, false);
            }
            // გავლის დაფიქსირება
            round.getTableInfo().setMyMove(true);
            if (!virtual) {
                Move aiPrediction = minMax.minMax(round);
                round.setAiPrediction(aiPrediction);
            }
            return round;
        }
        // ქვის აღების დაფიქსირება
        tableInfo.setTilesFromBazaar(tableInfo.getTilesFromBazaar() + 1);
        updateTileCountBeforeAddOpponent(round);

        if (!virtual) {
            CachedGames.addMove(gameId, MoveHelper.getAddTileForOpponentMove(), false);
        }

        GameLoggingProcessor.logInfoAboutMove("Added tile for opponent, gameId[" + gameId + "]", virtual);
        GameLoggingProcessor.logRoundFullInfo(round, virtual);
        return round;
    }

    @Override
    public Round play(Round round, Move move, boolean virtual) throws DAIException {
        if (virtual) {
            GameLoggingProcessor.logInfoAboutMove("<<<<<<<Virtual Mode>>>>>>>", true);
        } else {
            GameLoggingProcessor.logInfoAboutMove("<<<<<<<Real Mode<<<<<<<", false);
        }
        int left = move.getLeft();
        int right = move.getRight();
        MoveDirection direction = move.getDirection();
        round.getTableInfo().setOmittedOpponent(false);
        int gameId = round.getGameInfo().getGameId();
        GameLoggingProcessor.logInfoAboutMove("Start play for opponent method for tile [" + left + "-" + right + "] direction [" + direction.name() + "], gameId[" + gameId + "]", virtual);
        Game game = CachedGames.getGame(gameId);
        // ისტორიაში დამატება
        if (!virtual) {
            game.getHistory().push(CloneUtil.getClone(round));
        }
        // თუ პირველი ხელის პირველი სვლაა, ანალიზდება არ ჩამოსული წყვილები
        Map<Integer, OpponentTile> tiles = round.getOpponentTiles();
        if (round.getTableInfo().isFirstRound() && round.getTableInfo().getLeft() == null) {
            makeTwinTilesAsInBazaar(tiles.values(), (left == right ? left : -1));
        }
        // თუ წინა სვლაზე იყო ბაზარში, აღებული ქვების რაოდენობით ნაწილდება ალბათობები
        int hash = new Tile(left, right).hashCode();
        OpponentTile playedTile = tiles.get(hash);
        round.getTableInfo().setLastPlayedProb(playedTile.getProb());
        tiles.remove(hash);
        if (round.getTableInfo().getTilesFromBazaar() > 0) {
            updateProbabilitiesForLastPickedTiles(round, true);
        } else {
            // წინააღმდეგ შემთხვევაში, ჩამოსული ქვის ალბათობები ნაწილდბეა სხვებზე
            double prob = playedTile.getProb();
            if (prob != 1.0) {
                addProbabilitiesForOpponentProbsProportional(tileSelection(tiles.values(), true, true), prob - 1);
            }
        }
        // ქვის ჩამოსვლა
        playTile(round, move);
        updateTileCountBeforePlayOpponent(round);
        GameHelper.addLeftTiles(round.getGameInfo(), countScore(round), false, gameId, virtual);
        round.getTableInfo().setMyMove(true);

        if (!virtual) {
            CachedGames.addMove(gameId, MoveHelper.getPlayForOpponentMove(left, right, direction), false);
        }

        // თუ ქვები აღარ აქვს, ვამთავრებთ ხელს
        if (round.getTableInfo().getOpponentTilesCount() == 0) {
            return GameHelper.finishedLastAndGetNewRound(round, false, true, virtual);
        }
        // რჩევის მიღება
        if (!virtual) {
            Move aiPrediction = minMax.minMax(round);
            round.setAiPrediction(aiPrediction);
        }

        GameLoggingProcessor.logInfoAboutMove("Played tile for opponent, gameId[" + gameId + "]", virtual);
        GameLoggingProcessor.logRoundFullInfo(round, virtual);
        return round;
    }

    /**
     * ყველა ქვა, რომლის ჩამოსვლაც შესაძლებელია კონკრეტულ მომენტში, ცხადდება როგორც ბაზარში არსებული და მისი ალბათობები უნაწილდება სხვებს
     * @param round კონკრეტული ხელი
     */
    private void makeTilesAsInBazaarAndUpdateProbabilitiesForOther(Round round) {
        Set<Integer> possiblePlayNumbers = getPossiblePlayNumbers(round);
        double sum = 0.0;
        Set<OpponentTile> possibleTiles = new HashSet<>();
        for (OpponentTile tile : round.getOpponentTiles().values()) {
            if (possiblePlayNumbers.contains(tile.getLeft()) || possiblePlayNumbers.contains(tile.getRight())) {
                sum += tile.getProb();
                tile.setProb(0);
            } else {
                possibleTiles.add(tile);
            }
        }
        addProbabilitiesForOpponentProbsProportional(possibleTiles, sum);
    }

    /**
     * ბოლოს აღებული ქვების რაოდენობით ალბათობა უნაწილდება ყველა იმ ქვას, რომელიც შეიძლება ქონოდა მოწინააღმდეგეს
     * @param round კონკრეტული ხელი
     * @param played აღნიშნავს მოწინააღმდეგემ ითამაშა თუ არა(გამოძახება შეიძლება მომხდარიყო თამაშის ან გავლის შემდეგ)
     */
    private void updateProbabilitiesForLastPickedTiles(Round round, boolean played) {
        Set<Integer> notUsedNumbers = getPossiblePlayNumbers(round);
        List<OpponentTile> usefulTiles = new ArrayList<>();
        for (OpponentTile tile : round.getOpponentTiles().values()) {
            if (!notUsedNumbers.contains(tile.getLeft()) && !notUsedNumbers.contains(tile.getRight())) {
                usefulTiles.add(tile);
            }
        }
        double bazaarTilesCount = round.getTableInfo().getTilesFromBazaar();
        addProbabilitiesForBazaarProportional(usefulTiles, played ? bazaarTilesCount - 1 : bazaarTilesCount);
        round.getTableInfo().setTilesFromBazaar(0);
    }

    /**
     * ითვლება ის რიცხვები, რომელიც არის მაგიდის რომელიმე კუთხეში
     * @param round კონკრეტული ხელი
     * @return მაგიდის კუთხის ქვების ბოლო ნაწილების სეტი
     */
    private Set<Integer> getPossiblePlayNumbers(Round round) {
        TableInfo tableInfo = round.getTableInfo();
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
     * @param round კონკრეტული ხელი
     */
    private void updateTileCountBeforeAddOpponent(Round round) {
        TableInfo tableInfo = round.getTableInfo();
        tableInfo.setOpponentTilesCount(tableInfo.getOpponentTilesCount() + 1);
        tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
    }

    /**
     * მოწინააღმდეგის თამაშის შემდეგ ქვების რაოდენობის გადათვლა
     * @param round კონკრეტული ხელი
     */
    private void updateTileCountBeforePlayOpponent(Round round) {
        TableInfo tableInfo = round.getTableInfo();
        tableInfo.setOpponentTilesCount(tableInfo.getOpponentTilesCount() - 1);
    }
}
