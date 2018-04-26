package scribbly.scribbles.scribbler.core;

import scribbly.scribbles.scribbler.main.Menu;

import com.badlogic.gdx.Game;

public class Boot extends Game {

	public static Assets assets;

	@Override
	public void create() {
		assets = new Assets();
		assets.load();
		Assets.manager.finishLoading();
		Audio.create();
		//		Audio.action_1.play();
		//		Audio.action_1.setVolume(.25f);
		//		Audio.action_1.setLooping(true);
		setScreen(new Menu());
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause(){
		super.pause();
	}

	@Override
	public void resume(){
		super.resume();
	}

	public Assets getAssets() {
		return assets;
	}
}
