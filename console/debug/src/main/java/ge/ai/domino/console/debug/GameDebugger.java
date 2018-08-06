package ge.ai.domino.console.debug;

import ge.ai.domino.console.debug.util.RoundParser;
import ge.ai.domino.domain.game.Game;
import ge.ai.domino.domain.game.GameProperties;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.caching.game.CachedGames;
import ge.ai.domino.manager.game.logging.RoundLogger;
import ge.ai.domino.manager.game.move.AddForMeProcessor;
import ge.ai.domino.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.manager.game.move.PlayForOpponentProcessor;
import ge.ai.domino.manager.heuristic.HeuristicManager;
import ge.ai.domino.manager.sysparam.SystemParameterManager;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Scanner;

public class GameDebugger {

    private static final Logger logger = Logger.getLogger(GameDebugger.class);

    private static final String LOG_END = "END";

    private static final int GAME_ID = -1;

    private static final SystemParameterManager sysParamManager = new SystemParameterManager();

    private static final HeuristicManager heuristicManager = new HeuristicManager();

    private static Round round;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("0. exit");
            System.out.println("1. Cache game");
            System.out.println("2. Parse round(please type " + LOG_END + " for end)");
            System.out.println("3. Add tile for me");
            System.out.println("4. Add tile fot opponent");
            System.out.println("5. Play for me");
            System.out.println("6. Play for opponent");
            System.out.println("7. Change sys param(only in this session)");
            System.out.println("8. Get heuristics");
            String line = scanner.nextLine();

            try {
                switch (line) {
                    case "0" :
                        return;
                    case "1" : {
                        GameProperties gameProperties = new GameProperties();
                        System.out.println("Opponent name:");
                        gameProperties.setOpponentName(scanner.nextLine());
                        System.out.println("WebSite:");
                        gameProperties.setWebsite(scanner.nextLine());
                        System.out.println("Point for win:");
                        gameProperties.setPointsForWin(Integer.parseInt(scanner.nextLine()));
                        Game game = new Game();
                        game.setId(GAME_ID);
                        game.setProperties(gameProperties);
                        CachedGames.addGame(game);
                        logger.info("Game cached successfully");
                        break;
                    }
                    case "2" : {
                        String s;
                        StringBuilder log = new StringBuilder();
                        while (!(s = scanner.nextLine()).equals(LOG_END)) {
                            log.append(s).append(RoundLogger.END_LINE);
                        }
                        round = RoundParser.parseRound(log.toString());
                        round.getGameInfo().setGameId(GAME_ID);
                        logger.info("Round cached successfully");
                        break;
                    }
                    case "3" : {
                        System.out.println("Left:");
                        int left = Integer.parseInt(scanner.nextLine());
                        System.out.println("Right:");
                        int right = Integer.parseInt(scanner.nextLine());
                        System.out.println("Virtual:");
                        boolean virtual = Boolean.valueOf(scanner.nextLine());
                        AddForMeProcessor addForMeProcessor = new AddForMeProcessor();
                        round = addForMeProcessor.move(round, new Move(left, right, null));
                        logger.info("Added for me successfully");
                        break;
                    }
                    case "4" : {
                        System.out.println("Virtual:");
                        boolean virtual = Boolean.valueOf(scanner.nextLine());
                        AddForOpponentProcessor addForOpponentProcessor = new AddForOpponentProcessor();
                        round = addForOpponentProcessor.move(round, new Move(0, 0, null));
                        logger.info("Added for opponent successfully");
                        break;
                    }
                    case "5" : {
                        System.out.println("Left:");
                        int left = Integer.parseInt(scanner.nextLine());
                        System.out.println("Right:");
                        int right = Integer.parseInt(scanner.nextLine());
                        System.out.println("Direction:");
                        MoveDirection direction = MoveDirection.valueOf(scanner.nextLine());
                        System.out.println("Virtual:");
                        boolean virtual = Boolean.valueOf(scanner.nextLine());
                        PlayForMeProcessor playForMeProcessor = new PlayForMeProcessor();
                        round = playForMeProcessor.move(round, new Move(left, right, direction));
                        logger.info("Played fot me successfully");
                        break;
                    }
                    case "6" : {
                        System.out.println("Left:");
                        int left = Integer.parseInt(scanner.nextLine());
                        System.out.println("Right:");
                        int right = Integer.parseInt(scanner.nextLine());
                        System.out.println("Direction:");
                        MoveDirection direction = MoveDirection.valueOf(scanner.nextLine());
                        System.out.println("Virtual:");
                        boolean virtual = Boolean.valueOf(scanner.nextLine());
                        PlayForOpponentProcessor playForOpponentProcessor = new PlayForOpponentProcessor();
                        round = playForOpponentProcessor.move(round, new Move(left, right, direction));
                        logger.info("Played fot opponent successfully");
                        break;
                    }
                    case "7" : {
                        System.out.println("key:");
                        String key = scanner.nextLine();
                        System.out.println("value:");
                        String value = scanner.nextLine();
                        sysParamManager.changeParameterOnlyInCache(key, value);
                        logger.info("Sys param changed successfully");
                        break;
                    }
                    case "8" : {
                        Map<String, Double> result = heuristicManager.getHeuristics(round);
                        for (Map.Entry<String, Double> entry : result.entrySet()) {
                            System.out.println(entry.getKey() + ": " + entry.getValue());
                        }
                        logger.info("Heuristics counted successfully");
                        break;
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
