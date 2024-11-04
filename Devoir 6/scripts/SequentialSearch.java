package mesdevoirs;

import umontreal.ssj.probdist.PoissonDist;
import java.util.Scanner;

public class SequentialSearch {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		StringBuilder builder = new StringBuilder();
		double[] params = getParams(builder, scanner);
		
		builder.setLength(0);
		
		if (params[0] > 0){
			int X = inversePoissonCDF(params[0], params[1]);
			
			builder.append("La valeur X obtenue est de ")
				   .append(X);
		} else {
			builder.append("La valeur du lambda doit ")
				   .append("être positive.");
		}
		
		System.out.print(builder);
	}
	
	static double[] getParams(StringBuilder builder, Scanner scanner) {
		builder.append("Entrez la moyenne de ")
		   	   .append("votre distribution de Poisson :");
	
		System.out.println(builder.toString());
		double lambda = scanner.nextDouble();
		
		builder.setLength(0);
		
		builder.append("Entrez votre probabilité entre ")
		       .append("0 et 1 :");
		
		System.out.println(builder.toString());
		double U = scanner.nextDouble();
		
		return new double[] {lambda, U};
	}
	
	static int inversePoissonCDF(double lambda, double U) {
		if (lambda <= 0 || U <= 0 || U >= 1) {
            throw new IllegalArgumentException();
        }

        int X = 0;
        double cumulativeProbability = PoissonDist.cdf(lambda, X);

        // Recherche séquentielle
        while (cumulativeProbability < U) {
            X++;
            cumulativeProbability = PoissonDist.cdf(lambda, X);
        }
        
        return X;
	}
}
