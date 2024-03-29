package ge.ai.domino.util.string;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    private static final String DEFAULT_DELIMITER = ",";

    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }

    public static String concatIntegerList(List<Integer> list) {
        return concatIntegerList(list, DEFAULT_DELIMITER);
    }

    public static String concatIntegerList(List<Integer> list, String delimiter) {
        int size = list.size();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            result.append(list.get(i)).append(delimiter);
        }
        if (size != 0) {
            result.append(list.get(size - 1));
        }
        return result.toString();
    }

    public static List<Integer> getIntegerListFromString(String input) {
        return getIntegerListFromString(input, DEFAULT_DELIMITER);
    }

    public static List<Integer> getIntegerListFromString(String input, String delimiter) throws NumberFormatException {
        List<Integer> result = new ArrayList<>();
        for (String part : input.split(delimiter)) {
            part = part.trim();
            if (!isEmpty(part)) {
                result.add(Integer.parseInt(part));
            }
        }
        return result;
    }
}
