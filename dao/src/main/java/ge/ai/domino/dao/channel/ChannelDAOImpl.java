package ge.ai.domino.dao.channel;

import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.domain.channel.Channel;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChannelDAOImpl implements ChannelDAO {

	private final Logger logger = Logger.getLogger(ChannelDAOImpl.class);

	private static final String CHANNEL_TABLE_NAME = "channel";

	private static final String ID_COLUMN_NAME = "id";

	private static final String NAME_COLUMN_NAME = "name";

	private static final String PARAMS_COLUMN_NAME = "params";

	private PreparedStatement pstmt;

	@Override
	public void addChannel(Channel channel) {
		try {
			logger.info("Start addChannel method");
			String sql = String.format("INSERT INTO %s (%s, %s) VALUES (?,?);",
					CHANNEL_TABLE_NAME, NAME_COLUMN_NAME, PARAMS_COLUMN_NAME);
			pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
			pstmt.setString(1, channel.getName());
			pstmt.setString(2, ChannelParamsMarshaller.marshallChannelParams(channel.getParams()));
			pstmt.executeUpdate();

			logger.info("Added channel name [" + channel.getName() + "]");
		} catch (SQLException ex) {
			logger.error("Error occurred while add channel", ex);
		} finally {
			ConnectionUtil.closeConnection();
		}
	}

	@Override
	public void editChannel(Channel channel) {
		try {
			logger.info("Start editChannel method id[" + channel.getId() + "]");
			String sql = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ?",
					CHANNEL_TABLE_NAME, NAME_COLUMN_NAME, PARAMS_COLUMN_NAME, ID_COLUMN_NAME);
			pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
			pstmt.setString(1, channel.getName());
			pstmt.setString(2, ChannelParamsMarshaller.marshallChannelParams(channel.getParams()));
			pstmt.setInt(3, channel.getId());
			pstmt.executeUpdate();
			logger.info("Updated channel id[" + channel.getId() + "]");
		} catch (SQLException ex) {
			logger.error("Error occurred while edit channel", ex);
		} finally {
			ConnectionUtil.closeConnection();
		}
	}

	@Override
	public List<Channel> getChannels() {
		List<Channel> channels = new ArrayList<>();
		try {
			String sql = String.format("SELECT %s, %s, %s FROM %s", ID_COLUMN_NAME, NAME_COLUMN_NAME, PARAMS_COLUMN_NAME, CHANNEL_TABLE_NAME);

			pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Channel channel = new Channel();
				channel.setId(rs.getInt(ID_COLUMN_NAME));
				channel.setName(rs.getString(NAME_COLUMN_NAME));
				channel.setParams(ChannelParamsMarshaller.unmarshallChannelParams(rs.getString(PARAMS_COLUMN_NAME)));
				channels.add(channel);
			}
		} catch (SQLException ex) {
			logger.error("Error occurred while getting game games", ex);
		} finally {
			ConnectionUtil.closeConnection();
		}
		return channels;
	}
}
