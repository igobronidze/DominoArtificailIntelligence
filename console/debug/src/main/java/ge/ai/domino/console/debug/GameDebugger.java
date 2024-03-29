package ge.ai.domino.console.debug;

import ge.ai.domino.console.debug.operation.db.PlayedGameInsertOperation;
import ge.ai.domino.console.debug.operation.game.CacheGameOperation;
import ge.ai.domino.console.debug.operation.game.ParseRoundOperation;
import ge.ai.domino.console.debug.operation.game.ReplayGameOperation;
import ge.ai.domino.console.debug.operation.game.ReplayGameWithDifferenceModeOperation;
import ge.ai.domino.console.debug.operation.heuristic.CountHeuristicOperation;
import ge.ai.domino.console.debug.operation.heuristic.HeuristicOptimizationOperation;
import ge.ai.domino.console.debug.operation.minmax.MinMaxSolveOperation;
import ge.ai.domino.console.debug.operation.minmax.OpponentTilesPredictionOptimizationOperation;
import ge.ai.domino.console.debug.operation.minmax.SendGameToMultiProcessorClientOperation;
import ge.ai.domino.console.debug.operation.minmax.StartMultiProcessorServerOperation;
import ge.ai.domino.console.debug.operation.move.AddTileForMeOperation;
import ge.ai.domino.console.debug.operation.move.AddTileForOpponentOperation;
import ge.ai.domino.console.debug.operation.move.PlayForMeOperation;
import ge.ai.domino.console.debug.operation.move.PlayForOpponentOperation;
import ge.ai.domino.console.debug.operation.sysparam.ChangeSysParamOperation;
import ge.ai.domino.console.debug.operation.sysparam.SysParamsUpdateOperation;
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
            logger.info("10. OpponentTilesPredictor Optimization");
            logger.info("11. Start MinMax MultiProcessor server");
            logger.info("12. Execute MinMax solve");
            logger.info("13. Send game to multiProcessor client");
            logger.info("14. Heuristic Optimization");
            logger.info("15. Replay games with difference modes");
            logger.info("16. Update sys params from properties file");
            logger.info("17. Insert played games");
            String line = scanner.nextLine();

            try {
                switch (line) {
                    case "0":
                        return;
                    case "1": {
                        new CacheGameOperation().process(scanner);
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
                        new OpponentTilesPredictionOptimizationOperation().process(scanner);
                        break;
                    }
                    case "11": {
                        new StartMultiProcessorServerOperation().process(scanner);
                        break;
                    }
                    case "12": {
                        new MinMaxSolveOperation().process(scanner);
                        break;
                    }
                    case "13": {
                        new SendGameToMultiProcessorClientOperation().process(scanner);
                        break;
                    }
                    case "14": {
                        new HeuristicOptimizationOperation().process(scanner);
                        break;
                    }
                    case "15": {
                        new ReplayGameWithDifferenceModeOperation().process(scanner);
                        break;
                    }
                    case "16": {
                        new SysParamsUpdateOperation().process(scanner);
                        break;
                    }
                    case "17": {
                        new PlayedGameInsertOperation().process(scanner);
                        break;
                    }
                }
            } catch (DAIException ex) {
                logger.error(String.format("Error occurred while execute game debugger, %s ", ex.getMessageKey()), ex.getExc());
            } catch (Exception ex) {
                logger.error("Error occurred while execute game debugger", ex);
            }
        }
    }
}
