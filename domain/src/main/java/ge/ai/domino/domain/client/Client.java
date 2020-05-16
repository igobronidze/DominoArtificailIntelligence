package ge.ai.domino.domain.client;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Client {

    private int id;

    private String name;

    private Map<String, String> params = new HashMap<>();
}
