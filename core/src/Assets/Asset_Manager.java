package Assets;

import com.badlogic.gdx.graphics.Texture;

import Fish.Tank;

public class Asset_Manager {
	public static void load_assets() {
		Assets.fish.normal = new Texture("jeff.png");
		Assets.food.normal = new Texture("energy.png");
		Assets.temp_blocks.hot = new Texture("warm_block.png");
		Assets.temp_blocks.cold = new Texture("cold_block.png");
	}
	public static void dispose_assets() {
		Assets.fish.normal.dispose();
		Assets.food.normal.dispose();
		Assets.temp_blocks.hot.dispose();
		Assets.temp_blocks.cold.dispose();
	}
	public static class draw{
		public static void main() {
			energy();
			temp_blocks();
			fish();
		}
		public static void shapes() {
			for(int i=0;i<Tank.list.size();i++) {
				Tank.list.get(i).updater.display.Shapes();
			}
		}
		private static void energy() {
			for(int i=0;i<World.Map.objects.energy_blocks.size();i++) {
				int[] q = World.Map.objects.energy_blocks.get(i);
				Assets.sprite_batch.draw(Assets.food.normal, q[0]-4, q[1]-4);
			}
		}
		private static void temp_blocks() {
			for(int i=0;i<World.Map.objects.temp_blocks_cold.size();i++) {
				int[] q = World.Map.objects.temp_blocks_cold.get(i);
				Assets.sprite_batch.draw(Assets.temp_blocks.cold, q[0]-16, q[1]-16);
			}
			for(int i=0;i<World.Map.objects.temp_blocks_warm.size();i++) {
				int[] q = World.Map.objects.temp_blocks_warm.get(i);
				Assets.sprite_batch.draw(Assets.temp_blocks.hot, q[0]-16, q[1]-16);
			}
		}
		private static void fish() {
			for(int i=0;i<Tank.list.size();i++) {
				Tank.list.get(i).updater.display.Normal();
			}
		}
	}
}
