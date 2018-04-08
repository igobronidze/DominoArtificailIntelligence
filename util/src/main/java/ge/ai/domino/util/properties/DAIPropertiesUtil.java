package ge.ai.domino.util.properties;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DAIPropertiesUtil {

    private static final Logger logger = Logger.getLogger(DAIPropertiesUtil.class);

    private static String daiPropertyPath = "../properties/dai.properties";

    private static Properties properties;

    static File daiPropertiesFile = new File(daiPropertyPath);

    public static String getProperty(String key) {
        if (properties == null) {
            init();
        }
        return properties.getProperty(key);
    }

    private static void init() {
        properties = new Properties();
        try (FileInputStream in = new FileInputStream(daiPropertiesFile)) {
            properties.load(in);
        } catch (IOException ex) {
            logger.error("Error occurred while init DAI properties", ex);
        }
    }
}

