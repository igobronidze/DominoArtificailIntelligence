package ge.ai.domino.multiprocessorclient;

import org.apache.log4j.Logger;

public class MultiProcessorClientRunner {

    private static final Logger logger = Logger.getLogger(MultiProcessorClientManager.class);

    // java -jar domino.jar localhost 8080 id
    public static void main(String[] args) throws Exception {
        MultiProcessorClient client = new MultiProcessorClient();

        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            Integer clientId = Integer.parseInt(args[2]);
            client.startClient(host, port, clientId);
        } catch (Exception ex) {
            logger.error("Error occurred while run Multi Processor Client app", ex);
            client.startClient();
        }
    }
}
