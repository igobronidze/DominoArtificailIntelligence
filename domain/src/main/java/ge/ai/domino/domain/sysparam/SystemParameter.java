package ge.ai.domino.domain.sysparam;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class SystemParameter implements Serializable {

    private int id;

    private String key;

    private String value;

    private SystemParameterType type;
}
