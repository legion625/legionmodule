package legion.geneticAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneticAlgorithm {

	private int popSize;
	private int chromosomeLength;
	private double crossoverRate;
	private double mutationRate;
	
	private Function<Gene, PerformanceAbs> fnGetPerformanceAbs;
	

	public GeneticAlgorithm(int popSize, int chromosomeLength, Function<Gene, PerformanceAbs> fnGetPerformanceAbs) {
		this.popSize = popSize;
		this.chromosomeLength = chromosomeLength;
		this.crossoverRate = 0.8;
		this.mutationRate = 0.05;
		this.fnGetPerformanceAbs = fnGetPerformanceAbs;
	}
	
	public IterationAbs[] runAlgorithm(int _iterationLimit) {
		return runAlgorithm(CrossoverType.ONE_POINT_CUT, MutationType.INVERSION, _iterationLimit);
	}

	public IterationAbs[] runAlgorithm(CrossoverType _crossoverType, MutationType _mutationType, int _iterationLimit) {
		// 產生初始基因
		List<Gene> geneList0 = Arrays.asList(Gene.initBinaryCromosomes(popSize, chromosomeLength));

		// Do Iteration
		IterationAbs[] iterationAbss = new IterationAbs[_iterationLimit + 1];
		iterationAbss[0] = parseIterationAbs(geneList0);
		int count = 0;
		List<Gene> geneList =geneList0; 
		while (count < _iterationLimit) {
			count++;
			iterationAbss[count] = doOneIteration(geneList, _crossoverType, _mutationType);
			geneList = iterationAbss[count].getGeneList();
		}
		return iterationAbss;
	}
	
	
	private IterationAbs doOneIteration(List<Gene> _parentList, CrossoverType _crossoverType, MutationType _mutationType) {
		
		Random rand = new Random(System.currentTimeMillis());
		
		List<Gene> thisIterationList = new ArrayList<>();
		// add parentList
		thisIterationList.addAll(_parentList);
//		System.out.println("_parentList.size(): " + _parentList.size());
		/* crossover */
		List<Gene> tempParentList = new ArrayList<>(_parentList);
		int crossoverTimes = (int) (((double) popSize) * crossoverRate / 2.0);
		for (int i = 0; i < crossoverTimes; i++) {
			int p1 = rand.nextInt(tempParentList.size());
			int p2 = -1;
			do {
				p2 = rand.nextInt(tempParentList.size());
			} while (p1 == p2);
			// add children made by crossover to the childList
			Gene g1 = _parentList.get(p1);
			Gene g2 = _parentList.get(p2);
			
			List<Gene> crossoverChildList = _crossoverType.run(g1,g2);
			if (crossoverChildList != null)
				thisIterationList.addAll(crossoverChildList);
			
			tempParentList.remove(g1);
			tempParentList.remove(g2);
			
//			System.out.println("crossoverChildList.size(): " + crossoverChildList.size());
		}
		
		/* mutation */
		List<Gene> mutationList = new ArrayList<>();
		for (Gene g : thisIterationList) {
			if (rand.nextDouble() <= mutationRate) {
				Gene mutationG = _mutationType.run(g);
				if (mutationG != null)
					mutationList.add(mutationG);
			}
		}
		thisIterationList.addAll(mutationList);
		
		/* choose survivors */
		return parseIterationAbs(thisIterationList);
	}
	
	private IterationAbs parseIterationAbs(List<Gene> _geneList) {
		for (Gene c : _geneList) {
			c.setPerfAbs(fnGetPerformanceAbs.apply(c));
		}
		
		// 依目標函數由小到大排序。
		List<Gene> geneList = _geneList.stream().sorted(Comparator.comparingDouble(Gene::getObjectiveValue))
				.collect(Collectors.toList());
		Gene geneIterationBest = geneList.get(0);
		double avgObj = _geneList.parallelStream().mapToDouble(Gene::getObjectiveValue).average().orElse(Double.NaN);
		
		return new IterationAbs(geneIterationBest, avgObj, geneList.subList(0, popSize));
	}
}
