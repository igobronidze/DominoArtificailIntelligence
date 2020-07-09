package ge.ai.domino.domain.game;

import ge.ai.domino.domain.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class GameProperties implements Serializable {

    private String opponentName;

    private Channel channel;

    private int pointsForWin;

    private double level;
}
