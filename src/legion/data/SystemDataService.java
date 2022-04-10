package legion.data;

import legion.IntegrationService;
import legion.system.SysAttr;

public interface SystemDataService extends IntegrationService {
	
	// -------------------------------------------------------------------------------
	// ------------------------------------SysAttr------------------------------------
	boolean saveSysAttr(SysAttr _sysAttr);
	boolean deleteSysAttr(SysAttr _sysAttr);
	SysAttr loadSysAttrByUid(String _uid);
}
