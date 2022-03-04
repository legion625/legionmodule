package legion.kmeans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Kmeans {
	/* input */
	private List<Point> pointList;
	private int k; // k:群數
	
	/* data */
	private int dim; // dimension
	
	/* result */
	private List<Cluster> clusterList; 
	private double rmsstd, rs, rsRmsstdRatio;
	private double a,b,s;
	

	// -------------------------------------------------------------------------------
	public void init(List<Point> pointList, int k) {
		if (pointList.size() <= 1 || k <= 1)
			return;
		this.pointList = pointList;
		this.k = k;

		
		Point p0 = pointList.get(0);
		if (!pointList.parallelStream().allMatch(p -> p.v.length == p0.v.length)) {
			System.out.println("error");
			return;
		}
		dim = p0.v.length;
		
		
		/* 初始化群中心 */
		
		// 初始化K個群
		clusterList = new ArrayList<>();
		List<Point>[] tmpLists = new List[k];
		for(int i=0;i<k;i++)
			tmpLists[i] = new ArrayList<>();
		for (int i = 0; i < pointList.size(); i++) {
			tmpLists[i % k].add(pointList.get(i));
		}
		for(int i=0;i<k;i++) {
			List<Point> list = tmpLists[i];
//			System.out.println("list.size():\t"+list.size());
//			double[] center = new double[dim];
//			for (int j = 0; j < dim; j++) {
//				final int theJ = j;
//				center[j] = list.parallelStream().mapToDouble(p -> p.v[theJ]).average().getAsDouble();
//			}
//			double[] center = getCenter(list);
//			System.out.println(i+"\t"+center.length);
//			for(double d:center) 
//				System.out.print(d+" ");
//			clusterList.add(new Cluster(i, center));
			clusterList.add(new Cluster(i, list));
		}
		
		
		// 分群
		do {
			cluster(clusterList, pointList); // 分群
		} while (!checkStopCriteria());
		
		
		/* 計算RMSSTD */
		double[] totalCenter = getCenter(pointList);
		// 所有點和所有群體中心點的距離加總
//		double sst = pointList.parallelStream().mapToDouble(p -> getDis(p.v, totalCenter)).sum();
		double sst = pointList.parallelStream().mapToDouble(p ->Math.pow(getDis(p.v, totalCenter), 2) ).average().getAsDouble();
//		double sst = pointList.parallelStream().mapToDouble(p ->Math.pow(getDis(p.v, totalCenter), 2)/ ((double)pointList.size())).sum();
//		double sst = pointList.parallelStream().mapToDouble(p ->getDisSquare(p.v, totalCenter)).average().getAsDouble();
		
		// 所有群中心和群體中心點的距離加總
//		double ssr = clusterList.stream().mapToDouble(c->getDis(c.center, totalCenter)).sum();
		double ssr = clusterList.stream().mapToDouble(c->Math.pow(getDis(c.center, totalCenter),2) ).average().getAsDouble();
//		double ssr = clusterList.stream().mapToDouble(c->Math.pow(getDis(c.center, totalCenter),2)/ ((double)k) ).sum();
//		double ssr = clusterList.stream().mapToDouble(c->getDisSquare(c.center, totalCenter)).average().getAsDouble();
		// 所有點和所屬群中心點的距離加總
		double sse = clusterList.stream().mapToDouble(   c-> {
			double[] cc = c.center;
//			return c.getPointList().parallelStream().mapToDouble(p -> getDis(p.v, cc)).sum();
			return c.getPointList().parallelStream().mapToDouble(p -> Math.pow(getDis(p.v, cc),2)).sum();
//			return c.getPointList().parallelStream().mapToDouble(p -> getDisSquare(p.v, cc)).sum();
		}).sum();
		sse = sse/((double)pointList.size());
		System.out.println("sst:\t"+sst);
		System.out.println("ssr:\t"+ssr);
		System.out.println("sse:\t" + sse);
		rs = ssr / sst;
		rmsstd = Math.sqrt(sse / k);
		rsRmsstdRatio = rs / rmsstd;
		System.out.println("rs:\t"+rs);
		System.out.println("rmsstd:\t"+rmsstd);
		System.out.println("rsRmsstdRatio:\t" + rsRmsstdRatio);

		
		/* 計算Avg. Silhouette Method */
		// a.所有的組內平均距離
		a = 0;
		int cntA = 0;
		for(int i=0;i<k;i++) {
			Cluster c = clusterList.get(i);
			List<Point> list = c.getPointList();
			int s = list.size();
			for (int x = 0; x < s - 1; x++) {
				for(int y=x;y<s;y++) {
					a+=getDis(list.get(x).v, list.get(y).v);
					cntA++;
				}
			}
		}
		a = a / (double) cntA;
		// b.所有的組外平均距離
		b = 0;
		double[] b_c =new double[k]; // 各群的群外平均
		for (int i = 0; i < k; i++) {
			final int thisClusterIdx   = i;
			Cluster thisCluster = clusterList.get(thisClusterIdx);
			List<Cluster> otherClusterList = clusterList.stream().filter(c -> c.getIdx() != thisClusterIdx)
					.collect(Collectors.toList());

			double x = 0,y=0;
			for(Cluster c: otherClusterList) {
				x += getDis(thisCluster.center, c.center)*((double)c.getCnt());
				y+= c.getCnt();
			}
			b_c[i] = x/y;
		}
		
		{
			double x = 0,y=0;
//			for(b_c)
			for(int i=0;i<k;i++) {
				Cluster thisCluster = clusterList.get(i);
				x+=((double)thisCluster.getCnt())*b_c[i];
						y+=thisCluster.getCnt();
			}
			b = x/y;
		}
		
		
		System.out.println("a:\t"+a);
		System.out.println("b:\t"+b);
		s = (b-a)/Math.max(a, b);
		System.out.println("c:\t"+s);
	}
	
	
	private void cluster(List<Cluster> _clusterList, List<Point> _pointList) {
		// 所有的cluster把pointList移到pointList0。
		for (Cluster c : _clusterList) {
//			c.pointList0.clear();
//			c.pointList0.addAll(c.pointList);
//			c.pointList.clear();
			c.moveCenter();
		}
		
		// 重新分群
		List<Point> pointList = new ArrayList<>(_pointList);
		for(Point p: pointList) {
			double targetDis = Double.MAX_VALUE;
			Cluster targetC = null;
			for (Cluster c : _clusterList) {
//				System.out.println("c.center:\t"+c.center);
//				System.out.println("p.v:\t"+ p.v);
				double thisDis = getDis(c.center, p.v);
				if (thisDis < targetDis) {
					targetDis = thisDis;
					targetC = c;
				}
			}
			targetC.pointList.add(p);
		}
	}
	
	private boolean checkStopCriteria() {
		return clusterList.stream().allMatch(c->{
			List<Point> list0 = c.pointList0.stream().sorted(Comparator.comparing(p->p.idx)).collect(Collectors.toList());
			List<Point> list = c.pointList.stream().sorted(Comparator.comparing(p->p.idx)).collect(Collectors.toList());
			if (list0.size() != list.size())
				return false;
			int size = list0.size();
			for(int i=0;i<size;i++) {
				if(list0.get(i).idx!=list.get(i).idx)
					return false;
			}
			return true;
		});
	}
	
	private double[] getCenter(List<Point> _pointList) {
		if (_pointList == null || _pointList.size() <= 0)
			return null;
		double[] newCenter = new double[dim];
		for (int i = 0; i < dim; i++) {
			final int ii = i;
			newCenter[i] = _pointList.parallelStream().mapToDouble(p -> p.v[ii]).average().getAsDouble();
		}
		return newCenter;
	}
	
//	private double getDisSquare(double[] _v1, double[] _v2) {
//		if (_v1.length != _v2.length)
//			return Double.NaN;
//		int n = _v1.length;
//
//		double sum = 0;
//		for (int i = 0; i < n; i++) {
//			sum += Math.pow(_v2[i] - _v1[i], 2);
//		}
//		return sum;
//	}
	
	private double getDis(double[] _v1, double[] _v2) {
//		System.out.println(_v1+"\t"+_v2);
		if (_v1.length != _v2.length)
			return Double.NaN;
		int n = _v1.length;

		double sum = 0;
		for (int i = 0; i < n; i++) {
			sum += Math.pow(_v2[i] - _v1[i], 2);
		}
		double d = Math.sqrt(sum);
		return d;
	}
	
	
	
	public class Cluster{
		private int idx;
		private double[] center;
		private List<Point> pointList0; // 上一個iteration的點
		private List<Point> pointList; // 當前iteration的點
		
		private Cluster(int idx, List<Point> pointList) {
			this.idx = idx;
			this.center = null;
			this.pointList0 = new ArrayList<>();
			this.pointList = pointList;
		}
		
		private void moveCenter() {
			center = getCenter(pointList);
			
			//
			pointList0.clear();
			pointList0.addAll(pointList);
			pointList.clear();
		}

		public int getIdx() {
			return idx;
		}

		public List<Point> getPointList() {
			return pointList;
		} 
		
		public int getCnt() {
			return getPointList().size();
		}
		
		
		
	}
	
	public class Point{
		private int idx;
		private double[] v;

		public Point(int idx, double[] v) {
			this.idx = idx;
			this.v = v;
		}

		public int getIdx() {
			return idx;
		}

		public double[] getV() {
			return v;
		}
		
		
	}
	
	
	// -------------------------------------------------------------------------------
	// ------------------------------------result-------------------------------------
	public List<Cluster> getClusterList(){
		return clusterList;
	}

	public double getRmsstd() {
		return rmsstd;
	}

	public double getRs() {
		return rs;
	}

	public double getRsRmsstdRatio() {
		return rsRmsstdRatio;
	}


	public double getA() {
		return a;
	}


	public double getB() {
		return b;
	}

	public double getS() {
		return s;
	}
	
	
	
}
