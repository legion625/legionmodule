package legion.data.service.system;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.event.Level;

import legion.data.service.PgDao;
import legion.system.DocFile;
import legion.util.LogUtil;

public class DocFileDao extends PgDao {

	protected DocFileDao(String source) {
		super(source);
	}

	// -------------------------------------------------------------------------------
	private final static String TB_DOC_FILE = "sys_doc_file";
	private final static String COL_DF_FILE_NAME = "file_name";
	private final static String COL_DF_PATH = "path";

	boolean saveDocFile(DocFile _docFile) {
		DbColumn<DocFile>[] cols = new DbColumn[] { //
				DbColumn.of(COL_DF_FILE_NAME, ColType.STRING, DocFile::getFileName), //
				DbColumn.of(COL_DF_PATH, ColType.STRING, DocFile::getPath), //
		};
		return saveObject(TB_DOC_FILE, cols, _docFile);
	}

	boolean deleteDocFile(String _uid) {
		return deleteObject(TB_DOC_FILE, _uid);
	}

	private DocFile parseDocFile(ResultSet _rs) {
		try {
			DocFile df = DocFile.getInstance(parseUid(_rs), parseObjectCreateTime(_rs), parseObjectUpdateTime(_rs));
			/* pack attributes */
			df.setFileName(_rs.getString(COL_DF_FILE_NAME));
			df.setPath(_rs.getString(COL_DF_PATH));
			return df;
		} catch (SQLException e) {
			LogUtil.log(log, e, Level.ERROR);
			return null;
		}
	}

	DocFile loadDocFile(String _uid) {
		return loadObject(TB_DOC_FILE, _uid, this::parseDocFile);
	}

	List<DocFile> loadDocFileList() {
		return loadObjectList(TB_DOC_FILE, this::parseDocFile);
	}
}
