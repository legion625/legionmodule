package legion.util;

public class DataFO {
	public static boolean isEmptyString(String _str) {
		if (_str == null || _str.equalsIgnoreCase(""))
			return true;
		else
			return false;
	}
	
	public static String fillString(String _str, int _length, char _char) {
		if (_str.length() >= _length)
			return _str.substring(0, _length);
		else {
			String str = "";
			int s = _length - _str.length();
			for (int i = 0; i < s; i++)
				str += _char;
			str+=_str;
			return str;
		}
		
//		_str.
//		int youNumber = 1;   
	    // 0 代表前面補充0   
	    // 4 代表長度為4   
	    // d 代表參數為正數型   
//	    String str = String.format("%"+_char+"$"+_length+"s", _str);   
////	    System.out.println(str); // 0001  
//	    return str;
	}
	
//	特殊字符	代表含义	替换内容
//	+	URL 中+号表示空格	%2B
//	空格	URL中的空格可以用+号或者编码	%20
//	/	分隔目录和子目录	%2F
//	?	分隔实际的URL和参数	%3F
//	%	指定特殊字符	%25
//	#	表示书签	%23
//	&	URL 中指定的参数间的分隔符	%26
//	=	URL 中指定参数的值	%3D
	public static String toUrlFormat(String _str) {
		if (_str == null)
			return "";
		return _str.replace("%", "%25").replace("+", "%2B").replace(" ", "%20").replace("/", "%2F")
				.replace("?", "%3F").replace("#", "%23").replace("&", "%26").replace("=", "%3D");

	}
}
