package legion.datasource;

import org.junit.Test;

import legion.AbstractLegionInitTest;
import legion.DataServiceFactory;
import legion.data.SystemDataService;

public class PgTest2 extends AbstractLegionInitTest  {
	private static SystemDataService dataService = DataServiceFactory.getInstance().getService(SystemDataService.class);

	@Test
	public void test() {
		assert dataService.testCallback();
//		assert dataService.testPgSqlCallback();
	}
}
