package legion.data;

import legion.IntegrationService;

public interface ObjectSeqDataService extends IntegrationService {
	String getSeq(String _itemId);
	String getSimpleSeq(String _itemId);
}
