package legion.system;

import java.util.Calendar;

import legion.DataServiceFactory;
import legion.ObjectModel;
import legion.data.ObjectSeqDataService;

public abstract class SysObjectModel extends ObjectModel{
	
	@Override
	protected abstract boolean delete();
	
	@Override
	public abstract boolean equals(Object _obj);
	
	
	
	
	
	

}
