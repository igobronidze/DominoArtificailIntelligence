package ge.ai.domino.console.ui.util;

import javafx.scene.image.Image;

public class ImageFactory {

    public static Image getImage(String name) {
        return new Image("static/images/" + name);
    }
}