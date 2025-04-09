package legion.serviceFacade.rmi;

import java.io.Serializable;

public abstract class ObjectModelRemote implements Serializable {
	private String uid;
	private long objectCreateTime;
	private long objectUpdateTime;

	protected ObjectModelRemote(String uid, long objectCreateTime, long objectUpdateTime) {
		super();
		this.uid = uid;
		this.objectCreateTime = objectCreateTime;
		this.objectUpdateTime = objectUpdateTime;
	}

	public String getUid() {
		return uid;
	}

	public long getObjectCreateTime() {
		return objectCreateTime;
	}

	public long getObjectUpdateTime() {
		return objectUpdateTime;
	}

	/**
	 * 以物件的uid作為Hash Code表示
	 */
	@Override
	public int hashCode() {
		return getUid().hashCode();
	}

	public boolean equals(Object _obj) {
		if (_obj == null)
			return false;
		if (!(_obj instanceof ObjectModelRemote))
			return false;
		if (this.getClass() != _obj.getClass())
			return false;
		return this.getUid().equals(((ObjectModelRemote) _obj).getUid());
	}

}
