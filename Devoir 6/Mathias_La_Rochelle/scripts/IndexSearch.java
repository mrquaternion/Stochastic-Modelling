package mesdevoirs;

import java.util.Scanner;
import umontreal.ssj.probdist.PoissonDist;

public class IndexSearch {
	static final int LAMBDA = 25;
	static final int NB_INTERVALS = 32;
	static final double[] F; 

    static {
        F = new double[51]; 
        for (int i = 0; i < F.length; i++) {
            F[i] = PoissonDist.cdf(LAMBDA, i);
        }
    }

	public static void main(String[] args) {
		StringBuilder builder = new StringBuilder();
		Scanner scanner = new Scanner(System.in);
		
		builder.append("Entrez votre probabilité entre ")
	       	   .append("0 et 1 :");
		System.out.println(builder);
		
		double U = scanner.nextDouble();
		
		if (U <= 0 || U >= 1) {
            throw new IllegalArgumentException();
        }
		
		int s = (int) (NB_INTERVALS * U); // Casting sur entier
										  // agit comme un floor
		int i_s = find(s);
		
		int i = i_s;
        while (F[i] < U && i < F.length - 1) {
            i++;
        }
        
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
