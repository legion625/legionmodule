package legion.javaCallPython;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
//import org.python.util.PythonInterpreter;

public class JavaCallPythonTest {
	@Test
	public void test() throws IOException, InterruptedException {
//		PythonInterpreter interpreter = new PythonInterpreter();
//		interpreter.exec("days=('mod','Tue','Wed','Thu','Fri','Sat','Sun'); ");
//		interpreter.exec("print days[1];");
		String exe = "python";
//        String command = "D:\\calculator_simple.py";
		
//        String num1 = "1";
//        String num2 = "2";
		
//        String[] cmdArr = new String[] {exe,command,num1,num2};
		String command = "D:\\workspace_python\\lab\\basic.py";
		String[] cmdArr = new String[] {exe, command};
        Process process = Runtime.getRuntime().exec(cmdArr);
        InputStream is = process.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        String str = dis.readLine();
        process.waitFor();
        System.out.println(str);
	}
	
	@Test
	public void test1() throws IOException, InterruptedException {
		
		String exe = "python";
//  String command = "D:\\calculator_simple.py";
	
//  String num1 = "1";
//  String num2 = "2";
	
//  String[] cmdArr = new String[] {exe,command,num1,num2};
		String command = "D:\\workspace_python\\lab\\seq2seq\\eval.py";
//		String text1="abcdefg";
//		String[] cmdArr = new String[] { exe, command, text1 };
		String[] cmdArr = new String[] { exe, command };
		Process process = Runtime.getRuntime().exec(cmdArr);
		InputStream is = process.getInputStream();
		DataInputStream dis = new DataInputStream(is);
		String str = dis.readLine();
		process.waitFor();
		System.out.println(str);
	}
	
//	@Test
//	public void test2() {
//		System.setProperty("python.home", "C:\\jython2.7.0");
////	    String python = "D:\\simple_python.py";
//		String python = "D:\\workspace_python\\lab\\basic.py";
//	    
//	    PythonInterpreter interp = new PythonInterpreter();
//	    interp.execfile(python);
//	    interp.cleanup();
//	    interp.close();
//	}
	
//	@Test
//	public void test3() {
//		Py
//	}
//	

}
