package legion;

import java.util.Map;

public interface ISystemInfo {
	public final static String USER_SESSION_NAME = "user";
	public final static String USER_JXPATHCONTEXT_SESION_NAME = "user_jxpath_context";
	public final static String CLOSE_LOGIN_NAME = "close_login_name";
	public final static String MAINTAIN_MESSAGE_NAME = "maintain_message_name";

	String getAttribute(String _key);

	Map<String, String> getAttributes();

	String[] getClassAnalyseClasspath();
	AspectManager getAspectManager();
	String getName();
	String getId();
	String getHostIp();
	String getVersion();
	void putAttribute(String _key, String _attr);
	void setAspectManager(AspectManager _manager);
	void setClassAnalyseClasspath(String[] _path);
}
