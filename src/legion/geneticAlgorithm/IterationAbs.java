package legion.geneticAlgorithm;

import java.util.List;

public class IterationAbs {
	private Gene bestGene;
	private double avgObjValue;
	
	private List<Gene> geneList;
	
	IterationAbs(Gene geneBest, double avgObjValue, List<Gene> geneList) {
//	IterationAbs(Gene geneBest, double avgObjValue) {
		this.bestGene = geneBest;
		this.avgObjValue = avgObjValue;
		this.geneList = geneList;
	}

	public Gene getGeneBest() {
		return bestGene;
	}

	public double getAvgObjValue() {
		return avgObjValue;
	}

	public List<Gene> getGeneList() {
		return geneList;
	}
	
	
	
}
