package legion.system;

import java.io.File;

import legion.DataServiceFactory;
import legion.data.SystemDataService;

public class DocFile extends SysObjectModel {
	// -------------------------------------------------------------------------------
	// -----------------------------------attribute-----------------------------------
	private String path;
	private String fileName;

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private DocFile() {
	}

	static DocFile newInstance() {
		DocFile df = new DocFile();
		df.configNewInstance();
		return df;
	}

	public static DocFile getInstance(String _uid, long _objectCreateTime, long _objectUpdateTime) {
		DocFile df = new DocFile();
		df.configGetInstance(_uid, _objectCreateTime, _objectUpdateTime);
		return df;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------LegionObject----------------------------------
	@Override
	protected boolean save() {
		return DataServiceFactory.getInstance().getService(SystemDataService.class).saveDocFile(this);
	}

	@Override
	protected boolean delete() {
		return DataServiceFactory.getInstance().getService(SystemDataService.class).deleteDocFile(getUid());
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------DocFile------------------------------------
	public static DocFile create(String _path, String _fileName) {
		/* 產生DocFile物件。 */
		DocFile docFile = newInstance();
		docFile.setFileName(_fileName);
		docFile.setPath(_path);
		return docFile.save() ? docFile : null;
	}

	// -------------------------------------------------------------------------------
	public File getFile() {
		return new File(path + fileName);
	}

}
