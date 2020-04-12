package com.mygdx.game;

import org.nd4j.linalg.factory.Nd4j;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import Assets.Asset_Manager;
import Assets.Assets;
import Fish.Tank;
import World.Map;

public class GDXSim extends ApplicationAdapter {
	private OrthographicCamera camera;
	
	@Override
	public void create () {
		World.Generator.startup();
		Asset_Manager.load_assets();
		Fish.Manager.startup();
		
		camera = new OrthographicCamera(Map.width, Map.hight);
		camera.translate(camera.viewportWidth/2, camera.viewportHeight/2);
		Brain.Brain_Collection.setup();
		System.out.println(Brain.Brain_Collection.net1.compute.calculate(new double[]{1,1})[0]);
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0.02f, 0.01f, 0.03f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		Assets.shapeRenderer.setProjectionMatrix(camera.combined);
		Assets.sprite_batch.begin();
		Assets.sprite_batch.setProjectionMatrix(camera.combined);
		Asset_Manager.draw.main();
		Assets.sprite_batch.end();
		Asset_Manager.draw.shapes();
		Fish.Manager.update();
		if(Gdx.input.isKeyJustPressed(Input.Keys.A)) {
			World.Generator.startup();
		}
	}
	
	@Override
	public void dispose () {
		Assets.sprite_batch.dispose();
		Asset_Manager.dispose_assets();
	}
}
