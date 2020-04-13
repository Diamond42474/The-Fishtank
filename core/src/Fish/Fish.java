package Fish;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import Brain.Rewards;
import World.Map;

public class Fish {
	public Basic basic = new Basic();
	public Mutatable mutatable = new Mutatable();
	public Coordinates coordinates = new Coordinates();
	public Stats stats = new Stats();
	public Updater updater = new Updater();

	public class Updater {
		private Inputs inputs = new Inputs();
		public Display display = new Display();
		public Movement movement = new Movement();
		public Thinking thinking = new Thinking();
		public Actions actions = new Actions();
		public void Update() {
			actions.act();
			//movement.keys();
		}
		public class Thinking{
			double[] get_state() {
				return new double[] {coordinates.x,coordinates.y};
			}
			/*
			double[] get_future_state(Fish fish, int action) {
				Fish f = fish;
				f.updater.actions.shadow_act(action);
				return f.updater.thinking.get_state();
			}
			*/
		}
		class Actions{
			Move move = new Move();
			void act() {
				int a = get_action();
				Brain.Experience exp = new Brain.Experience();
				exp.state = thinking.get_state();
				exp.action = a;
				exp.reward = choose(a);
				exp.state = thinking.get_state();
				add_experience(exp);
			}
			void add_experience(Brain.Experience exp){
				switch(basic.Brain) {
				case 1:
					Brain.Brain_Collection.net1.experience.add_experience(exp);
					break;
				case 2:
					Brain.Brain_Collection.net2.experience.add_experience(exp);
					break;
				default: 
					System.out.println("Brain isnt right 1 ---"+ basic.Brain);
				}
			}
			int get_action() {
				switch(basic.Brain) {
				case 1:
					return Brain.Brain_Collection.net1.compute.get_action(thinking.get_state());
				case 2:
					return Brain.Brain_Collection.net2.compute.get_action(thinking.get_state());
				default: 
					System.out.println("Brain isnt right 2 ---");
					return Brain.Brain_Collection.net1.compute.get_action(thinking.get_state());
				}
			}
			double choose(int a) {
				switch(a) {
				case 0: 
					return move.Up();
				case 1: 
					return move.Down();
				case 2:
					return move.Left();
				case 3: 
					return move.Right();
				default:
					return 0.0;
				}
			}
			/*
			void shadow_act(int a) {
				//just do stuff
			}
			*/
			class Move{
				void Position_Manager() {
					if ((coordinates.x + 8) > Map.width) {
						coordinates.x = Map.width - 8;
					}
					if ((coordinates.x) < 8) {
						coordinates.x = 8;
					}
					if ((coordinates.y + 8) > Map.hight) {
						coordinates.y = Map.hight - 8;
					}
					if ((coordinates.y) < 8) {
						coordinates.y = 8;
					}
				}
				double Up() {
					coordinates.y+=mutatable.speed;
					Position_Manager();
					return Rewards.move;
				}
				double Down() {
					coordinates.y-=mutatable.speed;
					Position_Manager();
					return Rewards.move;
				}
				double Left() {
					coordinates.x-=mutatable.speed;
					Position_Manager();
					return Rewards.move;
				}
				double Right() {
					coordinates.x+=mutatable.speed;
					Position_Manager();
					return Rewards.move;
				}
			}
		}
		private class Inputs {
			Trig trig = new Trig();
			Temp_Block temp_block = new Temp_Block();

			class Trig {
				double distance(float x2, float y2) {
					return Math.sqrt(Math.pow((coordinates.x - x2), 2) + Math.pow((coordinates.y - y2), 2));
				}

				double angle(float x2, float y2) {
					x2 -= coordinates.x;
					y2 -= coordinates.y;
					double radiant = Math.atan(y2 / x2);
					double angle = radiant * (180 / Math.PI);
					return angle;
				}

				int[] polar_to_rectangular(float r, double angle, float x3, float x2) {
					x2 -= x3;
					if (x2 <= 0) {
						r = 0 - r;
					}
					double radiant = angle * (Math.PI / 180);
					double x = r * Math.cos(radiant);
					double y = r * Math.sin(radiant);
					return new int[] { (int) x, (int) y };
				}
			}

			ArrayList<int[]> fish() {
				int size = Tank.list.size();
				ArrayList<int[]> list = new ArrayList<int[]>();
				for (int i = 0; i < size; i++) {
					float x = Tank.list.get(i).coordinates.get_X();
					float y = Tank.list.get(i).coordinates.get_Y();
					double distance = trig.distance(x, y);
					if (distance < mutatable.visual_range) {
						list.add(new int[] { (int) x, (int) y });
					}
				}
				return list;
			}

			ArrayList<int[]> food() {
				int size = Map.objects.energy_blocks.size();
				ArrayList<int[]> list = new ArrayList<int[]>();
				for (int i = 0; i < size; i++) {
					int[] q = Map.objects.energy_blocks.get(i);
					double distance = trig.distance(q[0], q[1]);
					if (distance < mutatable.visual_range) {
						list.add(new int[] { q[0], q[1] });
					}
				}
				return list;
			}

