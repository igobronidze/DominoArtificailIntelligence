package ge.ai.domino.console.debug.operation.move;

import ge.ai.domino.console.debug.GameDebuggerHelper;
import ge.ai.domino.console.debug.operation.GameDebuggerOperation;
import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.move.MoveDirection;
import ge.ai.domino.manager.game.move.PlayForMeProcessor;
import ge.ai.domino.manager.game.move.PlayForMeProcessorVirtual;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class PlayForMeOperation implements GameDebuggerOperation {

	private static final Logger logger = Logger.getLogger(PlayForMeOperation.class);

	@Override
	public void process(Scanner scanner) throws DAIException {
		logger.info("Left:");
		int left = Integer.parseInt(scanner.nextLine());
		logger.info("Right:");
		int right = Integer.parseInt(scanner.nextLine());
		logger.info("Direction:");
		MoveDirection direction = MoveDirection.valueOf(scanner.nextLine());
		logger.info("Virtual:");
		boolean virtual = Boolean.parseBoolean(scanner.nextLine());
		if (virtual) {
			PlayForMeProcessorVirtual playForMeProcessor = new PlayForMeProcessorVirtual();
			GameDebuggerHelper.round = playForMeProcessor.move(GameDebuggerHelper.round, new Move(left, right, direction));
		} else {
			PlayForMeProcessor playForMeProcessor = new PlayForMeProcessor();
			GameDebuggerHelper.round = playForMeProcessor.move(GameDebuggerHelper.round, new Move(left, right, direction));
		}
		logger.info("Played for me successfully");
	}
}
