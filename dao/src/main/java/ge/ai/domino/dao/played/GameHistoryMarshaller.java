package ge.ai.domino.dao.played;

import ge.ai.domino.domain.played.GameHistory;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;

class GameHistoryMarshaller {

    private static final Logger logger = Logger.getLogger(GameHistoryMarshaller.class);

    private static final JAXBContext JAXB_CONTEXT;

    static String getMarshalledHistory(GameHistory history) {
        StringWriter stringWriter = new StringWriter();
        try (BufferedWriter bufferedWriter = new BufferedWriter(stringWriter)) {
            getMarshaller().marshal(history, bufferedWriter);
        } catch (Exception ex) {
            logger.error("Unable to marshall game history", ex);
        }
        return stringWriter.toString();
    }

    static GameHistory unmarshallGameHistory(String object) {
        StringReader stringReader = new StringReader(object);

        try (BufferedReader bufferedReader = new BufferedReader(stringReader)) {
            return (GameHistory) createUnmarshaller().unmarshal(bufferedReader);
        } catch (Exception ex) {
            logger.error("Unable to unmarshall game history", ex);
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
            JAXB_CONTEXT = JAXBContext.newInstance(GameHistory.class);
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
