package legion.web.control.zk.legionmodule.pageTemplate.stepbar;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Include;

public class StepbarProxy implements Stepbar {
	public final static String STEPBAR_PROXY = "stepbarProxy";

	private static Logger log = LoggerFactory.getLogger(StepbarProxy.class);

	private StepbarTemplateComposer stepbarTemplateComposer;
	/**
	 * Not knowing what to do. How about remove this?
	 */
	@Deprecated
	private Component stepMainComponent;
	private ConcurrentHashMap<Class<?>, SelectorComposer<Component>> composers = new ConcurrentHashMap<>();

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private StepbarProxy() {
		this.stepMainComponent = null;
	}
	
	private StepbarProxy(Component _stepMainComponent) {
		this.stepMainComponent = _stepMainComponent;
	}

	// -------------------------------------------------------------------------------
	@Deprecated
	public Component getStepMainComponent() {
		return stepMainComponent;
	}

	public <T> T getComposer(Class<T> _composerClass) {
		return (T) composers.get(_composerClass);
	}

	public void setComposer(SelectorComposer<Component> _composer) {
		composers.put(_composer.getClass(), _composer);
	}

	@Override
	public int getCurrentIndex() {
		return stepbarTemplateComposer.getCurrentIndex();
	}

	@Override
	public int getStepSize() {
		return stepbarTemplateComposer.getStepSize();
	}

	@Override
	public void back() {
		stepbarTemplateComposer.back();
	}

	@Override
	public void next() {
		stepbarTemplateComposer.next();
	}

	@Override
	public void navigateTo(int _index) {
		stepbarTemplateComposer.navigateTo(_index);
	}

	// -------------------------------------------------------------------------------
	/**
	 * StepMain初始化 pageComposer不需要呼叫到mainComposer，應呼叫此方法。(recommended)
	 */
	public static StepbarProxy initStepbar(Include _icdStepbar, Step[] _steps) {
		return initStepbar(_icdStepbar, _steps, null);
	}

	/**
	 * StepMain初始化 pageComposer有需求呼叫到mainComposer，應呼叫此方法。(recommended)
	 */
	public static StepbarProxy initStepbar(Include _icdStepbar, Step[] _steps,
			SelectorComposer<Component> _stepMainComposer) {
		return initStepbar(_icdStepbar, _steps, _stepMainComposer, null);
	}

	/** StepMain初始化 */
	@Deprecated
	public static StepbarProxy initStepbar(Include _icdStepbar, Step[] _steps,
			SelectorComposer<Component> _stepMainComposer, Component _stepMainComponent) {
		StepbarProxy proxy = new StepbarProxy(_stepMainComponent);
		if (_stepMainComposer != null)
			proxy.setComposer(_stepMainComposer);
		proxy.stepbarTemplateComposer = StepbarTemplateComposer.getInstance(_icdStepbar);
		proxy.stepbarTemplateComposer.initialize(_steps, proxy);

		return proxy;
	}

	/** 在各StepPage頁面doAfterCompose時，應呼叫此方法。 */
	public static StepbarProxy register(SelectorComposer<Component> _stepPageComposer) {
		StepbarProxy proxy = (StepbarProxy) Executions.getCurrent().getAttribute(STEPBAR_PROXY);
		if (proxy != null)
			proxy.setComposer(_stepPageComposer);
		else
			log.warn("register: proxyn null.");
		return proxy;
	}

}
