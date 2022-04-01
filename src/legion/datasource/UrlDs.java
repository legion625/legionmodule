package legion.datasource;

import java.util.HashMap;
import java.util.Map;

public class UrlDs {
	private String name = "";
	private String protocol = "";
	private String user = "";
	private char[] sourcePpwwdd;
	private String host = "";
	private String port = "";
	private String path = "";
	private String file = "";
	private String query = "";
	private String url = "";

	public UrlDs() {
	}

	public UrlDs(String _url) {
		String str;
		url = _url;
		Map<String, String> map = parse(_url);
		if (map == null)
			return;
		if ((str = map.get("Name")) != null)
			name = str;
		if ((str = map.get("Protocal")) != null)
			protocol = str;
		if ((str = map.get("User")) != null)
			user = str;
		if ((str = map.get("Password")) != null) {
			sourcePpwwdd = str.toCharArray();
			str = null;
		}
		if ((str = map.get("Host")) != null)
			host = str;
		if ((str = map.get("Port")) != null)
			port = str;
		if ((str = map.get("Path")) != null)
			path = str;
		if ((str = map.get("File")) != null)
			file = str;
		if ((str = map.get("Query")) != null)
			query = str;
	}

	/**
	 * <pre>
	 * The method parses the input URL string into a HashMap. The HashMap contains
	 * the following keys: Name, Protocol, User, Password, Host, Port, Path, File,
	 * Query.
	 * 
	 * 'parse("name@:ldap://user:password@local:8080")' will result in '{Name=name,Password=password, Host=local, Protocol=ldap, User=user, Port=8080}'
	 * 'parse("name@:ldap://user:password@local:8080/")' will result in '{Name=name,Password=password, Host=local, Protocol=ldap, Path=/, User=user, Port=8080}'
	 * 'parse("ldap://host/path/path2")' will result in '{Host=host, Protocal=ldap, Path=/path/path2}'.
	 * 'parse("ldap://host/path/path2")' will result in '{Host=host, Protocal=ldap, Path=/path, File=path2}'.
	 * 
	 * </pre>
	 * 
	 * @author Min-Hua Chao
	 *
	 */
	public static Map<String, String> parse(String _url) {
		Map<String, String> map = new HashMap<>();

		// parsing for name.
		int pos = _url.indexOf("@:");
		if (pos > 0)
			map.put("Name", _url.substring(0, pos));
		else {
			map.put("Name", _url);
			return map;
		}

		// parsing for protocol.
		pos += 2;
		String str = _url.substring(pos);
		pos = str.indexOf("://");
		if (pos == -1)
			return map;
		map.put("Protocol", str.substring(0, pos));

		// parsing for user and password.
		pos += 3;
		if (pos >= str.length())
			return map;

		str = str.substring(pos);
		pos = 0;
		int pos2 = str.indexOf('@');
		int pos3;

		if (pos2 > 0) {
			String userStr = str.substring(pos, pos2);
			pos3 = userStr.indexOf(":");

			if (pos3 > 0) {
				map.put("User", userStr.substring(pos, pos3));
				if (pos3 < userStr.length() - 1)
					map.put("Password", userStr.substring(pos3 + 1));
			} else if (pos3 == 0) {
				if (pos3 < userStr.length() - 1)
					map.put("Password", userStr.substring(pos3 + 1));
			} else {
				if (userStr.length() > 0)
					map.put(("User"), userStr);
			}
			pos = pos2 + 1;
		} else if (pos2 == 0) {
			pos = pos2 + 1;
			// parsing for host and port.
		}
		if (pos >= str.length())
			return map;

		str = str.substring(pos);
		pos = 0;
		pos2 = str.indexOf('/');
		pos3 = str.indexOf(':');

		if (pos3 >= 0) {
			if (pos3 < pos2 - 1)
				map.put("Port", str.substring(pos3 + 1, pos2));
			else if (pos3 < str.length() - 2)
				map.put("Port", str.substring(pos3 + 1));
			if (pos3 > 0)
				map.put("Host", str.substring(pos, pos3));
		} else {
			if (pos2 > 0)
				map.put("Host", str.substring(pos, pos2));
			else if (pos2 < 0)
				map.put("Host", str.substring(pos));
		}

		// parsing for path.
		if (pos2 >= 0) {
			pos = pos2;
			if (pos >= str.length() - 1) {
				map.put("Path", "/");
				return map;
			}
		} else
			return map;

		str = str.substring(pos);
		pos = 0;
		pos2 = str.lastIndexOf('/');

		if (pos2 > 0)
			map.put("Path", str.substring(pos, pos2));
		else {
			map.put("Path", "/");
			// parsing for file
		}

		pos = pos2 + 1;

		if (pos >= str.length())
			return map;
		else
			str = str.substring(pos);

		pos = 0;
		pos2 = str.indexOf('?');

		if (pos2 > 0)
			map.put("File", str.substring(pos, pos2));
		else if (pos2 < 0) {
			map.put("File", str.substring(pos));
			return map;
		}

		// parsing for query string
		pos = pos2 + 1;

		if (pos >= str.length()) {
			return map;
		}

		map.put("Query", str.substring(pos));
		return map;
	}
	
	/**
	 * The method returns the connection string with the following format:
	 * &lt;protocol&gt;://&lt;user&gt;:&lt;password&gt;@&lt;host&gt;:&lt;port&gt;
	 */
	public String getConnString() {
		StringBuffer str = new StringBuffer();
		
		if (!name.equals(""))
			str.append(name + "@:");
		
		str.append(protocol + "://");
		
		if(!user.equals(""))
			str.append(user);
		
		if(!host.equals("")) {
			if(!user.equals(""))
				str.append("@"+host);
			else
				str.append(host);
		}
		if (!port.equals(""))
			str.append(":" + port);
		
//		str.toString(); 
		return str.toString();
	}

	public String getName() {
		return name;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getUser() {
		return user;
	}

	public char[] getSourcePpwwdd() {
		return sourcePpwwdd;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getUrl() {
		return url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setSourcePpwwdd(char[] sourcePpwwdd) {
		this.sourcePpwwdd = sourcePpwwdd;
	}
	
	String getAttrs() {
		String attrs = "{Name=" + name + ",protocol=" + protocol + ",user=" + user + ",host=" + host + ",port=" + port
				+ ",path=" + path + ",file=" + file + ",query=" + query + "}";
		return attrs;
	}

}
