package ge.ai.domino.dao.channel;

import java.util.HashMap;
import java.util.Map;

public class ChannelParamsMarshaller {

	private static final String DELIMITER = ",";

	private static final String EQUAL = "=";

	static String marshallChannelParams(Map<String, String> params) {
		if (params.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append(EQUAL).append(entry.getValue()).append(DELIMITER);
		}

		String result = sb.toString();
		return result.substring(0, result.length() - 1);
	}

	static Map<String, String> unmarshallChannelParams(String content) {
		Map<String, String> params = new HashMap<>();

		if (content != null && !content.isEmpty()) {
			for (String param : content.split(DELIMITER)) {
				String key = param.split(EQUAL)[0];
				String value = param.split(EQUAL)[1];
				params.put(key, value);
			}
		}

		return params;
	}
}
