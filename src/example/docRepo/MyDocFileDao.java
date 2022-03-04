package example.docRepo;

import example.DataSource;
import legion.data.docRepo.DocFileDao;

public class MyDocFileDao extends DocFileDao {
	protected MyDocFileDao() {
		super(DataSource.getMySqlDs());
	}

	private final static MyDocFileDao INSTANCE = new MyDocFileDao();

	public final static MyDocFileDao getInstance() {
		return INSTANCE;
	}
}
