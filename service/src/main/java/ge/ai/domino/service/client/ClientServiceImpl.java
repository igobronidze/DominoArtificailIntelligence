package ge.ai.domino.service.client;

import ge.ai.domino.dao.client.ClientDAO;
import ge.ai.domino.dao.client.ClientDAOImpl;
import ge.ai.domino.domain.client.Client;

import java.util.List;

public class ClientServiceImpl implements ClientService {

    private final ClientDAO clientDAO = new ClientDAOImpl();

    @Override
    public void addClient(Client client) {
        clientDAO.addClient(client);
    }

    @Override
    public void editClient(Client client) {
        clientDAO.editClient(client);
    }

    @Override
    public List<Client> getClients() {
        return clientDAO.getClients();
    }
}
