package legion.data;

import java.util.List;

import legion.IntegrationService;
import legion.system.SysAttr;

public interface SystemDataService extends IntegrationService {
	
	boolean testCallback();
	
	// -------------------------------------------------------------------------------
	// ------------------------------------SysAttr------------------------------------
	boolean saveSysAttr(SysAttr _sysAttr);

	boolean deleteSysAttr(String _uid);

	SysAttr loadSysAttr(String _uid);

	List<SysAttr> loadSysAttrList();
}
