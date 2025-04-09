package legion.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import legion.LegionContext;

public class LogUtil {
	public static Logger log = LoggerFactory.getLogger(LogUtil.class);
	private static int printStackTraceLevel = 3; // 預設為3
	static {
		String printStackTraceLevelStr = LegionContext.getInstance().getSystemInfo()
				.getAttribute("system.log.LogUtil.printStackTraceLevel");
		log.debug("printStackTraceLevelStr: {}", printStackTraceLevelStr);
		if (DataFO.isInt(printStackTraceLevelStr))
			printStackTraceLevel = Integer.parseInt(printStackTraceLevelStr);
		log.debug("printStackTraceLevel: {}", printStackTraceLevel);
	}

	private LogUtil() {
	}

	public static void log(Throwable e) {
		log(e, Level.WARN);
	}

	public static void log(Logger _log, Throwable e) {
		log(_log, e, Level.WARN);
	}

	public static void log(Throwable e, String _typeMsg) {
		log(e, Level.WARN, _typeMsg);
	}

	public static void log(Logger _log, Throwable e, String _typeMsg) {
		log(_log, e, Level.WARN, _typeMsg);
	}

	public static void log(Throwable e, Level _level) {
		log(e, _level, "");
	}

	public static void log(Logger _log, Throwable e, Level _level) {
		log(_log, e, _level, "");
	}

	public static void log(Throwable e, Level _level, String _typeMsg) {
		log(null, e, _level, _typeMsg);
	}

	public static void log(Logger _log, Throwable e, Level _level, String _typeMsg) {
		if (_level == null || e == null)
			return;
		_typeMsg = DataFO.isEmptyString(_typeMsg) ? "Exception Stack Root" : _typeMsg;
		_log = _log == null ? log : _log;
		// 當e.getStackTrace()為空時，直接回傳。
		if (e.getStackTrace() == null || e.getStackTrace().length <= 0) {
			_log.trace("{}:{}.{}[{}]:{}", _typeMsg, "", "", "", e.getMessage());
			return;
		}

		int printedLevel = 0;
		StackTraceElement[] exStack = e.getStackTrace();
		StackTraceElement rootStack = exStack[0];
		switch (_level) {
		case TRACE: {
			_log.trace("{}:{}.{}[{}]:{}", _typeMsg, rootStack.getClassName(), rootStack.getMethodName(),
					rootStack.getLineNumber(), e.getMessage());
			while (printedLevel < printStackTraceLevel && printedLevel < exStack.length) {
				printedLevel++;
				StackTraceElement stackItem = exStack[printedLevel];
				_log.trace("\t{}.{}[{}]", stackItem.getClassName(), stackItem.getMethodName(),
						stackItem.getLineNumber());
			}
			break;
		}
		case DEBUG: {
			_log.debug("{}:{}.{}[{}]:{}", _typeMsg, rootStack.getClassName(), rootStack.getMethodName(),
					rootStack.getLineNumber(), e.getMessage());
			while (printedLevel < printStackTraceLevel && printedLevel < exStack.length) {
				printedLevel++;
				StackTraceElement stackItem = exStack[printedLevel];
				_log.debug("\t{}.{}[{}]", stackItem.getClassName(), stackItem.getMethodName(),
						stackItem.getLineNumber());
			}
			break;
		}
		case INFO: {
			_log.info("{}:{}.{}[{}]:{}", _typeMsg, rootStack.getClassName(), rootStack.getMethodName(),
					rootStack.getLineNumber(), e.getMessage());
			while (printedLevel < printStackTraceLevel && printedLevel < exStack.length) {
				printedLevel++;
				StackTraceElement stackItem = exStack[printedLevel];
				_log.info("\t{}.{}[{}]", stackItem.getClassName(), stackItem.getMethodName(),
						stackItem.getLineNumber());
			}
			break;
		}
		case WARN:
			_log.warn("{}:{}.{}[{}]:{}", _typeMsg, rootStack.getClassName(), rootStack.getMethodName(),
					rootStack.getLineNumber(), e.getMessage());
			while (printedLevel < printStackTraceLevel && printedLevel < exStack.length) {
				printedLevel++;
				StackTraceElement stackItem = exStack[printedLevel];
				_log.warn("\t{}.{}[{}]", stackItem.getClassName(), stackItem.getMethodName(),
						stackItem.getLineNumber());
			}
			break;
		case ERROR:
			_log.error("{}:{}.{}[{}]:{}", _typeMsg, rootStack.getClassName(), rootStack.getMethodName(),
					rootStack.getLineNumber(), e.getMessage());
			while (printedLevel < printStackTraceLevel && printedLevel < exStack.length) {
				printedLevel++;
				StackTraceElement stackItem = exStack[printedLevel];
				_log.error("\t{}.{}[{}]", stackItem.getClassName(), stackItem.getMethodName(),
						stackItem.getLineNumber());
			}
			break;
		default:
			_log.error("{}:{}.{}[{}]:{}", _typeMsg, rootStack.getClassName(), rootStack.getMethodName(),
					rootStack.getLineNumber(), e.getMessage());
			while (printedLevel < printStackTraceLevel && printedLevel < exStack.length) {
				printedLevel++;
				StackTraceElement stackItem = exStack[printedLevel];
				_log.error("\t{}.{}[{}]", stackItem.getClassName(), stackItem.getMethodName(),
						stackItem.getLineNumber());
			}
		}
	}
}
