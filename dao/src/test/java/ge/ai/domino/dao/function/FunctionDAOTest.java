package ge.ai.domino.dao.function;

import ge.ai.domino.dao.DAOTestUtil;
import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.domain.function.FunctionArgsAndValues;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionDAOTest {

	private static final String ARG_AND_VALUE_TABLE_NAME = "arg_and_value";

	private static final String FUNCTION_NAME_COLUMN_NAME = "function_name";

	private static final String ARG_COLUMN_NAME = "arg";

	private static final String VALUE_COLUMN_NAME = "value";

	private static final String NAME_1 = "testName1";

	private static final List<Double> args_1 = Arrays.asList(3.5, 4.2, 5.4);

	private static final List<Double> values_1 = Arrays.asList(13.5, 4.52, 5.9);

	private static final String NAME_2 = "testName2";

	private static final List<Double> args_2 = Arrays.asList(22.6, 16.8);

	private static final List<Double> values_2 = Arrays.asList(123.5, 6484.0);

	private static final double ASSERT_EQUAL_DELTA = 0.0001;

	private static FunctionDAO functionDAO;

	@BeforeClass
	public static void init() {
		functionDAO = new FunctionDAOImpl();
		DAOTestUtil.initDAIPropertiesFilePath();
	}

	@Test
	public void testGetFunctionArgsAndValues() throws Exception {
		insertData();

		Map<String, FunctionArgsAndValues> expectedData = getMockData();
		Map<String, FunctionArgsAndValues> realData = functionDAO.getFunctionArgsAndValues("test");

		Assert.assertEquals(expectedData.size(), realData.size());
		for (Map.Entry<String, FunctionArgsAndValues> expectedEntry : expectedData.entrySet()) {
			Assert.assertTrue(realData.containsKey(expectedEntry.getKey()));

			FunctionArgsAndValues expected = expectedEntry.getValue();
			FunctionArgsAndValues real = realData.get(expectedEntry.getKey());

			Assert.assertEquals(expected.getName(), real.getName());
			equalDoubleList(expected.getArgs(), real.getArgs());
			equalDoubleList(expected.getValues(), real.getValues());
		}
	}

	@After
	public void cleanUp() throws Exception {
		String sql = String.format("DELETE FROM %s", ARG_AND_VALUE_TABLE_NAME);
		PreparedStatement pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
		pstmt.executeUpdate();
	}

	private void equalDoubleList(List<Double> expected, List<Double> real) {
		Assert.assertEquals(expected.size(), real.size());
		for (int i = 0; i < expected.size(); i++) {
			Assert.assertEquals(expected.get(i), real.get(i), ASSERT_EQUAL_DELTA);
		}
	}

	private void insertData() throws Exception {
		for (Map.Entry<String, FunctionArgsAndValues> entry : getMockData().entrySet()) {
			for (int i = 0 ; i < entry.getValue().getArgs().size(); i++) {
				String sql = String.format("INSERT INTO %s (%s, %s, %s) VALUES ('%s', %s, %s)",
						ARG_AND_VALUE_TABLE_NAME, FUNCTION_NAME_COLUMN_NAME, ARG_COLUMN_NAME, VALUE_COLUMN_NAME,
						entry.getKey(), entry.getValue().getArgs().get(i), entry.getValue().getValues().get(i));
				PreparedStatement pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
				pstmt.executeUpdate();
			}
		}
	}

	private Map<String, FunctionArgsAndValues> getMockData() {
		Map<String, FunctionArgsAndValues> data = new HashMap<>();

		FunctionArgsAndValues functionArgsAndValues1 = new FunctionArgsAndValues(NAME_1);
		functionArgsAndValues1.setArgs(args_1);
		functionArgsAndValues1.setValues(values_1);
		data.put(NAME_1, functionArgsAndValues1);

		FunctionArgsAndValues functionArgsAndValues2 = new FunctionArgsAndValues(NAME_2);
		functionArgsAndValues2.setArgs(args_2);
		functionArgsAndValues2.setValues(values_2);
		data.put(NAME_2, functionArgsAndValues2);

		return data;
	}
}
