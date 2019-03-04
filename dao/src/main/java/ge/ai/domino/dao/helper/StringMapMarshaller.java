package ge.ai.domino.dao.helper;

import ge.ai.domino.util.string.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class StringMapMarshaller {

	private static final String DELIMITER = "##";

	private static final String EQUAL = "=";

	public static String marshallMap(Map<String, String> params) {
		if (params.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append(EQUAL).append(entry.getValue()).append(DELIMITER);
		}

		String result = sb.toString();
		return result.substring(0, result.length() - 2);
	}

	public static Map<String, String> unmarshallMap(String content) {
		Map<String, String> params = new HashMap<>();

		if (!StringUtil.isEmpty(content)) {
			for (String param : content.split(DELIMITER)) {
				String key = param.split(EQUAL)[0];
				String value = param.split(EQUAL)[1];
				params.put(key, value);
			}
		}

		return params;
	}
}
