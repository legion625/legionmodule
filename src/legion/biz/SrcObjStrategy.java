package legion.biz;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SrcObjStrategy {
	protected Logger log = LoggerFactory.getLogger(SrcObjStrategy.class);
	
	private <T extends SrcObjType> boolean matchInput(T _type, Object... _args) {
		Class[] a = _type.getArgsClasses();
		
		if(_args==null || _args.length==0)
			return a.length == 0;
		if(a.length!= _args.length)
			return false;
		for(int i=0;i<_args.length;i++)
			if(!a[i].isInstance(_args[i]))
				return false;
		return true;
	}
	
	public <T extends SrcObjType> List<? extends Object> getSrcObjList(T _srcObjType, Object... _args) {
		if (!matchInput(_srcObjType, _args)) {
			log.error("matchInput return false.");
			return null;
		}
		return getSrcObjListProcess(_srcObjType, _args);
	}

	protected abstract <T extends SrcObjType> List<? extends Object> getSrcObjListProcess(T _srcObjType,
			Object... _args);

	public abstract <T extends SrcObjType> boolean matchSrcObj(Object _srcObj, T _srcObjType, Object... _args);

}
