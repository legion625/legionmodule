package legion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Fsm<T> {
	private Logger log = LoggerFactory.getLogger(Fsm.class);

	private String uid;
	private T status;

	public Fsm(String uid, T status) {
		this.uid = uid;
		this.status = status;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public T getStatus() {
		return status;
	}

	public void setStatus(T status) {
		this.status = status;
	}

	// -------------------------------------------------------------------------------
	public final boolean transfer(T _from, T _to) {
		if (getStatus() == _from) {
			setStatus(_to);
			return true;
		}
		log.error("[{}] status [{}] error!!", getUid(), getStatus());
		return false;
	}

	public final boolean transfer(T[] _avlFroms, T _to) {
		for (T _from : _avlFroms) {
			if (getStatus() == _from) {
				setStatus(_to);
				return true;
			}
		}
		log.error("[{}] status [{}] error!!", getUid(), getStatus());
		return false;
	}
}
