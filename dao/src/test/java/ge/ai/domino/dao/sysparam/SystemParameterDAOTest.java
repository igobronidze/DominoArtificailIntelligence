package ge.ai.domino.dao.sysparam;

import ge.ai.domino.dao.DAOTestUtil;
import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.domain.sysparam.SystemParameter;
import ge.ai.domino.domain.sysparam.SystemParameterType;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class SystemParameterDAOTest {

	private static final String SYSTEM_PARAMETER_TABLE_NAME = "system_parameter";

	private static final String KEY_1 = "testKey";

	private static final String VALUE_1 = "testValue";

	private static final SystemParameterType TYPE_1 = SystemParameterType.CONSOLE_PARAMETER;

	private static final String KEY_2 = "otherTestKey";

	private static final String VALUE_2 = "otherTestValue";

	private static final SystemParameterType TYPE_2 = SystemParameterType.CONSOLE_PARAMETER;

	private static SystemParameterDAO systemParameterDAO;

	@BeforeClass
	public static void init() {
		systemParameterDAO = new SystemParameterDAOImpl();
		DAOTestUtil.initDAIPropertiesFilePath();
	}

	@After
	public void cleanUp() throws Exception {
		String sql = String.format("DELETE FROM %s", SYSTEM_PARAMETER_TABLE_NAME);
		PreparedStatement pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
		pstmt.executeUpdate();
	}

	@Test
	public void testAddAndGetSystemParameter() throws Exception {
		List<SystemParameter> systemParameters = getSystemParameterMock();

		for (SystemParameter systemParameter : systemParameters) {
			systemParameterDAO.addSystemParameter(systemParameter);
		}

		List<SystemParameter> result1 = systemParameterDAO.getSystemParameters(null, null);
		Assert.assertEquals(systemParameters.size(), result1.size());
		for (int i = 0; i < systemParameters.size(); i++) {
			equalSysParam(systemParameters.get(i), result1.get(i));
		}

		List<SystemParameter> result2 = systemParameterDAO.getSystemParameters("other", SystemParameterType.CONSOLE_PARAMETER);
		Assert.assertEquals(1, result2.size());
		equalSysParam(systemParameters.get(1), result2.get(0));
	}

	@Test
	public void testDeleteSystemParameter() throws Exception {
		List<SystemParameter> systemParameters = getSystemParameterMock();

		for (SystemParameter systemParameter : systemParameters) {
			systemParameterDAO.addSystemParameter(systemParameter);
		}

		for (SystemParameter systemParameter : systemParameters) {
			systemParameterDAO.deleteSystemParameter(systemParameter.getKey());
		}

		Assert.assertTrue(systemParameterDAO.getSystemParameters(null, null).isEmpty());
	}

	@Test
	public void testEditSystemParameter() throws Exception {
		List<SystemParameter> systemParameters = getSystemParameterMock();
		SystemParameter systemParameter = systemParameters.get(0);
		systemParameterDAO.addSystemParameter(systemParameter);

		systemParameter.setValue(VALUE_2);
		systemParameterDAO.editSystemParameter(systemParameter);

		Assert.assertEquals(systemParameter.getValue(), systemParameterDAO.getSystemParameters(null, null).get(0).getValue());
	}

	private List<SystemParameter> getSystemParameterMock() {
		List<SystemParameter> systemParameters = new ArrayList<>();
		systemParameters.add(new SystemParameter(0, KEY_1, VALUE_1, TYPE_1));
		systemParameters.add(new SystemParameter(0, KEY_2, VALUE_2, TYPE_2));
		return systemParameters;
	}

	private void equalSysParam(SystemParameter expected, SystemParameter real) {
		Assert.assertEquals(expected.getKey(), real.getKey());
		Assert.assertEquals(expected.getValue(), real.getValue());
		Assert.assertEquals(expected.getType(), real.getType());
	}
}
