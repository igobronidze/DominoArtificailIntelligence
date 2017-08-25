package ge.ai.domino.console.transfer.manager.domino;

import ge.ai.domino.console.transfer.dto.domino.GameDTO;
import ge.ai.domino.console.transfer.dto.domino.GamePropertiesDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayDirectionDTO;

public interface DominoManager {

    GameDTO startGame(GamePropertiesDTO gameProperties);

    GameDTO addTileForMe(GameDTO game, int x, int y);

    GameDTO addTileForHim(GameDTO game);

    GameDTO playForMe(GameDTO game, int x, int y, PlayDirectionDTO direction);

    GameDTO playForHim(GameDTO game, int x, int y, PlayDirectionDTO direction);
}
