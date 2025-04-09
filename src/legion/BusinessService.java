package legion;

import java.util.Map;

public interface BusinessService {
	void register(Map<String, String> _params);
	void destroy();
}
