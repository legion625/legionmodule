package legion.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFO {
	private static Logger log = LoggerFactory.getLogger(DataFO.class);
	
	public final static String SEPERATED_LINE_HEADER = "::";
	public final static String SEPERATED_LINE_FOOTER = "##";
	
	// -------------------------------------------------------------------------------
	public static <T> T orElse(T _value, T _else) {
		Optional<T> opt = Optional.ofNullable(_value);
		return opt.orElse(_else);
	}
	
	public static String fillString(String _str, int _length, char _char) {
		if (_length <= 0)
			return "";

		if (_str == null)
			_str = "";

		if (_str.length() >= _length)
			return _str.substring(0, _length);
		else if (_str.length() == _length)
			return _str;
		else {
			char[] cArray = new char[_length - _str.length()];
			Arrays.fill(cArray, _char);
			return String.copyValueOf(cArray)+_str;
		}
	}

	/**
	 * Gets the MD5 has string of the specified string. Each byte of MD5 string is
	 * represented by a hex value with 2 digits. That is, if there is a byte which
	 * value is less then 10 (hex), its md5 has is '0X', e.g. 00, 01, ..., 0F.
	 */
	public static String getMD5String(String _str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			StringBuffer md5_str = new StringBuffer();
			byte[] b = md.digest(_str.getBytes());
			int num;
			for (int n = 0; n < b.length; n++) {
				num = b[n] & 255;
				String hex = Integer.toHexString(num);
				if(hex.length()==1)
					md5_str.append("0"+hex);
				else 
					md5_str.append(hex);
			}
			return md5_str.toString();
		} catch (NoSuchAlgorithmException e) {
			LoggerFactory.getLogger(DataFO.class).error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 亂數取得字串，為a~z、A~Z、0~1組成，_maxChar為最大長度，_minChar為最小長度。
	 * 最大長度、最小長度都必須>0，且最大長度>=最小長度。
	 * 
	 * @param _maxChar
	 * @param _minChar
	 * @return
	 */
	public static String getRandomString(int _maxChar, int _minChar) {
		if (_maxChar < 0 || _minChar < 0 || _maxChar < _minChar)
			return "";
		// 產生一組亂碼數字
		char[] markL = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
				'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
		char[] markU = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
				'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

		StringBuffer result = new StringBuffer();
		// 取得長度大小
		int count = (int) parseFloorNumber(Math.random() * (_maxChar - _minChar + 1), 0) + _minChar;
		// 亂數取得每個字元
		for (int i = 0; i < count; i++) {
			int s = (int) parseFloorNumber(Math.random() * 36, 0);
			int z = (int) parseFloorNumber(Math.random() * 2, 0);
			char w = z == 1 ? markL[s] : markU[s];
			result.append(w);
		}
		return result.toString();
	}

	/**
	 * 檢查是否為空白字串
	 * 
	 * @param _str: 欲檢查之字串
	 * @return boolean: 空白 或 非空白
	 */
	public static boolean isEmptyString(String _str) {
		return _str == null || _str.trim().length() == 0;
	}
	
	/** 判別字串是否為整數值 */
	public static boolean isInt(String _str) {
		if (isEmptyString(_str))
			return false;
		return _str.matches("[-]?(([0]\\.[0]+)|(0)|([1-9][0-9]*)|([1-9][0-9]*\\.[0]+))");
	}

	/** 判別字串是否為正整數值 */
	public static boolean isPositiveInt(String _str) {
		if (isEmptyString(_str))
			return false;
		return _str.matches("([1-9][0-9]*)|([1-9][0-9]*\\.[0]+)");
	}
	
	/** 判別字串是否為數值 */
	public static boolean isNumber(String _str) {
		if (isEmptyString(_str))
			return false;
		return _str.matches("[-]?(([0]\\.[\\d]+)|[\\d]|([1-9][\\d]*)|([1-9][\\d]*\\.[\\d]+))");
	}
	
	
	/**
	 * 無條件進位至所指定的小數位。若指定的小數位<0，則回傳原值。<br/>
	 * e.g. (3.432,0) -> 4; (3.432,1)->3.5; (3.432,2)->3.44; (3.432,-1)->3.432
	 * 
	 * @param _d
	 * @param _digit 指定的小數位
	 * @return
	 */
	public static double parseCeilNumber(double _d, int _digit) {
		if (_digit < 0)
			return _d;
		return MathUtils.round(_d, _digit, RoundingMode.CEILING);
	}
	
	/**
	 * 無條件捨去至所指定的小數位。若指定的小數位<0，則回傳原值。<br/>
	 * e.g. (3.432,0) -> 3; (3.432,1)->3.4; (3.432,2)->3.43; (3.432,-1)->3.432
	 * 
	 * @param _d
	 * @param _digit 指定的小數位
	 * @return
	 */
	public static double parseFloorNumber(double _d, int _digit) {
		if (_digit < 0)
			return _d;
		return MathUtils.round(_d, _digit, RoundingMode.FLOOR);
	}
	
	/**
	 * 四捨五入至所指定的小數位。若指定的小數位<0，則回傳原值。<br/>
	 * e.g. (3.432,0) -> 3; (3.452,1)->3.5; (3.432,1)->3.4; (3.432,-1)->3.432
	 * 
	 * @param _d
	 * @param _digit 指定的小數位
	 * @return
	 */
	public static double parseRoundNumber(double _d, int _digit) {
		if (_digit < 0)
			return _d;
		return MathUtils.round(_d, _digit, RoundingMode.HALF_EVEN);
	}
	
	
	/**
	 * 將字串轉換成XML標籤下的CDATA：將>、<、&以代碼表示。
	 * 
	 * @param _str
	 * @return
	 */
	public static String replaceXMLREChar(String _str) {
		String resultStr = "";
		if (_str != null) {
			resultStr = _str.replaceAll("&", "&amp;");
			resultStr = resultStr.replaceAll(">", "&gt;");
			resultStr = resultStr.replaceAll("<", "&lt;");
		}
		return resultStr;
	}

	/**
	 * 檢查字串是否符合定義的萬用字元表示Pattern <br>
	 * 萬用字元表示Pattern中，"_"表示一個任何字元，"%"表示零個以上（包含零個）任何字元。<br>
	 * 範例：<br>
	 * (張三封,_張三封)=false <br>
	 * (張三封,_%張三封)=false<br>
	 * (張三封,%張三封)=true<br>
	 * (張三封,%_張三封)=false<br>
	 * (張三封,張%封)=true<br>
	 * (張三封,張_封)=true<br>
	 * (張三封,張_%封)=true<br>
	 * (張三封,張%_封)=true<br>
	 * (張封,張%_封)=false<br>
	 * (張封,張%封)=true<br>
	 * (張三封,張三封_)=false<br>
	 * (張三封,張三封%)=true<br>
	 * (張三封,張三封%_)=false<br>
	 * (張三封w,張三封_%)=true<br>
	 * (張三封w,張三封%_)=true<br>
	 * (張三封w,張_封w)=true<br>
	 * 
	 * @param _str
	 * @param _filterStr
	 * @return
	 */
	public static boolean wildcardStringEqual(String _str, String _filterStr) {
		if (_str == null || _filterStr == null)
			return false;
		else if ("".equals(_str) && "".equals(_filterStr))
			return true;

		// 設定正規表示式
		String regx = _filterStr;
		regx = regx.replaceAll("_", ".");
		regx = regx.replaceAll("%", ".*");
		return _str.matches(regx);
	}
	
	public static String maskString(String _str, int _beginIdx, int _endIndx) {
		if (_str == null || _str.length() < 1)
			return _str;
		if (_beginIdx < 0 || _endIndx > _str.length() || _beginIdx > _endIndx) {
			log.warn("maskStrin input error.");
			return _str;
		}

		char[] mask = new char[_endIndx - _beginIdx];
		for (int i = 0; i < mask.length; i++)
			mask[i] = 'x';
		return _str.substring(0, _beginIdx) + new String(mask) + _str.substring(_endIndx, _str.length());
	}
	
	public static void pipe(InputStream _is, OutputStream _os) throws IOException {
		byte[] buffer = new byte[10240]; // 10 kb
		try {
			int len;
			while ((len = _is.read(buffer)) != -1) {
				_os.write(buffer, 0, len);
			}
		} finally {
			if (_is != null)
				_is.close();
		}
	}

	public static void copy(File _src, File _dest) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		byte[] buff = new byte[1024];
		try {
			fis = new FileInputStream(_src);
			fos = new FileOutputStream(_dest);
			int r;
			while ((r = fis.read(buff)) > -1) {
				fos.write(buff, 0, r);
			}
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}
		}
	}
	
	public static String parseByte2HexStr(byte[] _buf) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<_buf.length;i++) {
			String hex = Integer.toHexString(_buf[i] & 0xFF);
			if(hex.length()==1) {
				hex = '0'+hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	public static byte[] parseHexStr2Byte(String _hexStr) {
		if(_hexStr.length()<1 || _hexStr.length()%2!=0)
			return null;
		byte[] result = new byte[_hexStr.length() / 2];
		for(int i=0;i<_hexStr.length()/2;i++) {
			int high = Integer.parseInt(_hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(_hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
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
	
	public static String getStr(boolean _b) {
		return _b?"✓":"×";
	}
}
