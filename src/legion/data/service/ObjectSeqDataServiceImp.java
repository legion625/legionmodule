package legion.data.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.logging.Log;

import legion.data.ObjectSeqDataService;

public class ObjectSeqDataServiceImp implements ObjectSeqDataService{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private String source;
	private String serverId;
	
	private GenSeqDao genSeqDao;
	
	// -------------------------------------------------------------------------------
	@Override
	public void register(Map<String, String> _params) {
		if (_params == null || _params.isEmpty())
			return;
		source = _params.get("source");
		serverId = _params.get("serverId");
		log.debug("source: {}\t serverId:{}", source, serverId);
		
		genSeqDao = new GenSeqDao(source, serverId);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
	// -------------------------------------------------------------------------------
	@Override
	public synchronized String getSeq(String _itemId) {
		return genSeqDao.getSeq(_itemId);
	}

	@Override
	public synchronized String getSimpleSeq(String _itemId) {
		return genSeqDao.getSimpleSeq(_itemId);
	}

}
