package mesdevoirs;

import umontreal.ssj.rng.MRG32k3a;
import java.util.Scanner;
import java.util.Arrays;

public class UniformGenerator {
	private static long m1 = 4294967087L;
	private static long m2 = 4294944443L;
	private static long a11 = 1403580L;
	private static long a12 = 810728L;
	private static long a21 = 527612L;
	private static long a22 = 1370589L;
	private static long[] states;
	
	static {
		// x_{-3}, x_{-2}, x_{-1}, y_{-3}, y_{-2}, y_{-1}
		states = new long[] {12345L, 12345L, 12345L, 12345L, 12345L, 12345L};
	}
	
	public static void main(String[] args) {
		MRG32k3a generator = new MRG32k3a();
		Scanner scanner = new Scanner(System.in);
		StringBuilder builder = new StringBuilder();
		
		builder.append("Entrez un nombre de ")
			   .append("variables aléatoires :");
		System.out.println(builder);
		
		int n = scanner.nextInt();
		
		builder.setLength(0);
		builder.append("L'état initial est : ")
		       .append(Arrays.toString(getCurrentState(generator)) + "\n");
		System.out.println(builder);
		
		for (int i = 0; i < n; i++) {
			System.out.println("Avec MRG32k3a : ");
			builder.setLength(0);
			builder.append("u")
				   .append(i)
			       .append(" = ")
			       .append(generatingNumber(generator))
			       .append(" avec l'état ")
			       .append(Arrays.toString(getCurrentState(generator)));
			System.out.println(builder);
			
			System.out.println("Avec notre récurrence : ");
			builder.setLength(0);
			builder.append("u")
				   .append(i)
			       .append(" = ")
			       .append(recurrence())
			       .append(" avec l'état ")
			       .append(Arrays.toString(states) + "\n");
			System.out.println(builder);
		}	
	}
	
	static double generatingNumber(MRG32k3a generator) {
		return generator.nextDouble();
	}
	
	static long[] getCurrentState(MRG32k3a generator) {
		return generator.getState();
	}
	
	static double recurrence() {
		long x_n = ((a11 * states[1]) - (a12 * states[0])) % m1;
		if (x_n < 0) x_n += m1;
		
		long y_n = ((a21 * states[5]) - (a22 * states[3])) % m2;
		if (y_n < 0) y_n += m2;
		
		long z_n = (x_n - y_n) % m1;
		if (z_n < 0) z_n += m1;
		
		double u_n = ((double) z_n) / m1;
		
		// x_{-3} devient x_{-2}, x_{-2} devient x_{-1} ainsi de suite
	    states[0] = states[1];
	    states[1] = states[2];
	    states[2] = x_n; 

	    states[3] = states[4];
	    states[4] = states[5];
	    states[5] = y_n;
	    
	    return u_n;
	}
}
