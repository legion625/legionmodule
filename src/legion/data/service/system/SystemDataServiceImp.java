package legion.data.service.system;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.data.SystemDataService;
import legion.system.SysAttr;

public class SystemDataServiceImp implements SystemDataService {
	private Logger log = LoggerFactory.getLogger(SystemDataServiceImp.class);
	
	private String source;
	
	// dao
	private SysAttrDao sysAttrDao;
	
	@Override
	public void register(Map<String, String> _params) {
		log.debug("SystemDataServiceImp register...");
		if (_params == null || _params.isEmpty())
			return;

		source = _params.get("source");

		// dao
		sysAttrDao = new SysAttrDao(source);
		log.debug("sysAttrDao: {}", sysAttrDao);

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean testCallback() {
		
		return sysAttrDao.testCallback();
	}
	
	// -------------------------------------------------------------------------------
	// ------------------------------------SysAttr------------------------------------
	@Override
	public boolean saveSysAttr(SysAttr _sysAttr) {
		return sysAttrDao.saveSysAttr(_sysAttr);
	}

	@Override
	public boolean deleteSysAttr(String _uid) {
		return sysAttrDao.deleteSysAttr(_uid);
	}

	@Override
	public SysAttr loadSysAttr(String _uid) {
		return sysAttrDao.loadSysAttr(_uid);
	}

	@Override
	public List<SysAttr> loadSysAttrList() {
		return sysAttrDao.loadSysAttrList();
	}

}