			class Temp_Block {
				ArrayList<int[]> warm() {
					int size1 = Map.objects.temp_blocks_warm.size();
					ArrayList<int[]> list = new ArrayList<int[]>();
					for (int i = 0; i < size1; i++) {
						int[] q = Map.objects.temp_blocks_warm.get(i);
						double distance = trig.distance(q[0], q[1]);
						if (distance < mutatable.visual_range) {
							list.add(new int[] { q[0], q[1] });
						}
					}
					return list;
				}

				ArrayList<int[]> cold() {
					int size2 = Map.objects.temp_blocks_cold.size();
					ArrayList<int[]> list = new ArrayList<int[]>();
					for (int i = 0; i < size2; i++) {
						int[] q = Map.objects.temp_blocks_cold.get(i);
						double distance = trig.distance(q[0], q[1]);
						if (distance < mutatable.visual_range) {
							list.add(new int[] { q[0], q[1] });
						}
					}
					return list;
				}
			}

		}

		public class Movement {
			public void keys() {
				if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
					coordinates.y += mutatable.speed;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
					coordinates.y -= mutatable.speed;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
					coordinates.x += mutatable.speed;
				}
				if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
					coordinates.x -= mutatable.speed;
				}
				if ((coordinates.x + 8) > Map.width) {
					coordinates.x = Map.width - 8;
				}
				if ((coordinates.x) < 8) {
					coordinates.x = 8;
				}
				if ((coordinates.y + 8) > Map.hight) {
					coordinates.y = Map.hight - 8;
				}
				if ((coordinates.y) < 8) {
					coordinates.y = 8;
				}
			}
		}

		public class Display {
			private Drawer drawer = new Drawer();

			public void Normal() {
				Assets.Assets.sprite_batch.draw(Assets.Assets.fish.normal, coordinates.x - 8, coordinates.y - 8);
			}

			public void Shapes() {
				drawer.normal.make_circle(coordinates.x, coordinates.y, mutatable.visual_range, Color.BLUE);
				drawer.normal.make_circle(coordinates.x, coordinates.y, basic.interaction_zone, Color.RED);

				drawer.list.circles(32, Color.RED, inputs.temp_block.warm());
				drawer.list.line(Color.RED, inputs.temp_block.warm(),32);
				drawer.list.circles(32, Color.RED, inputs.temp_block.cold());
				drawer.list.line(Color.RED, inputs.temp_block.cold(),32);
				drawer.list.circles(8, Color.YELLOW, inputs.food());
				drawer.list.line(Color.RED, inputs.fish(),basic.interaction_zone);
			}

			class Drawer {
				List list = new List();
				Normal normal = new Normal();

				class List {
					void circles(int radius, Color color, ArrayList<int[]> list) {
						for (int i = 0; i < list.size(); i++) {
							Assets.Assets.shapeRenderer.setColor(color);
							Assets.Assets.shapeRenderer.begin(ShapeType.Line);
							Assets.Assets.shapeRenderer.circle(list.get(i)[0], list.get(i)[1], radius);
							Assets.Assets.shapeRenderer.end();
						}
					}

					void line(Color color, ArrayList<int[]> list, int r2) {
						for (int i = 0; i < list.size(); i++) {
							normal.make_line(list.get(i)[0], list.get(i)[1], color, r2);
						}
					}
				}

				public class Normal {
					void make_circle(float x, float y, int radius, Color color) {
						Assets.Assets.shapeRenderer.setColor(color);
						Assets.Assets.shapeRenderer.begin(ShapeType.Line);
						Assets.Assets.shapeRenderer.circle(x, y, radius);
						Assets.Assets.shapeRenderer.end();
					}

					void make_line(int x2, int y2, Color color, int radius2) {
						Assets.Assets.shapeRenderer.setColor(color);
						Assets.Assets.shapeRenderer.begin(ShapeType.Line);
						double angle = inputs.trig.angle(x2, y2);
						int[] rc = inputs.trig.polar_to_rectangular(basic.interaction_zone, angle, coordinates.x, x2);
						int[] rc2 = inputs.trig.polar_to_rectangular(radius2, angle, x2, coordinates.x);
						Assets.Assets.shapeRenderer.line(rc[0] + coordinates.x, rc[1] + coordinates.y, x2 + rc2[0],
								y2 + rc2[1]);
						Assets.Assets.shapeRenderer.end();
					}
				}
			}
		}
	}

	public class Stats {
		public Health health = new Health();

		public class Health {
			private int health = 100;

			public void set_Health(int health1) {
				health = health1;
			}

			public int get_Health() {
				return health;
			}
		}
	}

	public class Coordinates {
		private float x = 8;
		private float y = 8;

		public void set_spawn(float x1, float y1) {
			x = x1;
			y = y1;
		}

		public float get_X() {
			return x;
		}

		public float get_Y() {
			return y;
		}
	}
	public class Basic {
		final int interaction_zone = 16;
		private int Brain = 1;
		public void set_brain(int b) {
			Brain = b;
		}
	}

	public class Mutatable {
		private int visual_range = 100;
		private float speed = 1f;

		public void set_Visual_Range(int range) {
			visual_range = range;
		}

		public void set_Speed(int speed1) {
			speed = speed1;
		}

		public int get_Visual_Range() {
			return visual_range;
		}

		public float get_Speed() {
			return speed;
		}
	}
}
