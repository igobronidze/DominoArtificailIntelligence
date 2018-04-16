package ge.ai.domino.server.dao.opponentplay;

import ge.ai.domino.domain.game.opponentplay.OpponentTile;
import ge.ai.domino.domain.game.opponentplay.OpponentTilesWrapper;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class OpponentTilesMarshaller {

    private static final Logger logger = Logger.getLogger(OpponentTilesMarshaller.class);

    private static final JAXBContext JAXB_CONTEXT;

    public static String getMarshalledTiles(OpponentTilesWrapper opponentTiles) {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
        try {
            getMarshaller().marshal(opponentTiles, bufferedWriter);
        } catch (Exception ex) {
            logger.error("Unable to marshall opponent tiles", ex);
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException ignore) {
            }
        }
        return stringWriter.toString();
    }

    public static OpponentTilesWrapper unmarshallOpponentTiles(String object) {
        StringReader stringReader = new StringReader(object);
        BufferedReader bufferedReader = new BufferedReader(stringReader);

        try {
            return (OpponentTilesWrapper) createUnmarshaller().unmarshal(bufferedReader);
        } catch (Exception ex) {
            logger.error("Unable to unmarshall opponent tiles", ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ignore) { }
        }
        return null;
    }

    private static Marshaller getMarshaller() {
        try {
            return createMarshaller();
        } catch (JAXBException e) {
            logger.error("Unable to create marshaller", e);
        }
        return null;
    }

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(OpponentTilesWrapper.class);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to create JAXB context", ex);
        }
    }

    private static JAXBContext getContext() {
        return JAXB_CONTEXT;
    }

    private static Marshaller createMarshaller() throws JAXBException {
        return getContext().createMarshaller();
    }

    private static Unmarshaller createUnmarshaller() throws JAXBException {
        return getContext().createUnmarshaller();
    }
}
