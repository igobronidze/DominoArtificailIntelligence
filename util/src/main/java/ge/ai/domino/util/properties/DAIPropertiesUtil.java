package ge.ai.domino.util.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DAIPropertiesUtil {

    protected static String daiPropertyPath = "properties/dai.properties";

    private static Properties properties;

    public static String getProperty(String key) {
        if (properties == null) {
            init();
        }
        return properties.getProperty(key);
    }

    private static void init() {
        properties = new Properties();
        File file = new File(daiPropertyPath);

        try (FileInputStream in = new FileInputStream(daiPropertyPath)) {
            properties.load(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

