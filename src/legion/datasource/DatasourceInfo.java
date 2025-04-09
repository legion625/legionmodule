package legion.datasource;

public interface DatasourceInfo {
	int getActive();

	String getAlertMail();

	int getIdle();

	int getMaxActive();

	int getMaxIdle();

	int getMaxWait();

	String getName();

	String getUrl();

	String getValidationQuery();
}
