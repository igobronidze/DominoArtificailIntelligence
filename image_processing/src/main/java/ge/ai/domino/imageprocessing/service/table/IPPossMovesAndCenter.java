package ge.ai.domino.imageprocessing.service.table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IPPossMovesAndCenter {

    private List<IPRectangle> possMoves;

    private IPRectangle center;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPPossMovesAndCenter that = (IPPossMovesAndCenter) o;
        return Objects.equals(possMoves, that.possMoves) && Objects.equals(center, that.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possMoves, center);
    }
}
