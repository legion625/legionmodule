package legion;

public class ObjectModelInfoDto {
	private String uid;
	private long objectCreateTime;
	private long objectUpdateTime;

	protected ObjectModelInfoDto(String uid, long objectCreateTime, long objectUpdateTime) {
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

	@Override
	public boolean equals(Object _obj) {
		if ((_obj == null) || !(_obj instanceof ObjectModelInfoDto) || (this.getClass() != _obj.getClass()))
			return false;
		return this.getUid().equals(((ObjectModelInfoDto) _obj).getUid());
	}

}
