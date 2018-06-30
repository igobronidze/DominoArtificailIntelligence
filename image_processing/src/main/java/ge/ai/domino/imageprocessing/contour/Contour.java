package ge.ai.domino.imageprocessing.contour;

import java.util.ArrayList;
import java.util.List;

public class Contour {

    private int top = Integer.MAX_VALUE;

    private int right = -1;

    private int bottom = -1;

    private int left = Integer.MAX_VALUE;

    private Contour parent;

    private List<Contour> children = new ArrayList<>();

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public Contour getParent() {
        return parent;
    }

    public void setParent(Contour parent) {
        this.parent = parent;
    }

    public List<Contour> getChildren() {
        return children;
    }

    public void setChildren(List<Contour> children) {
        this.children = children;
    }
}
