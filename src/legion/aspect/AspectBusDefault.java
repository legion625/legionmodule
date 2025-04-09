package legion.aspect;

import java.util.HashMap;
import java.util.Map;

public class AspectBusDefault implements AspectBus {
	private Map map = new HashMap<>();

	@Override
	public void addParam(String _key, Object _obj) {
		map.put(_key, _obj);
	}

	@Override
	public Object getParam(String _key) {
		return map.get(_key);
	}

}
