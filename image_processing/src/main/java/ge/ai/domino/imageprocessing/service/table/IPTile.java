package ge.ai.domino.imageprocessing.service.table;

import ge.ai.domino.imageprocessing.service.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IPTile {

    private int left;

    private int right;

    private Point topLeft;

    private Point bottomRight;
}
