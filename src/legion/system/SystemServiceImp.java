package legion.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import legion.DataServiceFactory;
import legion.data.SystemDataService;
import legion.util.LogUtil;
import legion.util.TimeTraveler;

public class SystemServiceImp implements SystemService {

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

	// -------------------------------------------------------------------------------
	// ------------------------------------DocFile------------------------------------
	@Override
	public DocFile createDocFile(String _path, String _fileName, InputStream _inStream) {
		TimeTraveler tt = new TimeTraveler();

		/* 存檔案 */
		File newFile = new File(_path + _fileName);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			int idx = 0;
			while ((idx = _inStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, idx);
			}

			_inStream.close();
			fileOutputStream.close();
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
			tt.travel();
			return null;
		}
		tt.addSite("revert write file.", () -> newFile.delete());

		/* 產生DocFile物件。 */
		DocFile docFile = DocFile.create(_path, _fileName);
		if (docFile == null) {
			tt.travel();
			log.error("DocFile.create() return null. {}\t{}", _path, _fileName);
			return null;
		}

		// 都成功了，把tt的內容釋放掉。
		tt = null;
		return docFile;
	}

	@Override
	public boolean deleteDocFile(String _uid) {
		DocFile docFile = loadDocFile(_uid);
		if (docFile == null) {
			log.info("docFile[{}] null.", _uid);
			return false;
		}

		/* 刪除DocFile物件 */
		if (!docFile.delete()) {
			log.error("docFile[{}].delete return false.", _uid);
			return false;
		}

		/* 刪檔案 */
		File file = docFile.getFile();
		if (file.exists()) {
			if (!file.delete()) {
				log.error("file.delete() return false. {}\t{}", docFile.getPath(), docFile.getFileName());
				return false;
			}
		}
		return true;
	}

	@Override
	public DocFile loadDocFile(String _uid) {
		return dataService.loadDocFile(_uid);
	}

	@Override
	public List<DocFile> loadDocFileList() {
		return dataService.loadDocFileList();
	}

}
