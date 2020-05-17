package ge.ai.domino.imageprocessing.contour;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Contour {

    private int top = Integer.MAX_VALUE;

    private int right = -1;

    private int bottom = -1;

    private int left = Integer.MAX_VALUE;

    private Contour parent;

    private List<Contour> children = new ArrayList<>();
}
