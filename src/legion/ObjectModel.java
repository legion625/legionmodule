package legion;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.data.ObjectSeqDataService;
import legion.util.DataFO;

public abstract class ObjectModel {
	protected static final Logger log = LoggerFactory.getLogger(ObjectModel.class);

	// OID
	protected static final String YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	protected static final String HIGH_OID = DataServiceFactory.getInstance().getService(ObjectSeqDataService.class)
			.getSeq("SysObjectModel");
	protected static final String OID_SEPARATOR = "!";

	protected static long low_oid = 1;

	// -------------------------------------------------------------------------------
	private String uid;
	private long objectCreateTime;
	private long objectUpdateTime;

	// -------------------------------------------------------------------------------
	protected boolean generateUid() {
		String oid = "";
		if (DataFO.isEmptyString(HIGH_OID)) {
			log.error("HIGH_OID error.");
			return false;
		}
		log.debug("high");
		synchronized (ObjectModel.class) {
			oid = YEAR + OID_SEPARATOR + HIGH_OID + OID_SEPARATOR + low_oid;
			low_oid++;
		}
		this.setUid(oid);
		log.debug("HIGH_OID: {}\tlow_oid: {}",HIGH_OID, low_oid);
		log.debug("oid: {}", oid);
		return true;
	}
	
	protected <T extends ObjectModel> T configNewInstance() {
		if (!generateUid()) {
			log.error("generateUid return false.");
			return null;
		}
		log.debug("getUid(): {}", getUid());
		
		return (T) this;
	}
	
	protected void configGetInstance(String _uid, long _objectCreateTime, long _objectUpdateTime) {
		setUid(_uid);
		setObjectCreateTime(_objectCreateTime);
		setObjectUpdateTime(_objectUpdateTime);
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public long getObjectCreateTime() {
		return objectCreateTime;
	}

	public void setObjectCreateTime(long objectCreateTime) {
		this.objectCreateTime = objectCreateTime;
	}

	public long getObjectUpdateTime() {
		return objectUpdateTime;
	}

	public void setObjectUpdateTime(long objectUpdateTime) {
		this.objectUpdateTime = objectUpdateTime;
	}

	/**
	 * 以物件的uid作為Hash Code表示
	 */
	@Override
	public int hashCode() {
		return getUid().hashCode();
	}

	// -------------------------------------------------------------------------------
	protected abstract boolean save();

	protected abstract boolean delete();

	public boolean equals(Object _obj) {
		if (_obj == null)
			return false;
		if (!(_obj instanceof ObjectModel))
			return false;
		if (this.getClass() != _obj.getClass())
			return false;
		return this.getUid().equals(((ObjectModel) _obj).getUid());
	}

}
