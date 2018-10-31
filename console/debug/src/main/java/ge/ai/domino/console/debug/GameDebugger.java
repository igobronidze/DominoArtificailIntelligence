package ge.ai.domino.console.debug;

import ge.ai.domino.console.debug.operation.game.CachGameOperation;
import ge.ai.domino.console.debug.operation.game.ChangeSysParamOperation;
import ge.ai.domino.console.debug.operation.game.ParseRoundOperation;
import ge.ai.domino.console.debug.operation.game.ReplayGameOperation;
import ge.ai.domino.console.debug.operation.heuristic.CountHeuristicOperation;
import ge.ai.domino.console.debug.operation.heuristic.HeuristicOptimizationOperation;
import ge.ai.domino.console.debug.operation.minmax.MinMaxPredictionOptimizationOperation;
import ge.ai.domino.console.debug.operation.minmax.MinMaxSolveOperation;
import ge.ai.domino.console.debug.operation.minmax.SendGameToMultithreadingClientOperation;
import ge.ai.domino.console.debug.operation.minmax.StartMultithreadingServerOperation;
import ge.ai.domino.console.debug.operation.move.AddTileForMeOperation;
import ge.ai.domino.console.debug.operation.move.AddTileForOpponentOperation;
import ge.ai.domino.console.debug.operation.move.PlayForMeOperation;
import ge.ai.domino.console.debug.operation.move.PlayForOpponentOperation;
import ge.ai.domino.domain.exception.DAIException;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class GameDebugger {

    private static final Logger logger = Logger.getLogger(GameDebugger.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            logger.info("0. exit");
            logger.info("1. Cache game");
            logger.info("2. Parse round(please type " + GameDebuggerHelper.LOG_END + " for end)");
            logger.info("3. Add tile for me");
            logger.info("4. Add tile fot opponent");
            logger.info("5. Play for me");
            logger.info("6. Play for opponent");
            logger.info("7. Change sys param(only in this session)");
            logger.info("8. Get heuristics");
            logger.info("9. Replay games");
            logger.info("10. MinMaxPredictor Optimization");
            logger.info("11. Start MinMax Multithreading server");
            logger.info("12. Execute MinMax solve");
            logger.info("13. Send game to multithreading client");
            logger.info("14. Heuristic Optimization");
            String line = scanner.nextLine();

            try {
                switch (line) {
                    case "0":
                        return;
                    case "1": {
                        new CachGameOperation().process(scanner);
                        break;
                    }
                    case "2": {
                        new ParseRoundOperation().process(scanner);
                        break;
                    }
                    case "3": {
                        new AddTileForMeOperation().process(scanner);
                        break;
                    }
                    case "4": {
                        new AddTileForOpponentOperation().process(scanner);
                        break;
                    }
                    case "5": {
                        new PlayForMeOperation().process(scanner);
                        break;
                    }
                    case "6": {
                        new PlayForOpponentOperation().process(scanner);
                        break;
                    }
                    case "7": {
                        new ChangeSysParamOperation().process(scanner);
                        break;
                    }
                    case "8": {
                        new CountHeuristicOperation().process(scanner);
                        break;
                    }
                    case "9": {
                        new ReplayGameOperation().process(scanner);
                        break;
                    }
                    case "10": {
                        new MinMaxPredictionOptimizationOperation().process(scanner);
                        break;
                    }
                    case "11": {
                        new StartMultithreadingServerOperation().process(scanner);
                        break;
                    }
                    case "12": {
                        new MinMaxSolveOperation().process(scanner);
                        break;
                    }
                    case "13": {
                        new SendGameToMultithreadingClientOperation().process(scanner);
                        break;
                    }
                    case "14": {
                        new HeuristicOptimizationOperation().process(scanner);
                        break;
                    }
                }
            } catch (DAIException ex) {
                logger.error("Error occurred while execute game debugger", ex.getExc());
            } catch (Exception ex) {
                logger.error("Error occurred while execute game debugger", ex);
            }
        }
    }
}
