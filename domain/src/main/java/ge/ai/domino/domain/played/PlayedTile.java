package ge.ai.domino.domain.played;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayedTile implements Serializable {

    private int openSide;

    private boolean twin;

    private boolean considerInSum;

    private boolean center;
}
