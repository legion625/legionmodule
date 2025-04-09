package legion.kernel;

import java.time.LocalDateTime;
import java.util.Date;


import legion.data.MySqlDataSource;
import legion.data.ObjSeqDao;
@Deprecated
public abstract class LegionObject {
	
	private String uid;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;

//	protected LegionObject(MySqlDataSource ds) {
//		this.ds = ds;
//	}
//	protected void setDs(MySqlDataSource ds) {
//		this.ds = ds;
//	}
	protected abstract MySqlDataSource getDataSource();
	
	protected String getObjSeqKey() {
		return this.getClass().getSimpleName();
	}

	protected void configNewInstance() {
		this.uid = generateUid();
	}

	private String generateUid() {
		String key = getObjSeqKey();
		ObjSeqDao objSeqDao = new ObjSeqDao(getDataSource());
		int i = objSeqDao.getObjSeqIndex(key);
		return key + i;
	}

	protected void configGetInstance(String uid, LocalDateTime createTime, LocalDateTime updateTime) {
		this.uid = uid;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public String getUid() {
		return uid;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	protected abstract boolean save();

	protected abstract boolean delete();

}
