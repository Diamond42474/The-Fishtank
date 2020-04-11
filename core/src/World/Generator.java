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
			int[] q = {4+r.nextInt(Map.width-12),4+r.nextInt(Map.hight-12)};
			Map.objects.energy_blocks.add(q);
		}
		
		for(int i=0;i<Map.configurations.starting.starting_temp_blocks_warm;i++) {
			Random r = new Random();
			int[] q = {16+r.nextInt(Map.width-48),16+r.nextInt(Map.hight-48)};
			Map.objects.temp_blocks_warm.add(q);
		}
		
		for(int i=0;i<Map.configurations.starting.starting_temp_blocks_cold;i++) {
			Random r = new Random();
			int[] q = {16+r.nextInt(Map.width-48),16+r.nextInt(Map.hight-48)};
			Map.objects.temp_blocks_cold.add(q);
		}
	}
}
