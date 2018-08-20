package ge.ai.domino.service.replaygame;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.played.ReplayMoveInfo;
import ge.ai.domino.manager.replaygame.ReplayGameManager;

public class ReplayGameServiceImpl implements ReplayGameService {

	private final ReplayGameManager replayGameManager = new ReplayGameManager();

	@Override
	public ReplayMoveInfo startReplayGame(int gameId) throws DAIException {
		return replayGameManager.startReplayGame(gameId);
	}

	@Override
	public ReplayMoveInfo replayMove(int gameId, int moveIndex) throws DAIException {
		return  replayGameManager.replayMove(gameId, moveIndex);
	}
}
