package ge.ai.domino.console.transfer.manager.domino;

import ge.ai.domino.console.transfer.dto.domino.GamePropertiesDTO;
import ge.ai.domino.console.transfer.dto.domino.HandDTO;
import ge.ai.domino.console.transfer.dto.domino.PlayDirectionDTO;
import ge.ai.domino.domain.domino.PlayDirection;
import ge.ai.domino.util.domino.DominoService;
import ge.ai.domino.util.domino.DominoServiceImpl;

public class DominoMangerImpl implements DominoManager {

    private static final DominoService dominoService = new DominoServiceImpl();

    @Override
    public HandDTO startGame(GamePropertiesDTO gameProperties) {
        return HandDTO.toHandDTO(dominoService.startGame(GamePropertiesDTO.toGameProperties(gameProperties)));
    }

    @Override
    public HandDTO addTileForMe(HandDTO hand, int x, int y) {
        return HandDTO.toHandDTO(dominoService.addTileForMe(HandDTO.toHand(hand), x, y));
    }

    @Override
    public HandDTO addTileForHim(HandDTO hand) {
        return HandDTO.toHandDTO(dominoService.addTileForHim(HandDTO.toHand(hand)));
    }

    @Override
    public HandDTO playForMe(HandDTO hand, int x, int y, PlayDirectionDTO direction) {
        return HandDTO.toHandDTO(dominoService.playForMe(HandDTO.toHand(hand), x, y, PlayDirection.valueOf(direction.name())));
    }

    @Override
    public HandDTO playForHim(HandDTO hand, int x, int y, PlayDirectionDTO direction) {
        return HandDTO.toHandDTO(dominoService.playForHim(HandDTO.toHand(hand), x, y, PlayDirection.valueOf(direction.name())));
    }
}
