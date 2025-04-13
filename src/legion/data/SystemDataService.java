package legion.data;

import java.util.List;

import legion.IntegrationService;
import legion.docRepo.DocFile;
import legion.system.SysAttr;

public interface SystemDataService extends IntegrationService {
	
	boolean testCallback();
	
//	boolean testPgSqlCallback();
	
	// -------------------------------------------------------------------------------
	// ------------------------------------SysAttr------------------------------------
	boolean saveSysAttr(SysAttr _sysAttr);

	boolean deleteSysAttr(String _uid);

	SysAttr loadSysAttr(String _uid);

	List<SysAttr> loadSysAttrList();
	
	// -------------------------------------------------------------------------------
	// ------------------------------------DocFile------------------------------------
	boolean saveDocFile(DocFile _docFile);
	boolean deleteDocFile(String _uid);
	DocFile loadDocFile(String _uid);
	
}
