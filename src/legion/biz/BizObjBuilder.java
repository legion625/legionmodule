package legion.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import legion.ObjectModel;
import legion.util.LogUtil;
import legion.util.TimeTraveler;

public abstract class BizObjBuilder<U extends ObjectModel> {
	protected Logger log = LoggerFactory.getLogger(BizObjBuilder.class);

	protected Object[] args;

	public final void init(Object[] _args) {
		args = _args;
		appendBase();
	}

	protected abstract BizObjBuilder<U> appendBase();

	/**
	 * 「確認」：合理性檢查（業務層級）
	 * 
	 * @param _msg
	 * @return
	 */
	public abstract boolean validate(StringBuilder _msg);

	/**
	 * 「驗證」：正確性檢查（資料結構層級）
	 * 
	 * @param _msg
	 * @return
	 */
	public abstract boolean verify(StringBuilder _msg);

	public final U build(StringBuilder _msg, TimeTraveler _tt) {
		if (!verify(_msg))
			return null;
		TimeTraveler tt = new TimeTraveler();
		try {
			U result = buildProcess(tt);
			if (result != null) {
				if (_tt != null)
					_tt.copySitesFrom(tt);
				return result;
			} else {
				tt.travel();
				return null;
			}
		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
			tt.travel();
			return null;
		}
	}

	protected abstract U buildProcess(TimeTraveler _tt);

}
