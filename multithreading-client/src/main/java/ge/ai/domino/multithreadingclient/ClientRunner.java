package ge.ai.domino.multithreadingclient;

import org.apache.log4j.Logger;

public class ClientRunner {

    private static final Logger logger = Logger.getLogger(ClientManager.class);

    // java -jar domino.jar localhost 8080 name
    public static void main(String[] args) throws Exception {
        Client client = new Client();

        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            String name = args[2];
            client.startClient(host, port, name);
        } catch (Exception ex) {
            logger.error("Error occurred while run multithreading client app", ex);
            client.startClient();
        }
    }
}
