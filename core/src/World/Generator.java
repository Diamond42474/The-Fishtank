package World;

import java.util.Random;

public class Generator {
	public static void startup() {
		clear_world();
		generate();
	}
	private static void clear_world() {
		Map.objects.energy_blocks.clear();
		Map.objects.temp_blocks_warm.clear();
		Map.objects.temp_blocks_cold.clear();
	}
	private static void generate() {
		for(int i=0;i<Map.configurations.starting.starting_energy;i++) {
			Random r = new Random();
			int[] q = {r.nextInt(Map.width-8),r.nextInt(Map.hight-8)};
			Map.objects.energy_blocks.add(q);
		}
		
		for(int i=0;i<Map.configurations.starting.starting_temp_blocks_warm;i++) {
			Random r = new Random();
			int[] q = {r.nextInt(Map.width-32),r.nextInt(Map.hight-32)};
			Map.objects.temp_blocks_warm.add(q);
		}
		
		for(int i=0;i<Map.configurations.starting.starting_temp_blocks_cold;i++) {
			Random r = new Random();
			int[] q = {r.nextInt(Map.width-32),r.nextInt(Map.hight-32)};
			Map.objects.temp_blocks_cold.add(q);
		}
	}
}
