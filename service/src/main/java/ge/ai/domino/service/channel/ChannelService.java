package ge.ai.domino.service.channel;

import ge.ai.domino.domain.channel.Channel;
import ge.ai.domino.domain.exception.DAIException;

import java.util.List;

public interface ChannelService {

    void addChannel(Channel channel) throws DAIException;

    void editChannel(Channel channel) throws DAIException;

    List<Channel> getChannels() throws DAIException;
}
