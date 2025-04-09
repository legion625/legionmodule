package legion;

import java.util.Map;

public interface IntegrationService {
	void register(Map<String, String> _params);
	void destroy();
}
