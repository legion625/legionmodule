package legion.aspect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AspectBusCache {
	// -------------------------------------------------------------------------------
	private final static AspectBusCache INSTANCE = new AspectBusCache();

	private AspectBusCache() {
		cache = new ConcurrentHashMap<>();
	}

	public final static AspectBusCache getInstance() {
		return INSTANCE;
	}
	
	// -------------------------------------------------------------------------------
	private ConcurrentHashMap<String, AspectBus> cache;
	
	public AspectBus getAspectBus(String _id) {
		return cache.get(_id);
	}
	
	public List<AspectBus> getAspectBusList(){
		List<AspectBus> result = new ArrayList<>(cache.values());
		return result;
	}

	public AspectBus putAspectBus(String _id, AspectBus bus) {
		return cache.put(_id, bus);
	}
	
	public AspectBus removeAspectBus(String _id) {
		return cache.remove(_id);
	}
	
	
}
