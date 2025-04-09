package legion.system;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.DataServiceFactory;
import legion.data.SystemDataService;

public class SystemServiceImp implements SystemService{

	private Logger log = LoggerFactory.getLogger(SystemServiceImp.class);

	private static SystemDataService dataService;

	@Override
	public void register(Map<String, String> _params) {
		log.debug("SystemServiceImp.register");
		dataService = DataServiceFactory.getInstance().getService(SystemDataService.class);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------SysAttr------------------------------------
	@Override
	public SysAttr createSysAttr() {
		return SysAttr.create();
	}
	
	@Override
	public boolean deleteSysAttr(String _uid) {
		return loadSysAttr(_uid).delete();
	}

	@Override
	public boolean saveSysAttr(SysAttr _sysAttr) {
		return _sysAttr.save();
	}
	
	@Override
	public SysAttr loadSysAttr(String _uid) {
		return dataService.loadSysAttr(_uid);
	}

	@Override
	public List<SysAttr> loadSysAttrList() {
		return dataService.loadSysAttrList();
	}

}
