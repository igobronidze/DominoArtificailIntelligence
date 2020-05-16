package ge.ai.domino.util.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DAIPropertiesUtil {

    private static final String daiPropertyPath = "../dai/properties/dai.properties";

    private static Properties properties;

    public static File daiPropertiesFile = new File(daiPropertyPath);

    public static String getProperty(String key) throws IOException {
        if (properties == null) {
            init();
        }
        return properties.getProperty(key);
    }

    private static void init() throws IOException {
        properties = new Properties();
        try (FileInputStream in = new FileInputStream(daiPropertiesFile)) {
            properties.load(in);
        }
    }
}

