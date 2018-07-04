package ge.ai.domino.dao.query;

public class QueryUtil {

	private static final String SPACE = " ";

	public static void addFilter(StringBuilder sql, String columnName, String value, FilterCondition filterCondition, boolean isString) {
		sql.append("AND ")
				.append(columnName)
				.append(SPACE)
				.append(filterCondition.getValue())
				.append(SPACE)
				.append(getFormattedValue(value, isString, filterCondition == FilterCondition.LIKE))
				.append(SPACE);
	}

	public static void addParameter(StringBuilder sql, String parameter, boolean withComma) {
		if (withComma) {
			sql.append(", ");
		}
		sql.append(parameter);
	}

	private static String getFormattedValue(String value, boolean isString, boolean isLike) {
		String result;
		if (isLike) {
			result = "%" + value + "%";
		} else {
			result = value;
		}
		if (isString) {
			return "'" + result + "'";
		}
		return result;
	}
}
