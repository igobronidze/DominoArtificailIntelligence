package ge.ai.domino.console.ui.util;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImageFactory {

    private static Map<String, Image> cachedImages = new HashMap<>();

    public static Image getImage(String name) {
        if (!cachedImages.containsKey(name)) {
            Image image = new Image("static/images/" + name);
            cachedImages.put(name, image);
        }
        return cachedImages.get(name);
    }
}