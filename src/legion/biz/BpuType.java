package legion.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface BpuType {
	Class getBuilderClass();

	Class[] getArgsClasses();

	default boolean matchInput(Object... _args) {
		Class[] a = getArgsClasses();
		if (_args == null || _args.length == 0)
			return a.length == 0;
		if (a.length != _args.length)
			return false;
		for (int i = 0; i < _args.length; i++)
			if (!a[i].isInstance(_args[i]))
				return false;
		return true;
	}

	boolean matchBiz(Object... _args);

	default boolean match(Object... _args) {
		Logger log = LoggerFactory.getLogger(BpuType.class);
		if (!matchInput(_args)) {
			log.error("matchInput return false.");
			return false;
		}
		if (!matchBiz(_args)) {
			log.info("matchBiz return false.");
			return false;
		}
		return true;
	}

}
