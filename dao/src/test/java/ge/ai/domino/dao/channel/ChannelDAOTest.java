package ge.ai.domino.dao.channel;

import ge.ai.domino.dao.DAOTestUtil;
import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.domain.channel.Channel;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelDAOTest {

	private static final String CHANNEL_TABLE_NAME = "channel";

	private static final String NAME = "testName";

	private static final String KEY_1 = "testKey";

	private static final String VALUE_1 = "testValue";

	private static final String KEY_2 = "otherTestKey";

	private static final String VALUE_2 = "otherTestValue";

	private static ChannelDAO channelDAO;

	@BeforeClass
	public static void init() {
		channelDAO = new ChannelDAOImpl();
		DAOTestUtil.initDAIPropertiesFilePath();
	}

	@After
	public void cleanUp() throws Exception {
		String sql = String.format("DELETE FROM %s", CHANNEL_TABLE_NAME);
		PreparedStatement pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
		pstmt.executeUpdate();
	}

	@Test
	public void testAddChannel() {
		Channel channel = new Channel();
		channel.setName(NAME);
		channelDAO.addChannel(channel);

		List<Channel> channels = channelDAO.getChannels();
		Assert.assertEquals(1, channels.size());

		Assert.assertEquals(NAME, channels.get(0).getName());
	}

	@Test
	public void testEditChannel() {
		Channel channel = new Channel();
		channel.setName(NAME);
		channelDAO.addChannel(channel);

		Channel channelFromDB = channelDAO.getChannels().get(0);
		channelFromDB.setParams(getTestParams());
		channelDAO.editChannel(channelFromDB);

		channelFromDB = channelDAO.getChannels().get(0);
		Assert.assertEquals(getTestParams(), channelFromDB.getParams());
	}

	private Map<String, String> getTestParams() {
		Map<String, String> params = new HashMap<>();
		params.put(KEY_1, VALUE_1);
		params.put(KEY_2, VALUE_2);
		return params;
	}
}
