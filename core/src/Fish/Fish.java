package Fish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import Assets.Assets;
import World.Map;

public class Fish {
	private static int x = 8;
	private static int y = 8;
	public static void update() {
		move();
		inputs.food();
		inputs.temp_block();
	}
	public static void display() {
		Assets.sprite_batch.draw(Assets.fish.normal, x-8, y-8);
	}
	public static void display_shapes() {
		make_circle(x,y,stats.visual_range, Color.RED);
		make_circle(x,y,stats.interaction_zone, Color.BLUE);
	}
	private static class inputs{
		static double distance(double x2, double y2) {
			return Math.sqrt(Math.pow((x-x2), 2)+Math.pow((y-y2), 2));
		}
		static double angle(double x2, double y2) {
			x2-=x;
			y2-=y;
			double degrees = Math.toDegrees(Math.atan2(x2, y2));
			return degrees;
		}
		static void food() {
			int size = Map.objects.energy_blocks.size();
			for(int i=0;i<size;i++) {
				int[] q = Map.objects.energy_blocks.get(i);
				double distance = distance(q[0]+4,q[1]+4);
				if(distance<stats.visual_range) {
					make_circle(q[0]+4,q[1]+4,4, Color.YELLOW);
				}
				if(distance<stats.interaction_zone) {
					Map.objects.energy_blocks.remove(i);
					size = Map.objects.energy_blocks.size();
				}
			}
		}
		static void temp_block() {
			int size1 = Map.objects.temp_blocks_warm.size();
			for(int i=0;i<size1;i++) {
				int[] q = Map.objects.temp_blocks_warm.get(i);
				double distance = distance(q[0]+16,q[1]+16);
				if(distance<stats.visual_range) {
					make_line(q[0]+16,q[1]+16,Color.RED);
					make_circle(q[0]+16,q[1]+16,16, Color.RED);
				}
			}
			int size2 = Map.objects.temp_blocks_cold.size();
			for(int i=0;i<size2;i++) {
				int[] q = Map.objects.temp_blocks_cold.get(i);
				double distance = distance(q[0]+16,q[1]+16);
				if(distance<stats.visual_range) {
					make_line(q[0]+16,q[1]+16,Color.RED);
					make_circle(q[0]+16,q[1]+16,16, Color.RED);
				}
			}
		}
	}
	private static void make_circle(int x1, int y1, int radius, Color color) {
		Assets.shapeRenderer.setColor(color);
		Assets.shapeRenderer.begin(ShapeType.Line);
		Assets.shapeRenderer.circle(x1, y1, radius);
		Assets.shapeRenderer.end();
	}
	private static void make_line(int x2, int y2, Color color) {
		Assets.shapeRenderer.setColor(color);
		Assets.shapeRenderer.begin(ShapeType.Line);
		Assets.shapeRenderer.line(x, y,x2,y2);
		Assets.shapeRenderer.end();
	}
	private static void move() {
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			y+=stats.speed;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			y-=stats.speed;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			x+=stats.speed;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			x-=stats.speed;
		}
		if((x+8)>Map.width) {
			x=Map.width-8;
		}
		if((x)<8) {
			x=8;
		}
		if((y+8)>Map.hight) {
			y=Map.hight-8;
		}
		if((y)<8) {
			y=8;
		}
	}
	private static class stats{
		static int speed = 5;
		static int interaction_zone = 16;
		static int visual_range = 150;
	}
}
