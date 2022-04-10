package legion.aspect;

public interface AspectBus {
	public final static String USER = "UserIdx"; // TODO check necessary
	public final static String USER_JXPATH_CONTEXT = "UserJxpathContext"; // TODO check necessary
	public final static String ACTION_MAPPING = "ActionMapping"; // TODO check necessary
	public final static String HTTP_SERVLET_REQUEST = "HttpServletRequest"; // TODO check necessary
	public final static String HTTP_SERVLET_RESPONSE = "HttpServletResponse"; // TODO check necessary
	public final static String ACTION_CLASS = "ActionClass"; // TODO check necessary
	public final static String METHOD_NAME = "MethodName"; // TODO check necessary
	public final static String SERVICE_MESSAGE = "ServiceMessage"; // TODO check necessary
	public final static String SERVICE_MESSAGE_SPACE = "ServiceMessageSpace"; // TODO check necessary
	public final static String SERVICE_FAIL_MESSAGE = "ServiceFailMessage"; // TODO check necessary
	public final static String BUSINESS_CLASS = "BusinessClass"; // TODO check necessary
	public final static String BUSINESS_METHOD = "BusinessMethod"; // TODO check necessary
	public final static String BUSINESS_METHOD_PARAM = "BusinessMethodParam"; // TODO check necessary
	public final static String BUSINESS_METHOD_PARAM_QUERY_OPERATION = "BusinessMethodParamQueryOperation"; // TODO check necessary
	public final static String BUSINESS_PROBE_FRAG = "BusinessProbeFrag"; // TODO check necessary
	public final static String BUSINESS_RESULT = "BusinessResult"; // TODO check necessary
	public final static String CURRENT_LOGIC_SERVICE = "CurrentLogicService"; // TODO check necessary

	public final static String USER_MENU_HANDLER_TARGET_CLASS = "UserMenuHandlerTargetClass"; // TODO check necessary
	public final static String USER_MENU_HANDLER_TARGET_METHOD = "UserMenuHandlerTargetMethod"; // TODO check necessary
	public final static String USER_MENU_HANDLER_DATA = "UserMenuHandlerData"; // TODO check necessary

	public void addParam(String _key, Object _obj);

	public Object getParam(String _key);

}
