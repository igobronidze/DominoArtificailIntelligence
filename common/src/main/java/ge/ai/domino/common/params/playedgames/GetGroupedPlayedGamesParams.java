package ge.ai.domino.common.params.playedgames;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetGroupedPlayedGamesParams {

    private String version;

    private Integer channelId;

    private Integer pointForWin;

    private Double level;

    private LocalDate fromDate;

    private LocalDate toDate;

    private boolean groupByVersion;

    private boolean groupByChannel;

    private boolean groupedByPointForWin;

    private boolean groupByLevel;

    private boolean groupByDate;
}
