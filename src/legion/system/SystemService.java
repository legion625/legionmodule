package legion.system;

import java.util.List;

import legion.BusinessService;

public interface SystemService extends BusinessService{
	
	// -------------------------------------------------------------------------------
	// ------------------------------------SysAttr------------------------------------
	boolean saveSysAttr(SysAttr _sysAttr);
	
	List<SysAttr> loadSysAttrList(); 
}
