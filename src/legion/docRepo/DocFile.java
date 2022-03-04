package legion.docRepo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

import legion.data.MySqlDataSource;
import legion.data.docRepo.DocFileDao;
import legion.kernel.LegionObject;

public class DocFile extends LegionObject {
	private MySqlDataSource ds;
	private DocFileDao dao;
	// -------------------------------------------------------------------------------
	// -----------------------------------attribute-----------------------------------
	private String path;
	private String fileName;

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private DocFile(MySqlDataSource ds, String path, String fileName) {
		this.ds = ds;
		this.dao = new DocFileDao(ds);
		this.path = path;
		this.fileName = fileName;
	}

	static DocFile newInstance(MySqlDataSource _ds, String _path, String _fileName) {
		DocFile df = new DocFile(_ds, _path, _fileName);
		df.configNewInstance();
		return df;
	}

	public static DocFile getInstance(MySqlDataSource _ds, String _uid, String _path, String _fileName,
			LocalDateTime _createTime, LocalDateTime _updateTime) {
		DocFile df = new DocFile(_ds, _path, _fileName);
		df.configGetInstance(_uid, _createTime, _updateTime);
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
		return dao.saveDocFile(this);
	}

	@Override
	protected boolean delete() {
		return dao.deleteDocFile(this.getUid());
	}

	@Override
	protected MySqlDataSource getDataSource() {
		return ds;
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------DocFile------------------------------------
	public static DocFile create(MySqlDataSource _ds, String _path, String _fileName, InputStream _inStream)
			throws Exception {
		/* 存檔案 */
		File newFile = new File(_path + _fileName);
		FileOutputStream fileOutputStream = new FileOutputStream(newFile);
		byte[] buffer = new byte[1024];
		int idx = 0;
		while ((idx = _inStream.read(buffer)) != -1) {
			fileOutputStream.write(buffer, 0, idx);
		}
		_inStream.close();
		fileOutputStream.close();

		/* 產生DocFile物件。 */
		DocFile docFile = newInstance(_ds, _path, _fileName);
		if (docFile.save())
			return docFile;
		else
			throw new Exception("docFile save error!!");
	}
	
	public boolean deleteProcess() {
		/* 刪除DocFile物件 */
		boolean del = delete();
		if (!del)
			return false;

		/* 刪檔案 */
		File file = new File(path + fileName);
		if (file.exists()) {
			if (!file.delete()) {
				// TODO log.
				return false;
			}
		}
		return true;
	}

	public File getFile() {
		return new File(path + fileName);
	}

}
