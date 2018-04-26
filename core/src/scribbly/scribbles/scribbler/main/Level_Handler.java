package scribbly.scribbles.scribbler.main;

import scribbly.scribbles.scribbler.core.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Level_Handler {

	public Array<Array<Level>> pages = new Array<Array<Level>>();
	Preferences pref = Gdx.app.getPreferences("$scribbler%");

	public Level_Handler() {

		int e = 0;
		for (int i = 1; i <= 4; i++) {

			Array<Level> page = new Array<Level>();

			for (int j = 1; j <= 12; j++) {

				if (pref.getBoolean(Integer.toString(++e))) {
					try{
						Texture t = new Texture(Gdx.files.local("./_" + e + ".png"), true);
						t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
						Level level = new Level(Integer.toString(e), new TextureRegionDrawable(new TextureRegion(t)));
						page.add(level);
						System.out.println(Gdx.files.getLocalStoragePath());
					} catch(GdxRuntimeException exception){
						System.out.println("User has exited after level completion ("+exception.getMessage()+")");
						Texture t = Assets.manager.get("levels/"+e+".png");
						Level level = new Level(Integer.toString(e), new TextureRegionDrawable(new TextureRegion(t)));
						page.add(level);
					}

				} else {
					Texture t = Assets.manager.get("levels/"+e+".png");
					Level level = new Level(Integer.toString(e), new TextureRegionDrawable(new TextureRegion(t)));
					page.add(level);
				}

			}
			pages.add(page);
		}
	}
}

class Level {
	String name;
	Drawable drawable;

	Level(String name, Drawable drawable) {
		this.name = name;
		this.drawable = drawable;
	}
}