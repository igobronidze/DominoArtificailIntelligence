package ge.ai.domino.service.client;

import ge.ai.domino.domain.client.Client;
import ge.ai.domino.domain.exception.DAIException;

import java.util.List;

public interface ClientService {

    void addClient(Client client) throws DAIException;

    void editClient(Client client) throws DAIException;

    List<Client> getClients() throws DAIException;
}
