package ge.ai.domino.dao.client;

import ge.ai.domino.dao.connection.ConnectionUtil;
import ge.ai.domino.dao.helper.StringMapMarshaller;
import ge.ai.domino.domain.client.Client;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAOImpl implements ClientDAO {

	private final Logger logger = Logger.getLogger(ClientDAOImpl.class);

	private static final String CLIENT_TABLE_NAME = "client";

	private static final String ID_COLUMN_NAME = "id";

	private static final String NAME_COLUMN_NAME = "name";

	private static final String PARAMS_COLUMN_NAME = "params";

	private PreparedStatement pstmt;

	@Override
	public void addClient(Client client) {
		try {
			logger.info("Start addClient method");
			String sql = String.format("INSERT INTO %s (%s, %s) VALUES (?,?);",
					CLIENT_TABLE_NAME, NAME_COLUMN_NAME, PARAMS_COLUMN_NAME);
			pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
			pstmt.setString(1, client.getName());
			pstmt.setString(2, StringMapMarshaller.marshallMap(client.getParams()));
			pstmt.executeUpdate();

			logger.info("Added client name [" + client.getName() + "]");
		} catch (SQLException ex) {
			logger.error("Error occurred while add client", ex);
		} finally {
			ConnectionUtil.closeConnection();
		}
	}

	@Override
	public void editClient(Client client) {
		try {
			logger.info("Start editClient method id[" + client.getId() + "]");
			String sql = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ?",
					CLIENT_TABLE_NAME, NAME_COLUMN_NAME, PARAMS_COLUMN_NAME, ID_COLUMN_NAME);
			pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
			pstmt.setString(1, client.getName());
			pstmt.setString(2, StringMapMarshaller.marshallMap(client.getParams()));
			pstmt.setInt(3, client.getId());
			pstmt.executeUpdate();
			logger.info("Updated client id[" + client.getId() + "]");
		} catch (SQLException ex) {
			logger.error("Error occurred while edit client", ex);
		} finally {
			ConnectionUtil.closeConnection();
		}
	}

	@Override
	public List<Client> getClients() {
		List<Client> clients = new ArrayList<>();
		try {
			String sql = String.format("SELECT %s, %s, %s FROM %s", ID_COLUMN_NAME, NAME_COLUMN_NAME, PARAMS_COLUMN_NAME, CLIENT_TABLE_NAME);

			pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Client client = new Client();
				client.setId(rs.getInt(ID_COLUMN_NAME));
				client.setName(rs.getString(NAME_COLUMN_NAME));
				client.setParams(StringMapMarshaller.unmarshallMap(rs.getString(PARAMS_COLUMN_NAME)));
				clients.add(client);
			}
		} catch (SQLException ex) {
			logger.error("Error occurred while getting clients", ex);
		} finally {
			ConnectionUtil.closeConnection();
		}
		return clients;
	}

	@Override
	public Client getClientById(int id) {
		Client client = null;

		try {
			String sql = String.format("SELECT %s, %s, %s FROM %s WHERE %s = ?", ID_COLUMN_NAME, NAME_COLUMN_NAME, PARAMS_COLUMN_NAME, CLIENT_TABLE_NAME, ID_COLUMN_NAME);

			pstmt = ConnectionUtil.getConnection().prepareStatement(sql);
			pstmt.setInt(1, id);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				client = new Client();
				client.setId(rs.getInt(ID_COLUMN_NAME));
				client.setName(rs.getString(NAME_COLUMN_NAME));
				client.setParams(StringMapMarshaller.unmarshallMap(rs.getString(PARAMS_COLUMN_NAME)));

				return client;
			}
		} catch (SQLException ex) {
			logger.error("Error occurred while get client by id - " + id, ex);
		} finally {
			ConnectionUtil.closeConnection();
		}
		return client;
	}
}
