package legion.geneticAlgorithm;

import java.util.Random;

public class Gene {
	
	private int[] chromosome;
	private PerformanceAbs perfAbs;
	
	Gene(int[] chromosome) {
		this.chromosome = chromosome;
	}

	// -------------------------------------------------------------------------------
	public int getLength() {
		return chromosome.length;
	}

	public int[] getChromosome() {
		return chromosome;
	}

	void setChromosome(int[] gene) {
		this.chromosome = gene;
	}
	
	public PerformanceAbs getPerfAbs() {
		return perfAbs;
	}

	void setPerfAbs(PerformanceAbs perfAbs) {
		this.perfAbs = perfAbs;
	}
	
	public double getObjectiveValue() {
		return getPerfAbs()==null?Double.NaN:getPerfAbs().getObjectiveValue();
	}
	
	// -------------------------------------------------------------------------------
	public static Gene[] initBinaryCromosomes(int _popSize, int _length) {
		if (_popSize <= 0 || _length <= 1) {
			return null;
		}

		Gene[] chromosomes = new Gene[_popSize];

		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < _popSize; i++) {
			int[] gene = new int[_length];
			for (int j = 0; j < _length; j++) {
				gene[j] = rand.nextBoolean() ? 1 : 0;
			}
			chromosomes[i] = new Gene(gene);
		}

		return chromosomes;
	}

}
