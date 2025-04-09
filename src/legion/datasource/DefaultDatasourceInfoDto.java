package legion.datasource;

import java.io.Serializable;

public class DefaultDatasourceInfoDto implements DatasourceInfo, Serializable {
	private int maxActive;
	private int maxIdle;
	private int active;
	private int idle;
	private int maxWait;
	private String validationQuery;
	private String name;
	private String url;
	private String alertMail;

	@Override
	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	@Override
	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	@Override
	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	@Override
	public int getIdle() {
		return idle;
	}

	public void setIdle(int idle) {
		this.idle = idle;
	}

	@Override
	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	@Override
	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getAlertMail() {
		return alertMail;
	}

	public void setAlertMail(String alertMail) {
		this.alertMail = alertMail;
	}

}
