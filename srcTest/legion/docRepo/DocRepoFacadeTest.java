package legion.docRepo;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import example.DataSource;
import legion.data.MySqlDataSource;

public class DocRepoFacadeTest {
	@Test
	public void testSaveDocFile() throws Exception {
		File file = new File("C:\\Users\\s1060\\Desktop\\rfi.docx");
		System.out.println("file: " +file);
		System.out.println("file.getPath(): " +file.getPath());
		System.out.println("file.getName(): " +file.getName());
		System.out.println("file.exists(): " +file.exists());
		
		InputStream inStream = new FileInputStream(file);
		
		MySqlDataSource _ds = DataSource.getMySqlDs();
		String _path = "C:\\Users\\s1060\\Desktop\\";
		String _fileName = "rfi_test.docx";
		DocRepoFacade.getInstance().saveDocFile(_ds, _path, _fileName, inStream);
	}
}
