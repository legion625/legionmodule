package legion.geneticAlgorithm;

import java.util.Random;

public enum MutationType {
	INVERSION;

	public Gene run(Gene _g) {
		switch (this) {
		case INVERSION:
			return runInversion(_g);
		default:
			return null;
		}
	}

	/**
	 * (p1,p2) = (1,4)
	 * 0 1 2 3 4 
	 *   ^ ^ ^
	 * 0 3 2 1 4
	 * @param _g
	 */
	private Gene runInversion(Gene _g) {
		int p1=-1, p2=-1;
		
		if(_g.getLength()<2)
			return null;
		
		//
		Random rand = new Random(System.currentTimeMillis());
		p1 = rand.nextInt(_g.getLength());
		do {
			p2 = rand.nextInt(_g.getLength());
		} while (p1 == p2);
		
		//
		int[] newGene = new int[_g.getLength()];
		for(int j=0;j<Math.min(p1, p2);j++) 
			newGene[j] = _g.getChromosome()[j];
		for(int j=Math.max(p1,p2);j<_g.getLength();j++)
			newGene[j] = _g.getChromosome()[j];
		
		int tempPosition = Math.max(p1, p2) - 1;
		for (int j = Math.min(p1, p2); j < Math.max(p1, p2); j++) {
			newGene[j] = _g.getChromosome()[tempPosition];
		}
		
		//
		return new Gene(newGene);
	}
}
