package legion;

import javax.servlet.ServletContext;

public interface ISystemWebInfo extends ISystemInfo {
	ServletContext getServletContext();
	
	void setServletContext(ServletContext servletContext);

	int getLocaleCookieAge();

}
