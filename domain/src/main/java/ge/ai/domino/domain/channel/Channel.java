package ge.ai.domino.domain.channel;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Channel implements Serializable {

	private int id;

	private String name;

	private Map<String, String> params = new HashMap<>();
}
