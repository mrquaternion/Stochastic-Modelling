package mesdevoirs;

import umontreal.ssj.rng.MRG32k3a;
import java.util.Scanner;

public class UniformGenerator {
	
	public static void main(String[] args) {
		MRG32k3a generator = new MRG32k3a();
		Scanner scanner = new Scanner(System.in);
		StringBuilder builder = new StringBuilder();
		
		builder.append("Entrez un nombre de ")
			   .append("variables aléatoires :");
		
		System.out.println(builder.toString());
		int n = scanner.nextInt();
		
		builder.setLength(0);
		
		builder.append("L'état initial est :\n")
		       .append(generator.toString());
		System.out.print(builder.toString());
		
		for (int i = 0; i < n; i++) {
			builder.setLength(0);
			
			builder.append("u")
				   .append(i)
			       .append(" = ")
			       .append(generatingNumber(generator))
			       .append(" avec l'état ")
			       .append(getCurrentState(generator));
			
			System.out.println(builder);
		}	
	}
	
	static double generatingNumber(MRG32k3a generator) {
		return generator.nextDouble();
	}
	
	static long[] getCurrentState(MRG32k3a generator) {
		return generator.getState();
	}
}
