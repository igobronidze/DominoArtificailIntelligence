package ge.ai.domino.console.debug.operation.move;

import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.move.AddForOpponentProcessor;
import ge.ai.domino.manager.game.move.AddForOpponentProcessorVirtual;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class AddTileForOpponentOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(AddTileForOpponentOperation.class);

	@Override
	public void process(Scanner scanner) throws DAIException {
		logger.info("Virtual:");
		boolean virtual = Boolean.parseBoolean(scanner.nextLine());
		if (virtual) {
			AddForOpponentProcessorVirtual addForOpponentProcessor = new AddForOpponentProcessorVirtual();
			GameDebuggerHelper.round = addForOpponentProcessor.move(GameDebuggerHelper.round, new Move(0, 0, null));
		} else {
			AddForOpponentProcessor addForOpponentProcessor = new AddForOpponentProcessor();
			GameDebuggerHelper.round = addForOpponentProcessor.move(GameDebuggerHelper.round, new Move(0, 0, null));
		}
		logger.info("Added for opponent successfully");
	}
}
