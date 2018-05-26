package ge.ai.domino.console.debug.util;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.GameInfo;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.RoundBlockingInfo;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.game.Tile;
import ge.ai.domino.domain.played.PlayedTile;
import ge.ai.domino.server.manager.game.logging.GameLoggingProcessor;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class RoundParser {

    private static final Logger logger = Logger.getLogger(RoundParser.class);

    /**
     * Parse round from log
     *
     * Log Example
     * __________Game Info__________
     Game ID: 121     |     Finished: false     |     My Point: 15     |     Opponent Point: 0
     __________Table Info__________
     Left: 3     |     Right: 5     |     Top: 0     |     Bottom: 6
     My Move: false     |     With Center: true     |     First Round: true
     Omitted Me: false     |     Omitted Opponent: false     |     Last Played Not Twin Tile Is Mine: true
     Opponent Tiles: 8.0     |     Bazaar Tiles: 11.0     |     Tiles From Bazaar: 0
     __________My Tiles__________
     3-2     |     1-1     |     4-0     |     4-1     |     2-0
     __________Opponent Tiles__________
     6-0: N          |     5-0: 0.0667     |     4-0: N          |     3-0: 0.0667     |     2-0: N          |     1-0: 0.0667     |     0-0: 0.0667
     6-1: 0.0667     |     5-1: 0.0667     |     4-1: N          |     3-1: 0.0667     |     2-1: 1.0000     |     1-1: N
     6-2: 0.0667     |     5-2: 0.0667     |     4-2: 1.0000     |     3-2: N          |     2-2: 1.0000
     6-3: N          |     5-3: 0.0667     |     4-3: 0.0667     |     3-3: 0.0667
     6-4: 0.0667     |     5-4: 0.0667     |     4-4: 1.0000
     6-5: N          |     5-5: 0.0667
     6-6: N
     *
     * @param roundLog Round log
     * @return Parsed round
     */
    public static Round parseRound(String roundLog) throws DAIException {
        String[] lines = roundLog.split(Pattern.quote(GameLoggingProcessor.END_LINE));

        Round round = new Round();
        round.setGameInfo(parseGameInfo(lines[1]));
        round.setTableInfo(parseTableInfo(Arrays.copyOfRange(lines, 3, 7)));
        round.setMyTiles(parseMyTiles(lines[8]));
        round.setOpponentTiles(parseOpponentTiles(Arrays.copyOfRange(lines, 10, 17)));
        return round;
    }

    private static Map<Tile, Double> parseOpponentTiles(String[] lines) {
        Map<Tile, Double> opponentTiles = new HashMap<>();
        for (String line : lines) {
            String[] opponentTilesString = line.split(Pattern.quote(GameLoggingProcessor.DELIMITER));
            for (String opponentTileString : opponentTilesString) {
                String[] tileAndProb = opponentTileString.trim().split(GameLoggingProcessor.EQUAL_CHARACTER);
                String tile = tileAndProb[0].trim();
                String prob = tileAndProb[1].trim();
                if (!prob.equals(GameLoggingProcessor.NOT)) {
                    opponentTiles.put(parseTile(tile), Double.valueOf(prob));
                }
            }
        }
        return opponentTiles;
    }

    private static Set<Tile> parseMyTiles(String line) {
        String[] tileStrings = line.split(Pattern.quote(GameLoggingProcessor.DELIMITER));
        Set<Tile> myTiles = new HashSet<>();
        for (String tileString : tileStrings) {
            myTiles.add(parseTile(tileString));
        }
        return myTiles;
    }

    private static Tile parseTile(String tileString) {
        String[] elements = tileString.split(Pattern.quote(Tile.DELIMITER));
        return new Tile(Integer.parseInt(elements[0].trim()), Integer.parseInt(elements[1].trim()));
    }

    private static TableInfo parseTableInfo(String[] lines) {
        TableInfo tableInfo = new TableInfo();

        String[] properties0 = lines[0].split(Pattern.quote(GameLoggingProcessor.DELIMITER));
        tableInfo.setLeft(new PlayedTile(Integer.parseInt(properties0[0].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim())));
        tableInfo.setRight(new PlayedTile(Integer.parseInt(properties0[1].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim())));
        tableInfo.setTop(new PlayedTile(Integer.parseInt(properties0[2].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim())));
        tableInfo.setBottom(new PlayedTile(Integer.parseInt(properties0[3].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim())));

        String[] properties1 = lines[1].split(Pattern.quote(GameLoggingProcessor.DELIMITER));
        tableInfo.setMyMove(Boolean.valueOf(properties1[0].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
        tableInfo.setWithCenter(Boolean.valueOf(properties1[1].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
        tableInfo.setFirstRound(Boolean.valueOf(properties1[2].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));

        String[] properties2 = lines[2].split(Pattern.quote(GameLoggingProcessor.DELIMITER));
        RoundBlockingInfo roundBlockingInfo = new RoundBlockingInfo();
        roundBlockingInfo.setOmitMe(Boolean.valueOf(properties2[0].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
        roundBlockingInfo.setOmitOpponent(Boolean.valueOf(properties2[1].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
        roundBlockingInfo.setLastNotTwinPlayedTileMy(Boolean.valueOf(properties2[2].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
        tableInfo.setRoundBlockingInfo(roundBlockingInfo);

        String[] properties3 = lines[3].split(Pattern.quote(GameLoggingProcessor.DELIMITER));
        tableInfo.setOpponentTilesCount(Double.valueOf(properties3[0].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
        tableInfo.setBazaarTilesCount(Double.valueOf(properties3[1].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
        tableInfo.setTilesFromBazaar(Integer.valueOf(properties3[2].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));

        return tableInfo;
    }

    private static GameInfo parseGameInfo(String line) throws DAIException {
        try {
            String[] properties = line.split(Pattern.quote(GameLoggingProcessor.DELIMITER));

            GameInfo gameInfo = new GameInfo();
            gameInfo.setGameId(Integer.parseInt(properties[0].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
            gameInfo.setFinished(Boolean.parseBoolean(properties[1].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
            gameInfo.setMyPoint(Integer.parseInt(properties[2].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
            gameInfo.setOpponentPoint(Integer.parseInt(properties[3].split(Pattern.quote(GameLoggingProcessor.EQUAL_CHARACTER))[1].trim()));
            return gameInfo;
        } catch (Exception ex) {
            logger.error("Can't parse Game Info from log", ex);
            throw new DAIException(ex.getMessage());
        }
    }
}