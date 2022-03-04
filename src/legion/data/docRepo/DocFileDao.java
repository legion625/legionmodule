package legion.data.docRepo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import legion.data.MySqlDao;
import legion.data.MySqlDataSource;
import legion.docRepo.DocFile;
import legion.util.DatabaseFO;

public class DocFileDao extends MySqlDao {

	public DocFileDao(MySqlDataSource _ds) {
		super(_ds);
	}

	// -------------------------------------------------------------------------------
	private final static String TABLE_DOC_FILE = "doc_file";
	private final static String COL_FILE_NAME = "file_name";
	private final static String COL_PATH = "path";

	private DbColumn<DocFile>[] docFileCols = new DbColumn[] {
			new DbColumn<DocFile>(COL_FILE_NAME, ColType.STRING, docFile -> docFile.getFileName()),
			new DbColumn<DocFile>(COL_PATH, ColType.STRING, docFile -> docFile.getPath()), };

	public boolean saveDocFile(DocFile _docFile) {
		return saveObject(TABLE_DOC_FILE, docFileCols, _docFile);
	}

	public boolean deleteDocFile(String _uid) {
		return deleteObject(TABLE_DOC_FILE, _uid);
	}

	private DocFile parseDocFile(ResultSet _rs) {
		DocFile df = null;
		try {
			String uid = _rs.getString(COL_UID);
			String fileName = _rs.getString(COL_FILE_NAME);
			String path = _rs.getString(COL_PATH);
			LocalDateTime createTime = DatabaseFO.parseLocalDateTime(_rs.getString(COL_OBJECT_CREATE_TIME));
			LocalDateTime updateTime = DatabaseFO.parseLocalDateTime(_rs.getString(COL_OBJECT_UPDATE_TIME));
			df = DocFile.getInstance(ds, uid, path, fileName, createTime, updateTime);
			/* pack attributes */
			// NONE
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return df;
	}

	public DocFile loadDocFile(String _uid) {
		return loadObject(TABLE_DOC_FILE, _uid, this::parseDocFile);
	}

}
