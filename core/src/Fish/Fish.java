package Fish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import Brain.Q_Learning;
import Brain.Rewards;
import World.Map;

public class Fish {
	public Basic basic = new Basic();
	public Mutatable mutatable = new Mutatable();
	public Coordinates coordinates = new Coordinates();
	public Stats stats = new Stats();
	public Updater updater = new Updater();
	private Lists lists = new Lists();

	public class Lists {
		Settings settings = new Settings();
		ArrayList<int[]> fish;
		ArrayList<int[]> food;
		ArrayList<int[]> temp_blocks_warm;
		ArrayList<int[]> temp_blocks_cold;

		class Settings {
			final int visual_number = 3;
		}
	}

	public class Updater {
		private Inputs inputs = new Inputs();
		public Display display = new Display();
		public Movement movement = new Movement();
		public Thinking thinking = new Thinking();
		public Actions actions = new Actions();

		public void Update() {
			stats.time.tick();
			actions.act();
			//thinking.train();
			// movement.keys();
		}

		public class Thinking {
			void train() {
				switch(basic.Brain) {
				case 1:
					Brain.Brain_Collection.net1.compute.training.Train();
					break;
				case 2:
					Brain.Brain_Collection.net2.compute.training.Train();
				}
			}
			double[] get_state() {
				// total should be 28
				int[][] fish = format_list(lists.fish);
				int[][] food = format_list(lists.food);
				int[][] temp_block_warm = format_list(lists.temp_blocks_warm);
				int[][] temp_block_cold = format_list(lists.temp_blocks_cold);
				double[] output = new double[Brain.Brain_Collection.net1.parameters.inputs];
				output[0] = coordinates.x;
				output[1] = coordinates.y;
				output[2] = stats.time.get_time();
				output[3] = stats.health.get_Health();
				for (int i = 4; i < output.length;) {
					for (int ie = 0; ie < lists.settings.visual_number; ie++) {
						output[i] = fish[ie][0];
						output[i+1] = fish[ie][1];

						output[i+2] = food[ie][0];
						output[i+3] = food[ie][1];

						output[i+4] = temp_block_warm[ie][0];
						output[i+5] = temp_block_warm[ie][1];

						output[i+6] = temp_block_cold[ie][0];
						output[i+7] = temp_block_cold[ie][1];
						i += 8;
					}
				}
				return output;
			}

			int[][] format_list(ArrayList<int[]> array) {
				for (int i = array.size(); i != lists.settings.visual_number;) {
					if (array.size() > lists.settings.visual_number) {
						array.remove(array.size() - 1);
					}
					if (array.size() < lists.settings.visual_number) {
						array.add(new int[] { 0, 0, 0 });
					}
					i = array.size();
				}
				int[][] out = new int[array.size()][];
				for (int i = 0; i < array.size(); i++) {
					out[i] = array.get(i);
				}
				return out;
			}
			/*
			 * double[] get_future_state(Fish fish, int action) { Fish f = fish;
			 * f.updater.actions.shadow_act(action); return f.updater.thinking.get_state();
			 * }
			 */
		}

		class Actions {
			Move move = new Move();

			void act() {
				int a = get_action();
				Brain.Experience exp = new Brain.Experience();
				exp.state = thinking.get_state();
				exp.action = a;
				exp.reward = choose(a);
				exp.state_prime = thinking.get_state();
				add_experience(exp);
			}

			double get_reward(double initial) {
				//return initial + stats.time.get_time();
				return initial;
			}

			void add_experience(Brain.Experience exp) {
				switch (basic.Brain) {
				case 1:
					Brain.Brain_Collection.net1.experience.add_experience(exp);
					break;
				case 2:
					Brain.Brain_Collection.net2.experience.add_experience(exp);
					break;
				default:
					System.out.println("Brain isnt right 1 ---" + basic.Brain);
				}
			}

			int get_action() {
				switch (basic.Brain) {
				case 1:
					return Brain.Brain_Collection.net1.compute.get_action_net(thinking.get_state(),stats.time.get_time());
				case 2:
					return Brain.Brain_Collection.net2.compute.get_action_net(thinking.get_state(),stats.time.get_time());
				default:
					System.out.println("Brain isnt right 2 ---");
					return Brain.Brain_Collection.net1.compute.get_action_net(thinking.get_state(),stats.time.get_time());
				}
			}

