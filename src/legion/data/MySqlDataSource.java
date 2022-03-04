package legion.data;

public class MySqlDataSource {
	private String ip;
	private String schema;
	private String user;
	private String password;

	public MySqlDataSource(String ip, String schema, String user, String password) {
		this.ip = ip;
		this.schema = schema;
		this.user = user;
		this.password = password;
	}

	public String getIp() {
		return ip;
	}

	public String getSchema() {
		return schema;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
