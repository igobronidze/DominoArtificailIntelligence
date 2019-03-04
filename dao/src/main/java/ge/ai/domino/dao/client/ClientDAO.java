package ge.ai.domino.dao.client;

import ge.ai.domino.domain.client.Client;

import java.util.List;

public interface ClientDAO {

	void addClient(Client client);

	void editClient(Client client);

	List<Client> getClients();

	Client getClientById(int id);
}
