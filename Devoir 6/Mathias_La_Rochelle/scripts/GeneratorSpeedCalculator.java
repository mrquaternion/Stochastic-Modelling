package mesdevoirs;

import umontreal.ssj.probdist.PoissonDist;
import umontreal.ssj.rng.MRG32k3a;
import java.lang.Math;
import java.util.HashMap;
import java.util.Map;

public class GeneratorSpeedCalculator {
	static final int LAMBDA = 25;
	static final int NB_INTERVALS = 32;
	static final double N = Math.pow(10, 6) + 1;
	static MRG32k3a generator1 = new MRG32k3a();
	static MRG32k3a generator2 = new MRG32k3a();
	static long diff;
	static Map<Integer, Integer> frequencies = new HashMap<>();

	public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 2; i++) {
            iterate(i);

            builder.setLength(0);
            builder.append("Temps d'exécution : ")
                   .append(diff)
                   .append(" ms");
            System.out.println(builder);

            System.out.println("Fréquence de la méthode " + (i + 1) + " : ");
            for (Map.Entry<Integer, Integer> entry : frequencies.entrySet()) {
            	builder.setLength(0);
                if (entry.getKey() >= 0 && entry.getKey() <= 50) {
                	builder.append(entry.getKey())
                		   .append(",")
                		   .append(entry.getValue());
                    System.out.println(builder);
                }
            }

            System.out.print("\n");
            frequencies.clear();
        }
    }
	
	static void iterate(int i) {
	    long start = System.nanoTime(); // Start timing before the loop
	    
	    switch(i) {
	        case (0):
	            for (int j = 0; j < N; j++) {
	                double currentU = generator1.nextDouble();
	                int X = sequentialSearch(currentU);
	                frequencies.put(X, frequencies.getOrDefault(X, 0) + 1);
	            }
	            break;
	            
	        case (1):
	            double[] F = new double[50]; 
	            for (int k = 0; k < F.length; k++) {
	                F[k] = PoissonDist.cdf(LAMBDA, k);
	            }
	        
	            for (int j = 0; j < N; j++) {
	                double currentU = generator2.nextDouble();
	                int X = indexSearch(currentU, F);
	                frequencies.put(X, frequencies.getOrDefault(X, 0) + 1);
	            }
	            break;
	    }
	    
	    long end = System.nanoTime(); 
	    diff = (end - start) / 1000000; // ms
	}
	
	// Méthode en c)
    static int indexSearch(double U, double[] F) {
        int s = (int) (NB_INTERVALS * U);
        int i_s = find(s, F);
        int i = i_s;

        while (i < F.length - 1 && F[i] < U) {
            i++;
        }

        return i;
    }

    static int find(int s, double[] F) {
        double limite = ((double) s) / NB_INTERVALS;
        for (int i = 0; i < F.length; i++) {
            if (F[i] >= limite) {
                return i;
            }
        }
        return F.length - 1;
    }
	
	// Méthode en b)
    static int sequentialSearch(double U) {
        if (LAMBDA <= 0 || U <= 0 || U >= 1) {
            throw new IllegalArgumentException();
        }

        int X = 0;
        double cumulativeProbability = PoissonDist.cdf(LAMBDA, X);

        while (cumulativeProbability < U) {
            X++;
            cumulativeProbability = PoissonDist.cdf(LAMBDA, X);
        }

        return X;
    }
}
