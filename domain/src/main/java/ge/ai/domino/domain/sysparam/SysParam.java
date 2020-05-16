package ge.ai.domino.domain.sysparam;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SysParam {

    private String key;

    private String defaultValue;
}
