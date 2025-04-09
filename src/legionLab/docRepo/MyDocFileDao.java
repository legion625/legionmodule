package legionLab.docRepo;

import legion.data.docRepo.DocFileDao;
import legionLab.DataSource;

public class MyDocFileDao extends DocFileDao {
	protected MyDocFileDao() {
		super(DataSource.getMySqlDs());
	}

	private final static MyDocFileDao INSTANCE = new MyDocFileDao();

	public final static MyDocFileDao getInstance() {
		return INSTANCE;
	}
}
