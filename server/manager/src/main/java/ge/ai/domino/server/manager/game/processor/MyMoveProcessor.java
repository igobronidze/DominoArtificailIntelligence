package ge.ai.domino.server.manager.game.processor;

import ge.ai.domino.domain.ai.AIPrediction;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.domain.tile.OpponentTile;
import ge.ai.domino.domain.tile.Tile;
import ge.ai.domino.server.caching.game.CachedGames;
import ge.ai.domino.server.manager.game.GameHelper;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import ge.ai.domino.server.manager.played.MoveHelper;
import ge.ai.domino.server.manager.sysparam.SystemParameterManager;
import ge.ai.domino.server.manager.util.CloneUtil;

import java.util.Map;

public class MyMoveProcessor extends MoveProcessor {

    private final SystemParameterManager sysParamManager = new SystemParameterManager();

    private final SysParam minMaxOnFirstTile = new SysParam("minMaxOnFirstTile", "false");

    @Override
    public Round addTile(Round round, int left, int right, boolean virtual) throws DAIException {
        if (virtual) {
            GameLoggingProcessor.logInfoAboutMove("<<<<<<<Virtual Mode>>>>>>>", true);
        } else {
            GameLoggingProcessor.logInfoAboutMove("<<<<<<<Real Mode<<<<<<<", false);
        }
        int gameId = round.getGameInfo().getGameId();
        GameLoggingProcessor.logInfoAboutMove("Start add tile for me method for tile [" + left + "-" + right + "], gameId[" + gameId + "]", virtual);
        TableInfo tableInfo = round.getTableInfo();
        // ისტორიაში დამატება
        Game game = CachedGames.getGame(gameId);
        if (!virtual) {
            game.getHistory().push(CloneUtil.getClone(round));
        }
        // თუ გავიარე -> ა) უკვე გავლილი აქვს მოწინააღმდეგეს და ვამთავრებთ  ბ) გადაეცა სვლა მოწინააღმდეგეს
        if (tableInfo.getBazaarTilesCount() == 2) {
            tableInfo.setOmittedMe(true);
            if (!virtual) {
                CachedGames.addMove(gameId, MoveHelper.getOmittedMeMove(), false);
            }
            if (tableInfo.isOmittedOpponent()) {
                round.getTableInfo().setNeedToAddLeftTiles(true);
                return round;
            } else {
                round.getTableInfo().setMyMove(false);
                return round;
            }
        }
        // აღებული ქვის ალბათობების შეცვლა და ცვლილების სხვებისთვის გადანაწილება
        Map<Integer, OpponentTile> tiles = round.getOpponentTiles();
        double prob = tiles.get(new Tile(left, right).hashCode()).getProb();
        round.getOpponentTiles().remove(new Tile(left, right).hashCode());
        round.getMyTiles().add(new Tile(left, right));
        addProbabilitiesForOpponentProbsProportional(tileSelection(tiles.values(), true, true), prob);
        updateTileCountBeforeAddMe(round);
        // თუ ეხლა დავამთავრე საწყისი 7 ქვის აღება ვითხოვთ რჩევას პროგრამისგან
        if (tableInfo.getLeft() == null && round.getTableInfo().getMyTilesCount() == 7) {
            if (CachedGames.startOpponent(gameId) && !virtual) {
                round.getTableInfo().setMyMove(false);
            }
            if (sysParamManager.getBooleanParameterValue(minMaxOnFirstTile) && !round.getTableInfo().isFirstRound() && round.getTableInfo().isMyMove() && !virtual) {
                AIPrediction aiPrediction = minMax.minMax(round);
                round.setAiPrediction(aiPrediction);
            }
        }
        // თუ უკვე ჩამოსულია ქვა, ვითხოვთ რჩევას პროგრამისგან
        if (round.getTableInfo().getLeft() != null && !virtual) {
            AIPrediction aiPrediction = minMax.minMax(round);
            round.setAiPrediction(aiPrediction);
        }

        if (!virtual) {
            if (tableInfo.getLeft() == null) {
                CachedGames.addMove(gameId, MoveHelper.getAddInitialTileForMeMove(left, right), tableInfo.getMyTilesCount() == 1);
            } else {
                CachedGames.addMove(gameId, MoveHelper.getAddTileForMeMove(left, right), false);
            }
        }

        GameLoggingProcessor.logInfoAboutMove("Added tile for me, gameId[" + gameId + "]", virtual);
        GameLoggingProcessor.logRoundFullInfo(round, virtual);
        return round;
    }

    @Override
    public Round play(Round round, Move move, boolean virtual) {
        if (virtual) {
            GameLoggingProcessor.logInfoAboutMove("<<<<<<<Virtual Mode>>>>>>>", true);
        } else {
            GameLoggingProcessor.logInfoAboutMove("<<<<<<<Real Mode<<<<<<<", false);
        }
        int left = move.getLeft();
        int right = move.getRight();
        MoveDirection direction = move.getDirection();
        round.getTableInfo().setOmittedMe(false);
        int gameId = round.getGameInfo().getGameId();
        GameLoggingProcessor.logInfoAboutMove("Start play for me method for tile [" + left + "-" + right + "] direction [" + direction.name() + "], gameId[" + gameId + "]", virtual);
        // ისტორიაში დამატება
        Game game = CachedGames.getGame(gameId);
        if (!virtual) {
            game.getHistory().push(CloneUtil.getClone(round));
        }
        // თუ პირველი ხელის პირველი ჩამოსვლაა, ვაანალიზებთ არ ჩამოსულ მაღალ წყვილებს
        if (round.getTableInfo().isFirstRound() && round.getTableInfo().getLeft() == null) {
            makeTwinTilesAsInBazaar(round.getOpponentTiles().values(), (left == right ? left : -1));
        }
        // ქვის ჩამოსვლა
        Tile tmpTile = new Tile(move.getLeft(), move.getRight());
        round.getMyTiles().remove(tmpTile);
        playTile(round, move);
        updateTileCountBeforePlayMe(round);
        GameHelper.addLeftTiles(round.getGameInfo(), countScore(round), true, gameId, virtual);
        round.getTableInfo().setMyMove(false);

        if (!virtual) {
            CachedGames.addMove(gameId, MoveHelper.getPlayForMeMove(left, right, direction),false);
        }

        // თუ ქვები აღარ მაქვს ვამთავრებთ
        if (round.getTableInfo().getMyTilesCount() == 0) {
            round.getTableInfo().setNeedToAddLeftTiles(true);
            return round;
        }

        GameLoggingProcessor.logInfoAboutMove("Played tile for me, gameId[" + gameId + "]", virtual);
        GameLoggingProcessor.logRoundFullInfo(round, virtual);
        return round;
    }

    /**
     * ჩემთვის ბაზარში გასვლის შემდეგ ქვების რაოდენობის გადათვლა
     * @param round კონკრეტული ხელი
     */
    private void updateTileCountBeforeAddMe(Round round) {
        TableInfo tableInfo = round.getTableInfo();
        tableInfo.setMyTilesCount(tableInfo.getMyTilesCount() + 1);
        tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
    }

    /**
     * ჩემთვის თამაშის შემდეგ ქვების რაოდენობის გადათვლა
     * @param round კონკრეტული ხელი
     */
    private void updateTileCountBeforePlayMe(Round round) {
        TableInfo tableInfo = round.getTableInfo();
        tableInfo.setMyTilesCount(tableInfo.getMyTilesCount() - 1);
    }
}
