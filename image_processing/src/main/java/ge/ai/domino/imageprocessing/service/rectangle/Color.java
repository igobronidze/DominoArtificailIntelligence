package ge.ai.domino.imageprocessing.service.rectangle;

import lombok.Getter;

@Getter
public class Color {

    private int redFrom;
    private int redTo;

    private int greenFrom;
    private int greenTo;

    private int blueFrom;
    private int blueTo;

    public Color redFrom(int redFrom) {
        this.redFrom = redFrom;
        return this;
    }

    public Color redTo(int redTo) {
        this.redTo = redTo;
        return this;
    }

    public Color greenFrom(int greenFrom) {
        this.greenFrom = greenFrom;
        return this;
    }

    public Color greenTo(int greenTo) {
        this.greenTo = greenTo;
        return this;
    }

    public Color blueFrom(int blueFrom) {
        this.blueFrom = blueFrom;
        return this;
    }

    public Color blueTo(int blueTo) {
        this.blueTo = blueTo;
        return this;
    }
}
