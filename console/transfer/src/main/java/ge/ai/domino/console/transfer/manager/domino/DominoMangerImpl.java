package ge.ai.domino.console.transfer.manager.domino;

import ge.ai.domino.console.transfer.dto.domino.GameDTO;
import ge.ai.domino.console.transfer.dto.domino.GamePropertiesDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayDirectionDTO;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.util.domino.DominoService;
import ge.ai.domino.util.domino.DominoServiceImpl;

public class DominoMangerImpl implements DominoManager {

    private static final DominoService dominoService = new DominoServiceImpl();

    @Override
    public GameDTO startGame(GamePropertiesDTO gameProperties) {
        return GameDTO.toGameDTO(dominoService.startGame(GamePropertiesDTO.toGameProperties(gameProperties)));
    }

    @Override
    public GameDTO addTileForMe(GameDTO game, int x, int y) {
        return GameDTO.toGameDTO(dominoService.addTileForMe(GameDTO.toGame(game), x, y));
    }

    @Override
    public GameDTO addTileForHim(GameDTO game) {
        return GameDTO.toGameDTO(dominoService.addTileForHim(GameDTO.toGame(game)));
    }

    @Override
    public GameDTO playForMe(GameDTO game, int x, int y, PlayDirectionDTO direction) {
        return GameDTO.toGameDTO(dominoService.playForMe(GameDTO.toGame(game), x, y, PlayDirection.valueOf(direction.name())));
    }

    @Override
    public GameDTO playForHim(GameDTO game, int x, int y, PlayDirectionDTO direction) {
        return GameDTO.toGameDTO(dominoService.playForHim(GameDTO.toGame(game), x, y, PlayDirection.valueOf(direction.name())));
    }
}
