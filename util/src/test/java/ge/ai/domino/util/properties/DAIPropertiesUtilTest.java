package ge.ai.domino.util.properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DAIPropertiesUtilTest {

    private static final String PROPERTY_KEY = "testProperty";

    private static final String PROPERTY_VALUE = "test";

    @BeforeClass
    public static void init() {
        DAIPropertiesUtil.daiPropertyPath = "C:/dai/properties/dai.properties";
    }

    @Test
    public void testGetProperty() {
        String value = DAIPropertiesUtil.getProperty(PROPERTY_KEY);
        Assert.assertEquals(PROPERTY_VALUE, value);
    }
}
