package Assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Assets {
	public static SpriteBatch sprite_batch = new SpriteBatch();
	public static ShapeRenderer shapeRenderer = new ShapeRenderer();
	public static class fish{
		public static Texture normal;
	}
	public static class food{
		public static Texture normal;
	}
	public static class temp_blocks{
		public static Texture hot;
		public static Texture cold;
	}
}
