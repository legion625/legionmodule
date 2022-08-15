package legion.system;

import java.util.List;

import legion.BusinessService;

public interface SystemService extends BusinessService{
	
	// -------------------------------------------------------------------------------
	// ------------------------------------SysAttr------------------------------------
	SysAttr createSysAttr();
	
	boolean deleteSysAttr(String _uid);
	
	boolean saveSysAttr(SysAttr _sysAttr);
	
	SysAttr loadSysAttr(String _uid);
	
	List<SysAttr> loadSysAttrList(); 
}
