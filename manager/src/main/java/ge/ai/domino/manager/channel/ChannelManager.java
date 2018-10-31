package ge.ai.domino.manager.channel;

import ge.ai.domino.dao.channel.ChannelDAO;
import ge.ai.domino.dao.channel.ChannelDAOImpl;
import ge.ai.domino.domain.channel.Channel;

import java.util.List;

public class ChannelManager {

	private static final ChannelDAO channelDAO = new ChannelDAOImpl();

	public Channel getChannelByName(String name) {
		List<Channel> channels = channelDAO.getChannels();
		for (Channel channel : channels) {
			if (channel.getName().equals(name)) {
				return channel;
			}
		}
		return null;
	}
}
