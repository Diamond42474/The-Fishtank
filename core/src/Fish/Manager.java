package Fish;

import java.util.Random;

import World.Map;

public class Manager {
	private static int count = 5;

	public static void startup() {
		for (int i = 0; i < count; i++) {
			Tank.list.add(fish_spawner());
		}

	}

	public static void update() {
		for (int i = 0; i < Tank.list.size(); i++) {
			Tank.list.get(i).updater.Update();
		}
	}

	private static Fish fish_spawner() {
		Fish f = new Fish();
		Random r = new Random();
		f.coordinates.set_spawn(4+r.nextInt(Map.width-24), 4+r.nextInt(Map.hight-24));
		f.basic.set_brain(1);
		return f;
	}
}
