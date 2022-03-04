package legion.docRepo;

import java.io.InputStream;

import legion.data.MySqlDataSource;
import legion.data.docRepo.DocFileDao;

public class DocRepoFacade {
	private final static DocRepoFacade INSTANCE = new DocRepoFacade();

	private DocRepoFacade() {
	}

	public final static DocRepoFacade getInstance() {
		return INSTANCE;
	}

	// -------------------------------------------------------------------------------
	public DocFile saveDocFile(MySqlDataSource _ds, String _path, String _fileName, InputStream _inStream)
			throws Exception {
		return DocFile.create(_ds, _path, _fileName, _inStream);
	}

	public boolean deleteDocFile(DocFile _docFile) {
		return _docFile.deleteProcess();
	}

	public DocFile loadDocFile(MySqlDataSource _ds, String _uid) {
		return new DocFileDao(_ds).loadDocFile(_uid);

	}
}
