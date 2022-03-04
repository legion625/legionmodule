package legion.data.search;

import java.util.ArrayList;
import java.util.List;

import legion.data.CompareOp;

public class SearchOperation<T extends SearchParam, U> {
	private List<SearchCondition<T>> conditions;
	private List<U> resultList;

	public SearchOperation() {
		conditions = new ArrayList<>();
		resultList = null;
	}

	public void addCondition(T _param, CompareOp _compareOp, Object _value) {
		conditions.add(new SearchCondition<T>(_param, _compareOp, _value));
	}
	
	public List<SearchCondition<T>> getConditions() {
		return conditions;
	}

	public List<U> getResultList() {
		return resultList;
	}

	public void setResultList(List<U> resultList) {
		this.resultList = resultList;
	}
	
}
