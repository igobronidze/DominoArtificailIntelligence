package ge.ai.domino.console.ui.util;

import ge.ai.domino.console.transfer.dto.sysparam.SysParamDTO;
import ge.ai.domino.console.transfer.manager.sysparam.SystemParameterManager;
import ge.ai.domino.console.transfer.manager.sysparam.SystemParameterManagerImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Messages {

    private static SystemParameterManager systemParameterManager = new SystemParameterManagerImpl();

    private static SysParamDTO systemLanguageCode = new SysParamDTO("systemLanguageCode", "KA");

    private static Map<String, Properties> messages = new HashMap<>();

    private static String languageCode;

    private static String get(String key, String langCode) {
        if (key != null) {
            Properties properties = messages.get(langCode);
            if (properties == null) {
                properties = new Properties();
                try {
                    InputStream is = Messages.class.getResourceAsStream("/messages_" + langCode + ".properties");
                    Reader bufferedReader = new InputStreamReader(is, "UTF-8");
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
            languageCode = systemParameterManager.getStringParameterValue(systemLanguageCode);
        }
        return get(key, languageCode);
    }

    public static void setLanguageCode(String code) {
        languageCode = code;
        messages = new HashMap<>();
    }
}
