package ge.ai.domino.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DAIException extends Exception {

    private String messageKey;

    private Exception exc;

    public DAIException(String messageKey) {
        this.messageKey = messageKey;
    }
}
