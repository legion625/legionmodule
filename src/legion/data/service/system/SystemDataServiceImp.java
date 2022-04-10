package legion.data.service.system;

import java.util.Map;

import legion.data.SystemDataService;
import legion.system.SysAttr;

public class SystemDataServiceImp implements SystemDataService {
	private String source;
	

	@Override
	public void register(Map<String, String> _params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	// -------------------------------------------------------------------------------
	// ------------------------------------SysAttr------------------------------------
	@Override
	public boolean saveSysAttr(SysAttr _sysAttr) {
		// TODO not implemnted yet...
		return false;
	}

	@Override
	public boolean deleteSysAttr(SysAttr _sysAttr) {
		// TODO not implemnted yet...
		return false;
	}

	@Override
	public SysAttr loadSysAttrByUid(String _uid) {
		// TODO not implemnted yet...
		return null;
	}

}
