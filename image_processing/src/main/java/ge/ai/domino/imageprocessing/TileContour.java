package ge.ai.domino.imageprocessing;

import ge.ai.domino.domain.game.Tile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TileContour {

    private Tile tile;

    private double topLeftX;

    private double topLeftY;

    private double bottomRightX;

    private double bottomRightY;

    public TileContour(Tile tile) {
        this.tile = tile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TileContour that = (TileContour) o;
        return Double.compare(that.topLeftX, topLeftX) == 0 && Double.compare(that.topLeftY, topLeftY) == 0 && Double.compare(that.bottomRightX, bottomRightX) == 0 && Double.compare(that.bottomRightY, bottomRightY) == 0 && Objects.equals(tile, that.tile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tile, topLeftX, topLeftY, bottomRightX, bottomRightY);
    }
}
