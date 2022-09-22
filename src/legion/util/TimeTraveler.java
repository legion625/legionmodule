package legion.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeTraveler {
	private Logger log = LoggerFactory.getLogger(TimeTraveler.class);

	private List<Site> siteList;
	
	public TimeTraveler() {
		siteList = new ArrayList<>();
	}
	
	public int getSiteCount() {
		return siteList.size();
	}
	
	public synchronized void addSite(String desp, BooleanSupplier process) {
		siteList.add(new Station(desp, process));
	}
	
	/**
	 * 用法：當想要標記一連串的Station時，在第一個Station加入Site前，type設為1；所有的Station都加入後，type設為2. 
	 * 在travel時反向拜訪所有Site，於是在遇到'2'時輸出為'Start，在遇到'1'時輸出為'End'。
	 * @param type
	 * @param desp
	 */
	public synchronized void addSite(int type, String desp) {
		siteList.add(new Flag(type, desp));
	}
	
	public void copySitesFrom(TimeTraveler _tt) {
		siteList.addAll(_tt.siteList);
	}
	
	public void travel() {
		if (siteList == null)
			return;
		int all = siteList.size();
		while (siteList.size() > 0) {
			int lastIdx = siteList.size() - 1;
			Site site = siteList.remove(lastIdx);
			boolean r = site.process();
			if (r)
				log.info("[{}] - {}", all - siteList.size(), site.getDesp());
			else
				log.error("[{}] - {}", all - siteList.size(), site.getDesp());
		}
	}
	
	// -------------------------------------------------------------------------------
	private interface Site {
		boolean process();

		String getDesp();
	}

	private class Station implements Site {
		private String desp;
		private BooleanSupplier process;

		private Station(String desp, BooleanSupplier process) {
			this.desp = desp;
			this.process = process;
		}

		@Override
		public boolean process() {
			return process.getAsBoolean();
		}

		@Override
		public String getDesp() {
			return "process success. " + desp;
		}
	}

	private class Flag implements Site {
		private int type;
		private String desp;

		private Flag(int type, String desp) {
			this.type = type;
			this.desp = desp;
		}

		@Override
		public boolean process() {
			return true;
		}

		@Override
		public String getDesp() {
			return (type == 2 ? "Start::" : "End::") + desp;
		}

	}
}
