package ge.ai.domino.dao;

import ge.ai.domino.util.properties.DAIPropertiesUtil;

import java.io.File;

public class DAOTestUtil {

	private static final String TEST_DAI_PROPERTIES_FILE_PATH = "../../properties/dai_test.properties";

	public static void initDAIPropertiesFilePath() {
		DAIPropertiesUtil.daiPropertiesFile = new File(TEST_DAI_PROPERTIES_FILE_PATH);
	}
}
