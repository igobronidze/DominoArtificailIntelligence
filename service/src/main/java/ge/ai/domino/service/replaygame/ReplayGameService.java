package ge.ai.domino.service.replaygame;

import ge.ai.domino.domain.exception.DAIException;
import ge.ai.domino.domain.played.ReplayMoveInfo;

public interface ReplayGameService {

	ReplayMoveInfo startReplayGame(int gameId) throws DAIException;

	ReplayMoveInfo replayMove(int gameId, int moveIndex) throws DAIException;
}
