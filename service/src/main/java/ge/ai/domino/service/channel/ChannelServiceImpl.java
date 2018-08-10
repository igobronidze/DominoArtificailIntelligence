package ge.ai.domino.service.channel;

import ge.ai.domino.dao.channel.ChannelDAO;
import ge.ai.domino.dao.channel.ChannelDAOImpl;
import ge.ai.domino.domain.channel.Channel;

import java.util.List;

public class ChannelServiceImpl implements ChannelService {

    private final ChannelDAO channelDAO = new ChannelDAOImpl();

    @Override
    public void addChannel(Channel channel) {
        channelDAO.addChannel(channel);
    }

    @Override
    public void editChannel(Channel channel) {
        channelDAO.editChannel(channel);
    }

    @Override
    public List<Channel> getChannels() {
        return channelDAO.getChannels();
    }
}
