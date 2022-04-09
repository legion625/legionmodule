package legion.datasource.manager;

public interface TransactionEventHandler<T> {
	void process(TransactionEventType _event, T _data);
}
