package mesdevoirs;

import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.rng.MRG32k3a;
import umontreal.ssj.rng.RandomStream;

public class NumeroDeux {
	final double mu1 = 2.9 * Math.pow(10, 7);
	final double sigma1 = 1.45 * Math.pow(10, 6);
	final double mu2 = 500;
	final double sigma2 = 100;
	final double mu3 = 1000;
	final double sigma3 = 100;
	final static int nbIters = 1000;
	final int x = 5;
	double w = 4;
	double t = 2;
	double kappa = 5 * Math.pow(10, 5);
	
	RandomStream generateurMC, generateurCMC;
	NormalDist Y1, Y2, Y3;
	
	public NumeroDeux(RandomStream stream) {
		// IRN donc generateurs differents (U_j)
		this.generateurMC = stream;
		this.generateurCMC = stream;
		this.Y1 = new NormalDist(mu1, sigma1);
		this.Y2 = new NormalDist(mu2, sigma2);
        this.Y3 = new NormalDist(mu3, sigma3);
	}
	
	// ######## a) ########
	private int generationMC() {
		double valeur1 = Y1.inverseF(generateurMC.nextDouble());
		double valeur2 = Y2.inverseF(generateurMC.nextDouble());
		double valeur3 = Y3.inverseF(generateurMC.nextDouble());
		
		double X = fonctionX(valeur1, valeur2, valeur3);
		if (X <= x) {
			return 1;
		}
		return 0;
	}
	
	private double generationCMC() {
		NormalDist normalDist = new NormalDist();
		double valeur2 = Y2.inverseF(generateurCMC.nextDouble());
		double valeur3 = Y3.inverseF(generateurCMC.nextDouble());
		
		double W1 = fonctionX(x, valeur2, valeur3);
		return 1 - normalDist.cdf((W1 - mu1) / sigma1);
	}
	
