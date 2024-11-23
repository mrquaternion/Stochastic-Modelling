package mesdevoirs;

import java.util.TreeMap;
import java.util.Map;

import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.rng.MRG32k3a;
import umontreal.ssj.rng.RandomStream;

public class NumeroUn {
	private static double lambdaOriginal = 0.5;
	private static double b = 15;
	final static int nbIters = 1000;
	
	RandomStream generateur;
	ExponentialDist X1Original, X2Original, X3Original;
    ExponentialDist X1IS, X2IS;
	
	public NumeroUn() {
		generateur = new MRG32k3a();
		X1Original = new ExponentialDist(lambdaOriginal);
        X2Original = new ExponentialDist(lambdaOriginal);
        X3Original = new ExponentialDist(lambdaOriginal);
	}
	
	private double calculerSansIS() {
		double x1 = X1Original.inverseF(generateur.nextDouble());
		double x2 = X2Original.inverseF(generateur.nextDouble());
		double x3 = X3Original.inverseF(generateur.nextDouble());
		
		if (x1 + x2 + x3 > b) {
			return 1;
		}
		return 0;
	}
	
	private double calculerAvecIS(double lambdaIS) {
        X1IS = new ExponentialDist(lambdaIS);
        X2IS = new ExponentialDist(lambdaIS);
		double x1 = X1IS.inverseF(generateur.nextDouble());
		double x2 = X2IS.inverseF(generateur.nextDouble());

		double L = calculerFacteurVraisemblance(x1, x2, lambdaIS);
	    
		if (x1 + x2 <= b) {
			return L * Math.exp(-lambdaOriginal * (b - x1 - x2));
		}
		return L;
	}
	
	private double calculerFacteurVraisemblance(double x1, double x2, double lambdaIS) {
		double pi1_x1 = lambdaOriginal * Math.exp(-lambdaOriginal * x1);
	    double pi2_x2 = lambdaOriginal * Math.exp(-lambdaOriginal * x2);
	    double g1_x1 = lambdaIS * Math.exp(-lambdaIS * x1);
	    double g2_x2 = lambdaIS * Math.exp(-lambdaIS * x2);
	    
	    return (pi1_x1 / g1_x1) * (pi2_x2 / g2_x2);
	}
	
	private static double calculerMoyenne(double somme) {
		return somme / nbIters;
	}
	
	private static double calculerVariance(double[] valeurs, double moyenne) {
	    double somme = 0;
	    for (double valeur : valeurs) {
	        somme += Math.pow(valeur - moyenne, 2);
	    }
	    return somme / valeurs.length;
	}
	
	private static double calculerBorneInf(double moyenne, double variance) {
		return moyenne - (1.96 * Math.sqrt(variance / nbIters));
	}
	
	private static double calculerBorneSup(double moyenne, double variance) {
		return moyenne + (1.96 * Math.sqrt(variance / nbIters));
	}
	
	public static void main(String[] args) {
		// Sans Importance Sampling
		NumeroUn simulation1 = new NumeroUn();
		double somme1 = 0;
		double[] resultatsSansIS = new double[nbIters];
		for (int i = 0; i < nbIters; i++) {
			resultatsSansIS[i] += simulation1.calculerSansIS();
			somme1 += resultatsSansIS[i];
		}
		double moyenneSansIS = calculerMoyenne(somme1);
		double varianceSansIS = calculerVariance(resultatsSansIS, moyenneSansIS);
		double borneInfSansIS = calculerBorneInf(moyenneSansIS, varianceSansIS);
		double borneSupSansIS = calculerBorneSup(moyenneSansIS, varianceSansIS);
		
		System.out.println("Estimateur sans IS :");
		System.out.printf("L'estimation de P[Y > 15] est de %.4f et sa variance est de %.4f \n", moyenneSansIS, varianceSansIS);
		System.out.printf("Intervalle de confiance : [%.4f, %.4f] \n \n", borneInfSansIS, borneSupSansIS);
		
		
		// Avec Importance Sampling
		NumeroUn simulation2 = new NumeroUn();
		double somme2 = 0;
		double lambdaIS = 0.2;
		double[] resultatsAvecIS = new double[nbIters];
		for (int i = 0; i < nbIters; i++) {
			resultatsAvecIS[i] += simulation2.calculerAvecIS(lambdaIS);
			somme2 += resultatsAvecIS[i];
		}
		double moyenneAvecIS = calculerMoyenne(somme2);
		double varianceAvecIS = calculerVariance(resultatsAvecIS, moyenneAvecIS);
		double borneInfAvecIS = calculerBorneInf(moyenneAvecIS, varianceAvecIS);
		double borneSupAvecIS = calculerBorneSup(moyenneAvecIS, varianceAvecIS);
		
		System.out.println("Estimateur avec IS :");
		System.out.printf("L'estimation de P[Y > 15] est de %.4f et sa variance est de %.4f \n", moyenneAvecIS, varianceAvecIS);
		System.out.printf("Intervalle de confiance : [%.4f, %.4f] \n \n", borneInfAvecIS, borneSupAvecIS);
		
		
		double lambdaISDepart = 4.0;
		double lambdaISFin = 10.0;
		double step = 0.01;
		int iters = 100000;
		Map<Double, Double> lambdas = new TreeMap<>();
		int nbSteps = (int) ((lambdaISFin - lambdaISDepart) / step); // Nombre total d'étapes
		for (int k = 0; k <= nbSteps; k++) {
		    double i = lambdaISDepart + k * step; // Calcul direct de la valeur actuelle
		    i = Math.round(i * 10.0) / 10.0; // Arrondi à 1 décimale
			
			NumeroUn simulation3 = new NumeroUn();
			resultatsAvecIS = new double[iters];
			somme2 = 0;
			
			for (int j = 0; j < iters; j++) {
				resultatsAvecIS[j] += simulation3.calculerAvecIS(i);
				somme2 += resultatsAvecIS[j];
			}
			
			moyenneAvecIS = calculerMoyenne(somme2);
			varianceAvecIS = calculerVariance(resultatsAvecIS, moyenneAvecIS);
			
			lambdas.put(i, varianceAvecIS);
		}
		
		// Initialisation correcte du premier ratio
		double lambdaOptimiser = lambdaISDepart;
		double ratioVariancePre = lambdas.get(lambdaOptimiser);

		for (Map.Entry<Double, Double> entry : lambdas.entrySet()) {
		    Double cle = entry.getKey();
		    Double valeur = entry.getValue();
		    //"Lambda : %.2f, Ratio de la variance : %.6f \n", 
		    System.out.println(cle + "," + valeur);
		    
		    if (valeur < ratioVariancePre) {
		        lambdaOptimiser = cle;
		        ratioVariancePre = valeur; // Mise à jour correcte
		    }
		}

		System.out.printf("Lambda optimisé : %.2f", lambdaOptimiser);
	}
	
}

