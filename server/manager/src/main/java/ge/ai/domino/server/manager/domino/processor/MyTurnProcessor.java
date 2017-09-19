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

import java.util.Map;

public class MyTurnProcessor extends TurnProcessor {

    private static final Logger logger = Logger.getLogger(MyTurnProcessor.class);

    @Override
    public Hand addTile(Hand hand, int x, int y) {
        int gameId = hand.getGameInfo().getGameId();
        logger.info("Start add tile for me method for tile [" + x + "-" + y + "], gameId[" + gameId + "]");
        // ისტორიაში დამატება
        Game game = CachedDominoGames.getGame(gameId);
        game.getHistory().push(CloneUtil.getClone(hand));
        // თუ გავიარე -> ა) უკვე გავლილი აქვს მოწინააღმდეგეს და ვამთავრებთ  ბ) გადაეცა სვლა მოწინააღმდეგეს
        if (hand.getTableInfo().getBazaarTilesCount() == 2) {
            if (TileOwner.HIM.equals(CachedDominoGames.getOmittedValue(hand.getGameInfo().getGameId()))) {
                return DominoHelper.finishedLastAndGetNewHand(hand, true, true);
            } else {
                CachedDominoGames.addOmitted(hand.getGameInfo().getGameId(), TileOwner.ME);
                hand.getTableInfo().setMyTurn(false);
                return hand;
            }
        }
        // აღებული ქვის ალბათობების შეცვლა და ცვლილების სხვებისთვის გადანაწილება
        Map<String, Tile> tiles = hand.getTiles();
        Tile tile = tiles.get(TileUtil.getTileUID(x, y));
        double him = tile.getHim();
        double bazaar = tile.getBazaar();
        makeTileAsMine(tile);
        DominoHelper.addProbabilitiesProportional(tiles, DominoHelper.getNotPlayedMineOrBazaarTiles(tiles), him, TileOwner.HIM);
        DominoHelper.addProbabilitiesProportional(tiles, DominoHelper.getNotPlayedMineOrBazaarTiles(tiles), bazaar - 1, TileOwner.BAZAAR);
        updateTileCountBeforeAddMe(hand);
        // თუ ეხლა დავამთავრე საწყისი 7 ქვის აღება ვითხოვთ რჩევას პროგრამისგან
        if (hand.getTableInfo().getLeft() == null && hand.getTableInfo().getMyTilesCount() == 7) {
            hand.getTableInfo().setMyTurn(game.getGameProperties().isStart());
            if (!game.getGameProperties().isFirstHand() && hand.getTableInfo().isMyTurn()) {
                AIPrediction aiPrediction = MinMax.minMax(CloneUtil.getClone(hand));
                hand.setAiPrediction(aiPrediction);
            }
        }
        // თუ უკვე ჩამოსულია ქვა, ვითხოვთ რჩევას პროგრამისგან
        if (hand.getTableInfo().getLeft() != null) {
            AIPrediction aiPrediction = MinMax.minMax(CloneUtil.getClone(hand));
            hand.setAiPrediction(aiPrediction);
        }

        logger.info("Added tile for me, gameId[" + gameId + "]");
        DominoLoggingProcessor.logTilesFullInfo(hand);
        return hand;
    }

    @Override
    public Hand play(Hand hand, int x, int y, PlayDirection direction) {
        int gameId = hand.getGameInfo().getGameId();
        logger.info("Start play for me method for tile [" + x + "-" + y + "] direction [" + direction.name() + "], gameId[" + gameId + "]");
        // ისტორიაში დამატება
        Game game = CachedDominoGames.getGame(gameId);
        game.getHistory().push(CloneUtil.getClone(hand));
        // თუ პირველი ხელის პირველი ჩამოსვლაა, ვაანალიზებთ არ ჩამოსულ მაღალ წყვილებს
        if (game.getGameProperties().isFirstHand() && hand.getTableInfo().getLeft() == null) {
            makeDoubleTilesAsInBazaar(hand, (x == y ? x : -1));
        }
        // ქვის ჩამოსვლა
        DominoHelper.makeTileAsPlayed(hand.getTiles().get(TileUtil.getTileUID(x, y)));
        DominoHelper.playTile(hand.getTableInfo(), x, y, direction);
        DominoHelper.updateTileCountBeforePlayMe(hand);
        hand.getGameInfo().setMyPoints(hand.getGameInfo().getMyPoints() + DominoHelper.countScore(hand));
        hand.getTableInfo().setMyTurn(false);
        // თუ ქვები აღარ მაქვს ვამთავრებთ
        if (hand.getTableInfo().getMyTilesCount() == 0) {
            return DominoHelper.finishedLastAndGetNewHand(hand, true, true);
        }

        logger.info("Played tile for me, gameId[" + gameId + "]");
        DominoLoggingProcessor.logTilesFullInfo(hand);
        return hand;
    }

    private void makeTileAsMine(Tile tile) {
        tile.setMe(1.0);
        tile.setHim(0.0);
        tile.setBazaar(0.0);
    }

    private void updateTileCountBeforeAddMe(Hand hand) {
        TableInfo tableInfo = hand.getTableInfo();
        tableInfo.setMyTilesCount(tableInfo.getMyTilesCount() + 1);
        tableInfo.setBazaarTilesCount(tableInfo.getBazaarTilesCount() - 1);
    }
}
