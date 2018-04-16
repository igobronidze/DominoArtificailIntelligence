package ge.ai.domino.server.manager.util;

import ge.ai.domino.server.manager.played.PlayedGameManager;
import org.apache.log4j.Logger;

import java.util.Properties;

public class ProjectVersionUtil {

    private static final Logger logger = Logger.getLogger(ProjectVersionUtil.class);

    public static String getVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(PlayedGameManager.class.getResourceAsStream("/project.properties"));
            return properties.getProperty("version");
        } catch (Exception ex) {
            logger.error("Error occurred while reading project.properties", ex);
            return "";
        }
    }
}