	private double fonctionX(double v1, double v2, double v3) {
		return (kappa / v1) * Math.sqrt((Math.pow(v2, 2) / Math.pow(w, 4)) 
				+ (Math.pow(v3, 2) / Math.pow(t, 4)));
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
	
	
	private void estimerDeriveeIRNMC(double delta) {
		MRG32k3a baseStream = new MRG32k3a();
		NumeroDeux simulation = new NumeroDeux(baseStream);
	    double somme = 0; 
	    double[] resultatsIRNMC = new double[nbIters];
	    NormalDist ogNormalDist = new NormalDist(mu3, sigma3);
	    NormalDist newNormalDist = new NormalDist(mu3, sigma3 + delta);
	    
	    for (int i = 0; i < nbIters; i++) {
	        // Première simulation avec sigma3
	    	simulation.Y3 = ogNormalDist;
	        double X1 = this.generationMC();

	        // Deuxième simulation avec sigma3 + delta
	        simulation.Y3 = newNormalDist;
	        double X2 = this.generationMC();

	        // Calcul de la dérivée pour cette itération
	        double derivee = (X2 - X1) / delta;
	        resultatsIRNMC[i] = derivee;
	        somme += derivee;
	    }
	    
	    // Calcul de la moyenne et de la variance
	    double moyenne = calculerMoyenne(somme);
	    double variance = calculerVariance(resultatsIRNMC, moyenne);
	    double borneInf = calculerBorneInf(moyenne, variance);
	    double borneSup = calculerBorneSup(moyenne, variance);

	    // Afficher les résultats
	    System.out.printf("Moyenne IRN MC : %.4f, Variance : %.4f, Intervalle de confiance : [%.4f, %.4f]\n", moyenne, variance, borneInf, borneSup);
	}

	
	private void estimerDeriveeIRNCMC(double delta) {
		MRG32k3a baseStream = new MRG32k3a();
		NumeroDeux simulation = new NumeroDeux(baseStream);
	    double[] resultatsIRNCMC = new double[nbIters];
	    double somme = 0;
	    
	    for (int i = 0; i < nbIters; i++) {
	        // Calcul de X1 avec sigma3
	    	simulation.Y3 = new NormalDist(mu3, sigma3);
	        double X1 = this.generationCMC();

	        // Calcul de X2 avec sigma3 + delta
	        simulation.Y3 = new NormalDist(mu3, sigma3 + delta);
	        double X2 = this.generationCMC();

	        // Calcul de la dérivée
	        double derivee = (X2 - X1) / delta;
	        resultatsIRNCMC[i] = derivee;
	        somme += derivee;
	    }

	    // Calcul de la moyenne et de la variance
	    double moyenne = calculerMoyenne(somme);
	    double variance = calculerVariance(resultatsIRNCMC, moyenne);
	    double borneInf = calculerBorneInf(moyenne, variance);
	    double borneSup = calculerBorneSup(moyenne, variance);

	    // Afficher les résultats
	    System.out.printf("Moyenne IRN CMC : %.4f, Variance : %.4f, Intervalle de confiance : [%.4f, %.4f]\n", moyenne, variance, borneInf, borneSup);
	}

	
	private void estimerDeriveeCRNCMC(double delta) {
		MRG32k3a baseStream = new MRG32k3a();
		NumeroDeux simulation1 = new NumeroDeux(baseStream);
		NumeroDeux simulation2 = new NumeroDeux(baseStream.clone());
		simulation2.Y3 = new NormalDist(mu3, sigma3 + delta);
		
		double[] resultatsCRNCMC = new double[nbIters];
	    double somme = 0;
	    
		for (int i = 0; i < nbIters; i++) {
			double X1 = simulation1.generationCMC();
			double X2 = simulation2.generationCMC();
			
			// Calcul de la dérivée
	        double derivee = (X2 - X1) / delta;
	        resultatsCRNCMC[i] = derivee;
	        somme += derivee;
		}
		
		// Calcul de la moyenne et de la variance
	    double moyenne = calculerMoyenne(somme);
	    double variance = calculerVariance(resultatsCRNCMC, moyenne);
	    double borneInf = calculerBorneInf(moyenne, variance);
	    double borneSup = calculerBorneSup(moyenne, variance);

	    // Afficher les résultats 
	    System.out.printf("Moyenne CRN CMC : %.4f, Variance : %.6f, Intervalle de confiance : [%.4f, %.4f]\n", moyenne, variance, borneInf, borneSup);
	}
	
	private void estimerDeriveeStochastique() {
	    MRG32k3a baseStream = new MRG32k3a();
	    NormalDist normal = new NormalDist();
	    
		double[] resultatsDevStocha = new double[nbIters];
	    double somme = 0;
	    for (int i = 0; i < nbIters; i++) {
	    	double U2 = baseStream.nextDouble();
	    	double U3 = baseStream.nextDouble();
	    	
	    	double ppf_U2 = normal.inverseF(U2);
	    	double ppf_U3 = normal.inverseF(U3);
	    	
	    	double v_Y2 = mu2 + (sigma2 * ppf_U2);
	    	double v_Y3 = mu3 + (sigma3 * ppf_U3);
	    	
	    	double densite_W1 = normal.cdf((fonctionX(x, v_Y2, v_Y3) - mu1) / sigma1);
	    	
	    	double estimer = -((densite_W1 * kappa * ppf_U3 * v_Y3) / (x * Math.pow(t, 4) * Math.sqrt((Math.pow(v_Y2, 2) / Math.pow(w, 4)) + (Math.pow(v_Y3, 2) / Math.pow(t, 4))) * sigma1));
	    	resultatsDevStocha[i] = estimer;
	        somme += estimer;
	    }
		
	    // Calcul de la moyenne et de la variance
	    double moyenne = calculerMoyenne(somme);
	    double variance = calculerVariance(resultatsDevStocha, moyenne);
	    double borneInf = calculerBorneInf(moyenne, variance);
	    double borneSup = calculerBorneSup(moyenne, variance);

	    // Afficher les résultats
	    System.out.printf("Moyenne dérivée stochastique : %.4f, Variance : %.6f, Intervalle de confiance : [%.4f, %.4f]\n", moyenne, variance, borneInf, borneSup);
	}
	
	
	public static void main(String[] args) {
		// ######## a) ########
		// On calcule I = 1[X <= 5] sur 1000 iterations
		NumeroDeux simulation1 = new NumeroDeux(new MRG32k3a());
		double[] resultatsMC = new double[nbIters];
		double sommeMC = 0;
		for (int i = 0; i < nbIters; i++) {
		    resultatsMC[i] = simulation1.generationMC();
		    sommeMC += resultatsMC[i];
		}
		double moyenneMC = calculerMoyenne(sommeMC);
		double varianceMC = calculerVariance(resultatsMC, moyenneMC);
		double borneInfMC = calculerBorneInf(moyenneMC, varianceMC);
		double borneSupMC = calculerBorneSup(moyenneMC, varianceMC);
		
		// On calcule J = P[X <= 5 | Y2, Y3] sur 1000 iterations
		NumeroDeux simulation2 = new NumeroDeux(new MRG32k3a());
		double sommeCMC = 0;
		double[] resultatsCMC = new double[nbIters];
		for (int i = 0; i < nbIters; i++) {
		    resultatsCMC[i] = simulation2.generationCMC();
		    sommeCMC += resultatsCMC[i];
		}
		double moyenneCMC = calculerMoyenne(sommeCMC);
		double varianceCMC = calculerVariance(resultatsCMC, moyenneCMC);
		double borneInfCMC = calculerBorneInf(moyenneCMC, varianceCMC);
		double borneSupCMC = calculerBorneSup(moyenneCMC, varianceCMC);
		
		System.out.printf("Espérance avec Monte Carlo : %.3f \n", moyenneMC); // Notre esperance avec MC
		System.out.printf("Variance avec Monte Carlo : %.3f \n", varianceMC); // Notre variance avec MC
		System.out.printf("Intervalle de confiance avec Monte Carlo : [%.3f, %.3f] \n \n", borneInfMC, borneSupMC);
		System.out.printf("Espérance avec Monte Carlo conditionnel : %.3f \n", moyenneCMC); // Notre esperance avec CMC
		System.out.printf("Variance avec Monte Carlo conditionnel : %.3f \n", varianceCMC); // Notre variance avec CMC
		System.out.printf("Intervalle de confiance avec Monte Carlo conditionnel : [%.3f, %.3f] \n \n", borneInfCMC, borneSupCMC);
		
		
		// ######## b) ########
		//
		NumeroDeux simulation = new NumeroDeux(new MRG32k3a());
		double delta = 1.0;
		simulation.estimerDeriveeIRNMC(delta);
		simulation.estimerDeriveeIRNCMC(delta);
		simulation.estimerDeriveeCRNCMC(delta);		
		simulation.estimerDeriveeStochastique();
	}
}
