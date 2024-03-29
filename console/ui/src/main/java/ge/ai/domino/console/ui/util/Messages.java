package ge.ai.domino.console.ui.util;

import ge.ai.domino.console.ui.util.service.ServiceExecutor;
import ge.ai.domino.domain.sysparam.SysParam;
import ge.ai.domino.service.sysparam.SystemParameterService;
import ge.ai.domino.service.sysparam.SystemParameterServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Messages {

    private static SystemParameterService systemParameterService = new SystemParameterServiceImpl();

    private static SysParam systemLanguageCode = new SysParam("systemLanguageCode", "ka");

    private static Map<String, Properties> messages = new HashMap<>();

    private static String languageCode;

    private static String get(String key, String langCode) {
        if (key != null) {
            Properties properties = messages.get(langCode);
            if (properties == null) {
                properties = new Properties();
                try {
                    InputStream is = Messages.class.getResourceAsStream("/messages_" + langCode + ".properties");
                    Reader bufferedReader = new InputStreamReader(is, StandardCharsets.UTF_8);
                    properties.load(bufferedReader);
                    messages.put(langCode, properties);
                } catch (IOException ignore) {}
            }
            return properties.getProperty(key, key);
        }
        return "";
    }

    public static String get(String key) {
        if (languageCode == null) {
            messages = new HashMap<>();

            new ServiceExecutor() {}.execute(() -> languageCode = systemParameterService.getStringParameterValue(systemLanguageCode));
        }
        return get(key, languageCode);
    }

    public static void setLanguageCode(String code) {
        languageCode = code;
        messages = new HashMap<>();
    }
}
