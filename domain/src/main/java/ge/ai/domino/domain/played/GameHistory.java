package ge.ai.domino.domain.played;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayDeque;
import java.util.Deque;

@Getter
@Setter
@XmlRootElement(name = "GameHistory")
public class GameHistory {

    private Deque<PlayedMove> playedMoves = new ArrayDeque<>();
}
