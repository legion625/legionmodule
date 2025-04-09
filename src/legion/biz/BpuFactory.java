package legion.biz;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.util.LogUtil;

public class BpuFactory<T extends BpuType> {
	private Logger log = LoggerFactory.getLogger(BpuFactory.class);
	
	Bpu getBuilder(T _builderType, Object... _args) {
		if(!_builderType.match(_args)) {
			log.info("match return false.");
			return null;
		}
		
		try {
			Bpu b = (Bpu) _builderType.getBuilderClass().getDeclaredConstructor().newInstance();
			b.init(_args);
			return b;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			LogUtil.log(e);
			return null;
		}

	}
	
//	List<BizObjBuilder> getBuilderList(T[] _builderTypes, Object... _args) {
//		List<BizObjBuilder> list = new ArrayList<>();
//		for (T builderType : _builderTypes)
//			if (builderType.match(_args))
//				list.add(getBuilder(builderType, _args));
//		return list;
//	}
}
