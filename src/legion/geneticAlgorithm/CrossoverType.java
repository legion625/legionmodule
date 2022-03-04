package legion.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum CrossoverType {
	ONE_POINT_CUT;

	public List<Gene> run(Gene _g1, Gene _g2) {
		switch (this) {
		case ONE_POINT_CUT:
			return runOnePointCut(_g1, _g2);
		default:
			return null;
		}
	}

	private List<Gene> runOnePointCut(Gene _g1, Gene _g2) {
		int length = Math.min(_g1.getLength(), _g2.getLength());
		if (length < 1)
			return null;

		List<Gene> childList = new ArrayList<>();
		int cut = new Random(System.currentTimeMillis()).nextInt(length);

		int[] newChromosome1 = new int[_g1.getLength()];
		int[] newChromosome2 = new int[_g2.getLength()];

		for (int j = 0; j < _g1.getLength(); j++)
			newChromosome1[j] = j < cut ? _g1.getChromosome()[j] : _g2.getChromosome()[j];
		for (int j = 0; j < _g2.getLength(); j++)
			newChromosome2[j] = j < cut ? _g2.getChromosome()[j] : _g1.getChromosome()[j];

		childList.add(new Gene(newChromosome1));
		childList.add(new Gene(newChromosome2));
		return childList;
	}

}
