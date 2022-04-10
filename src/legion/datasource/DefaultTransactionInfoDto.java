package legion.datasource;

import java.io.Serializable;

public class DefaultTransactionInfoDto implements TransactionInfo, Serializable {
	private int connSize;
	private boolean nonTransactionChain;
	private boolean regular;
	private int transactionChain;
	private String uid;

	@Override
	public int getConnSize() {
		return connSize;
	}

	public void setConnSize(int connSize) {
		this.connSize = connSize;
	}

	@Override
	public boolean isNonTransactionChain() {
		return nonTransactionChain;
	}

	public void setNonTransactionChain(boolean nonTransactionChain) {
		this.nonTransactionChain = nonTransactionChain;
	}

	@Override
	public boolean isRegular() {
		return regular;
	}

	public void setRegular(boolean regular) {
		this.regular = regular;
	}

	@Override
	public int getTransactionChain() {
		return transactionChain;
	}

	public void setTransactionChain(int transactionChain) {
		this.transactionChain = transactionChain;
	}

	@Override
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

}
