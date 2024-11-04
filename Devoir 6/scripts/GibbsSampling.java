package mesdevoirs;

import umontreal.ssj.rng.MRG32k3a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GibbsSampling {
	static Map<Integer, int[]> graph;
	static Map<Integer, Integer> coloring;
	static Map<String, Integer> colorCount = new HashMap<>(); 
	static final int NUM_ITERS = 216000;
	static final int NUM_COLORS = 4;
	static MRG32k3a generator = new MRG32k3a();
	
	static {
		// Initialisation du graphe
		graph = Map.of(
			1, new int[] {2},
			2, new int[] {1, 3, 4},
			3, new int[] {2, 4},
			4, new int[] {2, 3, 5},
			5, new int[] {4}
		);
	}
	
	static void graphInitialization() {
		coloring = new HashMap<>();
		
		// Initialisation du premier coloriage
		for (Map.Entry<Integer, int[]> entry : graph.entrySet()) {
			int node = entry.getKey();
			coloring.put(node, generator.nextInt(1, NUM_COLORS));
		}
	}
	
	static Map<Integer, Integer> getNeighborsColors(int node) {
		Map<Integer, Integer> neighborsColors = new HashMap<>();
		int[] neighbors = graph.get(node);
		
		int currentId = 1;
		for (int neighbor : neighbors) {
			neighborsColors.put(currentId, coloring.get(neighbor));
			currentId++;
		}
		
		return neighborsColors;
	}
	
	static void sampling() {
		for (int i = 0; i < NUM_ITERS; i++) {
			for (Map.Entry<Integer, int[]> entry : graph.entrySet()) {
				int node = entry.getKey();
				Map<Integer, Integer> neighborsColors = getNeighborsColors(node);
				
				List<Integer> availableColors = new ArrayList<>();
				for (int j = 1; j < NUM_COLORS + 1; j++) {
					if (!neighborsColors.containsValue(j)) {
						availableColors.add(j);
					}
				}
				
				if (!availableColors.isEmpty()) {
                    int randomIndex = generator.nextInt(0, availableColors.size() - 1);
                    coloring.put(node, availableColors.get(randomIndex));
                }
			}
			recordColoring();
		}
	}
	
	static void recordColoring() {
        StringBuilder tupleBuilder = new StringBuilder();
        for (int i = 1; i <= graph.size(); i++) {
            tupleBuilder.append(coloring.get(i)).append(",");
        }
        String colorTuple = tupleBuilder.toString();

        colorCount.put(colorTuple, colorCount.getOrDefault(colorTuple, 0) + 1);
    }

	public static void main(String[] args) {
		graphInitialization();
		sampling();
		
		StringBuilder builder = new StringBuilder();
		
		int totalCount = 0;
		for (Map.Entry<String, Integer> entry : colorCount.entrySet()) {
			totalCount += entry.getValue();
			
			builder.setLength(0);
			builder.append("Le coloriage ")
				   .append(entry.getKey())
				   .append(" a été visité ")
				   .append(entry.getValue());
			System.out.println(builder);
        }
		
		System.out.println("\nNombre total de coloriage admissible : " + totalCount);
	}
}
