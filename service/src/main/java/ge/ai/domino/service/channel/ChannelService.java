package ge.ai.domino.service.channel;

import ge.ai.domino.domain.channel.Channel;

import java.util.List;

public interface ChannelService {

    void addChannel(Channel channel);

    void editChannel(Channel channel);

    List<Channel> getChannels();
}
