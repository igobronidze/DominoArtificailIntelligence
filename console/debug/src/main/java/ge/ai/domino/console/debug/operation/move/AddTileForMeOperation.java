package ge.ai.domino.console.debug.operation.move;

import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.manager.game.move.AddForMeProcessor;
import ge.ai.domino.manager.game.move.AddForMeProcessorVirtual;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class AddTileForMeOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(AddTileForMeOperation.class);

	@Override
	public void process(Scanner scanner) throws DAIException {
		logger.info("Left:");
		int left = Integer.parseInt(scanner.nextLine());
		logger.info("Right:");
		int right = Integer.parseInt(scanner.nextLine());
		logger.info("Virtual:");
		boolean virtual = Boolean.valueOf(scanner.nextLine());
		if (virtual) {
			AddForMeProcessorVirtual addForMeProcessor = new AddForMeProcessorVirtual();
			GameDebuggerHelper.round = addForMeProcessor.move(GameDebuggerHelper.round, new Move(left, right, null));
		} else {
			AddForMeProcessor addForMeProcessor = new AddForMeProcessor();
			GameDebuggerHelper.round = addForMeProcessor.move(GameDebuggerHelper.round, new Move(left, right, null));
		}
		logger.info("Added for me successfully");
	}
}
