package World;

import java.util.ArrayList;

public class Map {
	public static int width = 1920;
	public static int hight = 1080;
	public static class configurations{
		public static class starting{
			public static int starting_energy = 500;
			public static int starting_temp_blocks_warm = 7;
			public static int starting_temp_blocks_cold = 3;
		}
	}
	public static class objects{
		public static ArrayList<int[]> energy_blocks = new ArrayList<int[]>();
		public static ArrayList<int[]> temp_blocks_cold = new ArrayList<int[]>();
		public static ArrayList<int[]> temp_blocks_warm = new ArrayList<int[]>();
		public static ArrayList<int[]> special_blocks = new ArrayList<int[]>();
		public static ArrayList<int[]> fish = new ArrayList<int[]>();
	}
}
