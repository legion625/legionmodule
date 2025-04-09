package legion.datasource;

public interface TransactionInfo {
	int getConnSize();

	int getTransactionChain();

	String getUid();

	boolean isNonTransactionChain();

	boolean isRegular();

}
