package legionLab;

import legion.data.MySqlDataSource;

public class DataSource {
	public final static String IP = "localhost";
	public final static String SCHEMA = "legionmodule";
	public final static String USER = "root";
	public final static String PASSWORD = "root!87570620";

	private final static MySqlDataSource INSTANCE = new MySqlDataSource(IP, SCHEMA, USER, PASSWORD);

	public final static MySqlDataSource getMySqlDs() {
		return INSTANCE;
	}
}
