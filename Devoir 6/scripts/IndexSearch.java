package mesdevoirs;

import umontreal.ssj.probdist.PoissonDist;
import umontreal.ssj.rng.MRG32k3a;

public class IndexSearch {
	static final int LAMBDA = 25;
	static final int NB_INTERVALS = 32;
	static final double[] F; 

    static {
        F = new double[50]; 
        for (int i = 0; i < F.length; i++) {
            F[i] = PoissonDist.cdf(LAMBDA, i);
        }
    }

	public static void main(String[] args) {
		MRG32k3a generator = new MRG32k3a();
		double U = generator.nextDouble();
		
		int s = (int) (NB_INTERVALS * U); // Casting sur entier
										  // agit comme un floor
		
		int i_s = find(s);
		
		int i = i_s;
        while (F[i] < U && i < F.length - 1) {
            i++;
        }
        
        System.out.println("Valeur de U : " + U);
        System.out.println("Indice trouvé : " + i);
	}
	
	static int find(int s) {
		double limite = (double) (s / NB_INTERVALS);
        for (int i = 0; i < F.length; i++) {
            if (F[i] >= limite) {
                return i;
            }
        }
        
        return F.length - 1; 
	}
}
