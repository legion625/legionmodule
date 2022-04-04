package legion;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.data.ObjectSeqDataService;
import legion.util.DataFO;

public abstract class ObjectModel {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	// OID
	protected static final String YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	protected static final String HIGH_OID = DataServiceFactory.getInstance().getService(ObjectSeqDataService.class)
			.getSeq("SysObjectModel");
	protected static final String OID_SEPARATOR = "!";
	
	protected static long low_oid = 1;
	
	// -------------------------------------------------------------------------------
	private String uid;
	private long objCreateDate;
	private long objUpdateDate;
	
	// -------------------------------------------------------------------------------
	protected abstract boolean delete();
	public abstract boolean equals(Object _obj);
	
	protected String generateUid() throws Exception {
		String oid = "";
		if(DataFO.isEmptyString(HIGH_OID)) {
			log.error("HIGH_OID error.");
			throw new Exception("HIGH_OID error.");
		}
		synchronized (ObjectModel.class) {
			oid = YEAR + OID_SEPARATOR+HIGH_OID +OID_SEPARATOR+low_oid;
			low_oid++;
		}
		this.setUid(oid);
		return oid;
	}
	
	// -------------------------------------------------------------------------------
	public String getUid() {
		return uid;
	}
	public long getObjCreateDate() {
		return objCreateDate;
	}
	public long getObjUpdateDate() {
		return objUpdateDate;
	}
	
	
}
