package ge.ai.domino.server.dao.opponentplay;

import ge.ai.domino.domain.game.opponentplay.OpponentPlay;

import java.util.List;

public interface OpponentPlayDAO {

    void addOpponentPlays(List<OpponentPlay> opponentPlays);

    List<OpponentPlay> getOpponentPlays(String version, Integer gameId);
}
