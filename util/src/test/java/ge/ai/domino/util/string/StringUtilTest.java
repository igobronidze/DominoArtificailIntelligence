package ge.ai.domino.util.string;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StringUtilTest {

    private static final String EMPTY_0 = null;

    private static final String EMPTY_1 = "";

    private static final String NOT_EMPTY = "test";

    private static final List<Integer> INTEGER_LIST = new ArrayList<>();

    @BeforeClass
    public static void init() {
        INTEGER_LIST.add(3);
        INTEGER_LIST.add(1);
        INTEGER_LIST.add(16);
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(StringUtil.isEmpty(EMPTY_0));
        Assert.assertTrue(StringUtil.isEmpty(EMPTY_1));
        Assert.assertFalse(StringUtil.isEmpty(NOT_EMPTY));
    }

    @Test
    public void testConcatAndReverse() {
        String text = StringUtil.concatIntegerList(INTEGER_LIST);
        Assert.assertEquals(INTEGER_LIST, StringUtil.getIntegerListFromString(text));
    }
}
