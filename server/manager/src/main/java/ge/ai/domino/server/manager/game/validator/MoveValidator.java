package ge.ai.domino.server.manager.game.validator;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.game.Round;
import ge.ai.domino.domain.game.TableInfo;
import ge.ai.domino.domain.move.Move;
import ge.ai.domino.domain.played.PlayedTile;
import org.apache.log4j.Logger;

public class MoveValidator {

	private static final Logger logger = Logger.getLogger(MoveValidator.class);

	public static void validateMove(Round round, Move move) throws DAIException {
		PlayedTile left = round.getTableInfo().getLeft();
		PlayedTile right = round.getTableInfo().getRight();
		PlayedTile top = round.getTableInfo().getTop();
		PlayedTile bottom = round.getTableInfo().getBottom();
		TableInfo tableInfo = round.getTableInfo();
		if (left == null && right == null && top == null && bottom == null) {
			return;
		}
		switch (move.getDirection()) {
			case LEFT:
				if (left == null || (left.getOpenSide() != move.getLeft() && left.getOpenSide() != move.getRight())) {
					logger.warn("Incorrect move[" + move +"]");
					throw new DAIException("incorrectMove");
				}
				break;
			case RIGHT:
				if (right == null || (right.getOpenSide() != move.getLeft() && right.getOpenSide() != move.getRight())) {
					logger.warn("Incorrect move[" + move +"]");
					throw new DAIException("incorrectMove");
				}
				break;
			case TOP:
				if (top == null || ((top.getOpenSide() != move.getLeft() && top.getOpenSide() != move.getRight()) || tableInfo.getLeft().isCenter() || tableInfo.getRight().isCenter())) {
					logger.warn("Incorrect move[" + move +"]");
					throw new DAIException("incorrectMove");
				}
				break;
			case BOTTOM:
				if (bottom == null || ((bottom.getOpenSide() != move.getLeft() && bottom.getOpenSide() != move.getRight()) || tableInfo.getLeft().isCenter() || tableInfo.getRight().isCenter())) {
					logger.warn("Incorrect move[" + move +"]");
					throw new DAIException("incorrectMove");
				}
				break;
		}
	}
}
