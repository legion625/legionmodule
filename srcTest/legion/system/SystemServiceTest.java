package legion.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

import legion.AbstractLegionInitTest;
import legion.BusinessServiceFactory;
import legion.data.MySqlDataSource;

public class SystemServiceTest extends AbstractLegionInitTest {
	@Test
	public void testSaveDocFile() throws FileNotFoundException {
		File file = new File("C:\\Users\\User\\Desktop\\Ai.pdf");
		InputStream inStream = new FileInputStream(file);

//		MySqlDataSource _ds = DataSource.getMySqlDs();
		SystemService service = BusinessServiceFactory.getInstance().getService(SystemService.class);
		String path = "C:\\Users\\user\\Desktop\\";
		String fileName = "Ai_copy.pdf";
		DocFile df = service.createDocFile(path, fileName, inStream);
//		DocRepoFacade.getInstance().saveDocFile(_ds, _path, _fileName, inStream);
	}

}
