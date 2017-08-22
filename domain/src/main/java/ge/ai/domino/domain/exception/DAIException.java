package ge.ai.domino.domain.exception;

public class DAIException extends Exception {

    private String messageKey;

    private Exception exc;

    public DAIException() {
    }

    public DAIException(String messageKey) {
        this.messageKey = messageKey;
    }

    public DAIException(String messageKey, Exception exc) {
        this.messageKey = messageKey;
        this.exc = exc;
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
