package ge.ai.domino.console.transfer.dto.exception;

import ge.ai.domino.domain.exception.DAIException;

public class DAIConsoleException extends Exception {

    private String messageKey;

    private Exception exc;

    public DAIConsoleException(DAIException exception) {
        this.messageKey = exception.getMessageKey();
        this.exc = exception.getExc();
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public Exception getExc() {
        return exc;
    }

    public void setExc(Exception exc) {
        this.exc = exc;
    }
}
