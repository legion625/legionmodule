package legion.data;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.datasource.manager.DSManager;

public abstract class AbstractDao {
	protected static Logger log = LoggerFactory.getLogger(AbstractDao.class);

	protected DSManager getDsManager() {
		return DSManager.getInstance();
	}

}
