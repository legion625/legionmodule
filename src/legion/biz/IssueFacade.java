package legion.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.util.LogUtil;

public class IssueFacade {
	private Logger log = LoggerFactory.getLogger(IssueFacade.class);

	// -------------------------------------------------------------------------------
	private final static IssueFacade INSTANCE = new IssueFacade();

	private IssueFacade() {
		bizObjBuilderFactoryMap = new HashMap<>();
		srcObjStrategyMap = new HashMap<>();
	}

	public final static IssueFacade getInstance() {
		return INSTANCE;
	}

	// -------------------------------------------------------------------------------
	private Map<Class<? extends BizObjBuilderType>, BizObjBuilderFactory<? extends BizObjBuilderType>> bizObjBuilderFactoryMap;

	private BizObjBuilderFactory<? extends BizObjBuilderType> getFactory(
			Class<? extends BizObjBuilderType> _bizObjBuilderTypeClass) {
		bizObjBuilderFactoryMap.putIfAbsent(_bizObjBuilderTypeClass, new BizObjBuilderFactory<>());
		return bizObjBuilderFactoryMap.get(_bizObjBuilderTypeClass);
	}
	
	// -------------------------------------------------------------------------------
	private Map<SrcObjType, SrcObjStrategy> srcObjStrategyMap;
	
	private <T extends SrcObjType> SrcObjStrategy getSrcObjStrategy(T _srcObjType,
			Class<? extends SrcObjStrategy> _srcObjStrategyClass) {
		try {
			srcObjStrategyMap.putIfAbsent(_srcObjType, _srcObjStrategyClass.getDeclaredConstructor().newInstance());
		} catch (Exception e) {
			LogUtil.log(e);
			return null;
		}
		return srcObjStrategyMap.get(_srcObjType);
	}
	
	// -------------------------------------------------------------------------------
	public final <T extends SrcObjType, U extends Object> List<U> getSrcObjList(T _srcObjType, Object... _args) {
		return (List<U>) getSrcObjStrategy(_srcObjType, _srcObjType.getStrategyClass()).getSrcObjList(_srcObjType,
				_args);
	}
	
	public final <T extends SrcObjType> boolean matchSrcObj(Object _srcObj, T _srcObjType, Object... _args) {
		return getSrcObjStrategy(_srcObjType, _srcObjType.getStrategyClass()).matchSrcObj(_srcObj, _srcObjType, _args);
	}
	
	public final <B extends BizObjBuilder, T extends BizObjBuilderType> B getBuilder(T _builderType, Object... _args) {
		return (B) ((BizObjBuilderFactory<T>) getFactory(_builderType.getClass())).getBuilder(_builderType, _args);
	}
	public final boolean matchBuilder(BizObjBuilderType _builderType, Object... _args) {
		return _builderType.match(_args);
	}
	
	
}
