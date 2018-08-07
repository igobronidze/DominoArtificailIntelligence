package ge.ai.domino.dao.channel;

import ge.ai.domino.domain.channel.Channel;

import java.util.List;

public interface ChannelDAO {

	void addChannel(Channel channel);

	void editChannel(Channel channel);

	List<Channel> getChannels();
}
