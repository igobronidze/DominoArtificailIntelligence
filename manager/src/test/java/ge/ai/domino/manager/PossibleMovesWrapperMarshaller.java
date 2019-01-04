package ge.ai.domino.manager;

import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;

class PossibleMovesWrapperMarshaller {

    private static final Logger logger = Logger.getLogger(PossibleMovesWrapperMarshaller.class);

    private static final JAXBContext JAXB_CONTEXT;

    static void marshall(PossibleMovesWrapper possibleMovesWrapper, BufferedWriter bufferedWriter) {
        try {
            getMarshaller().marshal(possibleMovesWrapper, bufferedWriter);
        } catch (Exception ex) {
            logger.error("Unable to marshall possible moves", ex);
        }
    }

    static PossibleMovesWrapper unmarshall(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return (PossibleMovesWrapper) createUnmarshaller().unmarshal(bufferedReader);
        } catch (Exception ex) {
            logger.error("Unable to unmarshall possible moves", ex);
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
            JAXB_CONTEXT = JAXBContext.newInstance(PossibleMovesWrapper.class);
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
