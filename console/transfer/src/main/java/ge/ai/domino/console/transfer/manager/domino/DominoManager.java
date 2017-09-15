package ge.ai.domino.console.transfer.manager.domino;

import ge.ai.domino.console.transfer.dto.domino.GamePropertiesDTO;
import ge.ai.domino.console.transfer.dto.domino.HandDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayDirectionDTO;

public interface DominoManager {

    HandDTO startGame(GamePropertiesDTO gameProperties);

    HandDTO addTileForMe(HandDTO hand, int x, int y);

    HandDTO addTileForHim(HandDTO hand);

    HandDTO playForMe(HandDTO hand, int x, int y, PlayDirectionDTO direction);

    HandDTO playForHim(HandDTO hand, int x, int y, PlayDirectionDTO direction);
}
