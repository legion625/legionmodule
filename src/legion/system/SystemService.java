package legion.system;

import java.io.InputStream;
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

	// -------------------------------------------------------------------------------
	// ------------------------------------DocFile------------------------------------
	DocFile createDocFile(String _path, String _fileName, InputStream _inStream);

	boolean deleteDocFile(String _uid);

	DocFile loadDocFile(String _uid);

	List<DocFile> loadDocFileList();

}