			double choose(int a) {
				switch (a) {
				case 0:
					return move.Up();
				case 1:
					return move.Down();
				case 2:
					return move.Left();
				case 3:
					return move.Right();
				case 4:
					return move.Stay_Still();
				case 5:
					return Eat();
				default:
					return 0.0;
				}
			}

			/*
			 * void shadow_act(int a) { //just do stuff }
			 */
			double Eat() {
				if(lists.food.get(0)[2]<basic.interaction_zone&&lists.food.size()>0) {
					try {
						Map.objects.energy_blocks.remove(lists.food.get(0)[3]);
					}catch(Exception e) {
						//System.out.println(lists.food.get(0).length);
					}
					return get_reward(Rewards.eat);
				}else{
					return 0;
				}
			}
			class Move {
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
					coordinates.y += mutatable.speed;
					Position_Manager();
					return get_reward(Rewards.move);
				}

				double Down() {
					coordinates.y -= mutatable.speed;
					Position_Manager();
					return get_reward(Rewards.move);
				}

				double Left() {
					coordinates.x -= mutatable.speed;
					Position_Manager();
					return get_reward(Rewards.move);
				}

				double Right() {
					coordinates.x += mutatable.speed;
					Position_Manager();
					return get_reward(Rewards.move);
				}

				double Stay_Still() {
					Position_Manager();
					return 0;
				}
			}
		}

		private class Inputs {
			Trig trig = new Trig();
			Temp_Block temp_block = new Temp_Block();
			Sort sort = new Sort();

			class Sort {
				ArrayList<int[]> sortbyColumn(ArrayList<int[]> array, final int col) {
					int[][] arr = new int[array.size()][4];
					for (int i = 0; i < array.size(); i++) {
						arr[i] = array.get(i);
					}
					try {
						Arrays.sort(arr, new Comparator<int[]>() {

							@Override
							public int compare(final int[] entry1, final int[] entry2) {
								if (entry1[col] > entry2[col])
									return 1;
								else
									return -1;
							}
						});
					}catch(Exception e) {
						e.printStackTrace();
					}
					array.clear();
					for (int i = 0; i < arr.length; i++) {
						array.add(new int[] { arr[i][0], arr[i][1], arr[i][2],arr[i][3]});
					}
					return array;
				}
			}

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
						list.add(new int[] { (int) x, (int) y, (int) distance,i });
					}
				}
				list = sort.sortbyColumn(list, 2);
				list.remove(0);
				lists.fish = list;
				return list;
			}

			ArrayList<int[]> food() {
				int size = Map.objects.energy_blocks.size();
				ArrayList<int[]> list = new ArrayList<int[]>();
				for (int i = 0; i < size; i++) {
					int[] q = Map.objects.energy_blocks.get(i);
					double distance = trig.distance(q[0], q[1]);
					if (distance < mutatable.visual_range) {
						int index = i;
						list.add(new int[] { q[0], q[1], (int) distance ,index});
					}
				}
				list = sort.sortbyColumn(list, 2);
				lists.food = list;
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
							list.add(new int[] { q[0], q[1], (int) distance,i });
						}
					}
					list = sort.sortbyColumn(list, 2);
					lists.temp_blocks_warm = list;
					return list;
				}

				ArrayList<int[]> cold() {
					int size2 = Map.objects.temp_blocks_cold.size();
					ArrayList<int[]> list = new ArrayList<int[]>();
					for (int i = 0; i < size2; i++) {
						int[] q = Map.objects.temp_blocks_cold.get(i);
						double distance = trig.distance(q[0], q[1]);
						if (distance < mutatable.visual_range) {
							list.add(new int[] { q[0], q[1], (int) distance,i });
						}
					}
					list = sort.sortbyColumn(list, 2);
					lists.temp_blocks_cold = list;
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
				drawer.list.line(Color.RED, inputs.temp_block.warm(), 32);
				drawer.list.circles(32, Color.RED, inputs.temp_block.cold());
				drawer.list.line(Color.RED, inputs.temp_block.cold(), 32);
				drawer.list.circles(8, Color.YELLOW, inputs.food());
				drawer.list.line(Color.RED, inputs.fish(), basic.interaction_zone);
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
		public Time time = new Time();

		public class Time {
			private int time_alive = 0;

			public void tick() {
				time_alive++;
			}

			public int get_time() {
				return time_alive;
			}
		}

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
		final int interaction_zone = 32;
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
