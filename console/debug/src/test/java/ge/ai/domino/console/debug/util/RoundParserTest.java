package ge.ai.domino.console.debug.util;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.RoundBlockingInfo;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.manager.game.logging.RoundLogger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoundParserTest {

    private static final int gameId = 1;

    private static final boolean finished = false;

    private static final int myPoint = 15;

    private static final int opponentPoint = 25;

    private static final PlayedTile left = new PlayedTile(3, true, true, true);

    private static final PlayedTile right = new PlayedTile(2, true, false, true);

    private static final PlayedTile top = new PlayedTile(3, false, true, false);

    private static final PlayedTile bottom = new PlayedTile(6, false, false, false);

    private static final boolean myMove = false;

    private static final boolean withCenter = true;

    private static final boolean firstRound = true;

    private static final boolean omittedMe = false;

    private static final boolean omittedOpponent = true;

    private static final boolean lastNotTwinTileIsMine = false;

    private static final double opponentTilesCount = 8.0;

    private static final double bazaarTilesCount = 11.0;

    private static final int tilesFromBazaar = 0;

    private static final List<Tile> myTiles = new ArrayList<Tile>() {{
        add(new Tile(3, 2));
        add(new Tile(1, 1));
        add(new Tile(4, 0));
        add(new Tile(2, 0));
    }};

    private static final Map<Tile, Double> opponentTiles = new HashMap<Tile, Double>() {{
        put(new Tile(6, 1), 0.0667);
        put(new Tile(6, 2), 0.0667);
        put(new Tile(6, 4), 0.0667);
        put(new Tile(5, 0), 0.0667);
        put(new Tile(5, 1), 0.0667);
        put(new Tile(5, 2), 0.0667);
        put(new Tile(5, 3), 0.0667);
        put(new Tile(5, 4), 0.0667);
        put(new Tile(4, 2), 1.0);
        put(new Tile(4, 3), 0.0667);
        put(new Tile(4, 4), 1.0);
        put(new Tile(3, 0), 0.0667);
        put(new Tile(3, 1), 0.0667);
        put(new Tile(3, 3), 0.0667);
        put(new Tile(2, 1), 1.0);
        put(new Tile(2, 2), 1.0);
        put(new Tile(1, 0), 0.0667);
        put(new Tile(0, 0), 0.0667);
    }};

    @Test
    public void testParseRound() throws DAIException {
        Round mockRound = getMockRound();

        String log = RoundLogger.getRoundFullInfo(mockRound);

        Round roundFromLog = RoundParser.parseRound(log);

        assertEquals(mockRound.getGameInfo(), roundFromLog.getGameInfo());
        assertEquals(mockRound.getTableInfo(), roundFromLog.getTableInfo());
        assertEquals(mockRound.getMyTiles(), roundFromLog.getMyTiles());
        assertEquals(mockRound.getOpponentTiles(), roundFromLog.getOpponentTiles());
    }

    private void assertEquals(Map<Tile, Double> expected, Map<Tile, Double> real) {
        Assert.assertEquals(expected.size(), real.size());
        for (Tile tile : expected.keySet()) {
            Assert.assertTrue(real.containsKey(tile));
            Assert.assertEquals(expected.get(tile), real.get(tile));
        }
    }

    private void assertEquals(Set<Tile> expected, Set<Tile> real) {
        Assert.assertEquals(expected.size(), real.size());
        for (Tile tile : expected) {
            Assert.assertTrue(real.contains(tile));
        }
    }

    private void assertEquals(TableInfo expected, TableInfo real) {
        assertEquals(expected.getLeft(), real.getLeft());
        assertEquals(expected.getRight(), real.getRight());
        assertEquals(expected.getTop(), real.getTop());
        assertEquals(expected.getBottom(), real.getBottom());

        Assert.assertEquals(expected.getBazaarTilesCount(), real.getBazaarTilesCount(), 0.0);
        Assert.assertEquals(expected.getOpponentTilesCount(), real.getOpponentTilesCount(), 0.0);
        Assert.assertEquals(expected.getTilesFromBazaar(), real.getTilesFromBazaar());
        assertEquals(expected.getRoundBlockingInfo(), real.getRoundBlockingInfo());
        Assert.assertEquals(expected.isFirstRound(), real.isFirstRound());
        Assert.assertEquals(expected.isMyMove(), real.isMyMove());
        Assert.assertEquals(expected.isWithCenter(), real.isWithCenter());
    }

    private void assertEquals(PlayedTile expected, PlayedTile real) {
        Assert.assertEquals(expected.getOpenSide(), real.getOpenSide());
        Assert.assertEquals(expected.isCenter(), real.isCenter());
        Assert.assertEquals(expected.isConsiderInSum(), real.isConsiderInSum());
        Assert.assertEquals(expected.isTwin(), real.isTwin());
    }

    private void assertEquals(RoundBlockingInfo expected, RoundBlockingInfo real) {
        Assert.assertEquals(expected.isLastNotTwinPlayedTileMy(), real.isLastNotTwinPlayedTileMy());
        Assert.assertEquals(expected.isOmitMe(), real.isOmitMe());
        Assert.assertEquals(expected.isOmitOpponent(), real.isOmitOpponent());
    }

    private void assertEquals(GameInfo expected, GameInfo real) {
        Assert.assertEquals(expected.getGameId(), real.getGameId());
        Assert.assertEquals(expected.getMyPoint(), real.getMyPoint());
        Assert.assertEquals(expected.getOpponentPoint(), real.getOpponentPoint());
    }

    private Round getMockRound() {
        Round round = new Round();
        round.setGameInfo(getMockGameInfo());
        round.setTableInfo(getMockTableInfo());
        round.setMyTiles(new HashSet<>(myTiles));
        round.setOpponentTiles(opponentTiles);
        return round;
    }

    private GameInfo getMockGameInfo() {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setGameId(gameId);
        gameInfo.setFinished(finished);
        gameInfo.setMyPoint(myPoint);
        gameInfo.setOpponentPoint(opponentPoint);
        return gameInfo;
    }

    private TableInfo getMockTableInfo() {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setMyMove(myMove);
        tableInfo.setBazaarTilesCount(bazaarTilesCount);
        tableInfo.setBottom(bottom);
        tableInfo.setTop(top);
        tableInfo.setLeft(left);
        tableInfo.setRight(right);
        tableInfo.setTilesFromBazaar(tilesFromBazaar);
        tableInfo.setFirstRound(firstRound);
        tableInfo.setOpponentTilesCount(opponentTilesCount);
        tableInfo.setWithCenter(withCenter);
        tableInfo.setRoundBlockingInfo(getMockRoundBlockingInfo());
        return tableInfo;
    }

    private RoundBlockingInfo getMockRoundBlockingInfo() {
        RoundBlockingInfo roundBlockingInfo = new RoundBlockingInfo();
        roundBlockingInfo.setLastNotTwinPlayedTileMy(lastNotTwinTileIsMine);
        roundBlockingInfo.setOmitOpponent(omittedOpponent);
        roundBlockingInfo.setOmitMe(omittedMe);
        return roundBlockingInfo;
    }
}
