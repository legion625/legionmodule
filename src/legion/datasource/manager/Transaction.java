package legion.datasource.manager;

import java.util.Arrays;
import java.util.EventListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import legion.datasource.UrlDs;
import legion.util.LogUtil;

public class Transaction {
	private static Logger log = LoggerFactory.getLogger(Transaction.class);
	private volatile ConcurrentHashMap<String, TransactionConnection> connMap = new ConcurrentHashMap<>();
	private volatile int transactionChain;
	private String id;
	private volatile boolean failed = false;

	private long createTime;
	private long startTime;
	private boolean debug;
	private CopyOnWriteArrayList<StackTraceElement[]> transactionTraces;
	private CopyOnWriteArrayList<EventListener<?>> eventHandlers;

	public Transaction(String id, boolean debug) {
		this.id = id;
		this.createTime = System.currentTimeMillis();
		this.debug = debug;
		if (debug)
			transactionTraces = new CopyOnWriteArrayList<>();
	}

	/** 增加交易遞迴數 */
	private synchronized void accTransactionChain() {
		transactionChain++;
	}

	/** 減少交易遞迴數 */
	private synchronized void decTransactionChain() {
		transactionChain--;
	}

	/** 開始(階段)交易 */
	public synchronized void beginTransaction() {
		if (debug)
			// 將該交易點的StackTrace記錄，以備後續追查使用。
			addStackTrace(Thread.currentThread().getStackTrace());
		if (transactionChain == 0)
			// 初始交易時間
			startTime = System.currentTimeMillis();
		accTransactionChain();
	}

	/** 將該交易點的stackTrace記錄，以備後續追查使用 */
	private void addStackTrace(StackTraceElement[] _trace) {
		
		// 進行篩選至beginTransaction呼叫的前2個
		// stack item
		int idx = 0;
		boolean tsPoint = false, breakPoint = false;
		for (StackTraceElement trace : _trace) {
			if (tsPoint)
				breakPoint = true;
			if (DSManager.class.getName().equalsIgnoreCase(trace.getClassName())
					&& ("beginTransaction".equalsIgnoreCase(trace.getMethodName())
							|| "endTransaction".equalsIgnoreCase(trace.getMethodName())
							|| "failTransaction".equalsIgnoreCase(trace.getMethodName()))) {
				// 呼叫DsManager交易相關記錄點
				tsPoint = true;
				breakPoint = false;
			}
			if (tsPoint && breakPoint)
				break;
			idx++;
		}
		_trace = Arrays.copyOf(_trace, (idx + 1) > _trace.length ? _trace.length : (idx + 1));
		transactionTraces.add(_trace);
	}
	
	/** 確認該階段運作成功，會進行交易狀態判斷，若是已經完成該交易的所有階段，則進行整體交易運作commit。 */
	protected synchronized boolean endTransaction() {
		if (debug)
			// 將該交易點的stack trace記錄，以備後續追查使用。
			addStackTrace(Thread.currentThread().getStackTrace());
		if (failed)
			return true;
		// 先減少交易階段數
		decTransactionChain();
		// 判別是否已完成所有交易階段，是否可以進行整體commit
		return commitTransaction();
	}
	
	/** transactionFail 該交易失敗，交易階段遞減，然後進行rollback及endTransaction結束整個交易 */
	public synchronized void failTransaction() {
		if(failed)
			return;
		// 進行rollback
		rollbackTransaction();
		failed = true;
		transactionChain = 0;
	}
	
	private synchronized boolean rollbackTransaction() {
		for(TransactionConnection conn:connMap.values()) {
			try {
				// 回復狀態
				conn.rollbackAll();
				// 釋放
				conn.release();
				conn = null;
			}catch (Exception e) {
				log.error("Transaction[{}] rollbackTransaction - close connection[{}] fail", id, conn.toString());
				continue;
			}
		}
		connMap.clear();
		fireEvent(TransactionEventType.AfterRollback);
		return true;
	}
	
	private synchronized boolean commitTransaction() {
		if (transactionChain == 0) {
			for (TransactionConnection conn : connMap.values()) {
				try {
					conn.commitAll();
					conn.release();
					conn = null;
				} catch (Exception e) {
					LogUtil.log(e, Level.ERROR);
					e.printStackTrace();
					continue;
				}
			}
			connMap.clear();
			fireEvent(TransactionEventType.AfterCommit);
			return true;
		}
		return false;
	}
	
	protected int getConnSize() {
		return connMap.size();
	}

	protected synchronized Object getConnection(UrlDs _urlDs) {
		return connMap.get(_urlDs.getConnString());
	}
	
	private synchronized void fireEvent(TransactionEventType _event) {
		if(eventHandlers==null || eventHandlers.isEmpty())
			return;
		
		eventHandlers.stream().filter(handler->handler.isEvent(_event)).forEach(handler->{
			handler.process();
		});
	}
	
	protected synchronized <T> void addEventListener(TransactionEventType _event, TransactionEventHandler<T> _handler) {
		if(eventHandlers==null)
			eventHandlers= new CopyOnWriteArrayList<>();
		eventHandlers.add(new EventListener<>(_event, _handler));
	}
	
	protected synchronized <T> void addEventListener(TransactionEventType _event, TransactionEventHandler<T> _handler
			, T _data) {
		if(eventHandlers==null)
			eventHandlers= new CopyOnWriteArrayList<>();
		eventHandlers.add(new EventListener<>(_event, _handler, _data));
	}
	
	// -------------------------------------------------------------------------------
	private class EventListener<T> {
		TransactionEventType event;
		TransactionEventHandler<T> handler;
		T data;
		public EventListener(TransactionEventType event, TransactionEventHandler<T> handler, T data) {
			this.event = event;
			this.handler = handler;
			this.data = data;
		}
		public EventListener(TransactionEventType event, TransactionEventHandler<T> handler) {
			this(event, handler, null);
		}
		
		boolean isEvent(TransactionEventType _event) {
			return event.equals(_event);
		}
		
		void process() {
			handler.process(event, data);
		}
	}
	
	public String getId() {
		return id;
	}
	
	/** 是否已沒有交易範疇(遞迴) */
	protected synchronized boolean isNonTransactionChain() {
		if(transactionChain ==0)
			return true;
		return false;
	}
	
	/** 是否在正常交易狀態(交易遞迴數必須>=0) */
	protected boolean isRegular() {
		return transactionChain >= 0;
	}
	
	/** 加入連線物件至該交易控管 */
	protected synchronized void putConnection(UrlDs _urlDs, TransactionConnection _conn) {
		if(_conn!=null)
			connMap.put(_urlDs.getConnString(), _conn);
	}

	public int transactionChain() {
		return transactionChain;
	}

	public long getCreateTime() {
		return createTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public CopyOnWriteArrayList<StackTraceElement[]> getTransactionTraces() {
		return transactionTraces;
	}
	
	
}
