package ge.ai.domino.util.properties;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DAIPropertiesUtil {

    private static final Logger logger = Logger.getLogger(DAIPropertiesUtil.class);

    static String daiPropertyPath = "properties/dai.properties";

    private static Properties properties;

    public static String getProperty(String key) {
        if (properties == null) {
            init();
        }
        return properties.getProperty(key);
    }

    private static void init() {
        properties = new Properties();
        try (FileInputStream in = new FileInputStream(daiPropertyPath)) {
            properties.load(in);
        } catch (IOException ex) {
            logger.error("Error occurred while init DAI properties", ex);
        }
    }
}

