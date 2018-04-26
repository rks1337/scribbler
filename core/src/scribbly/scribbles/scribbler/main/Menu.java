package scribbly.scribbles.scribbler.main;

import scribbly.scribbles.scribbler.core.Assets;
import scribbly.scribbles.scribbler.core.Audio;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

class Colorized_Table extends Table {

	public boolean is_locked;

	public Colorized_Table(boolean is_locked) {
		Table.debugCellColor = Color.GREEN;
	}
}

public class Menu implements Screen {
	private static final float TIMESTEP = 1 / 60f;
	private static final int VELOCITYITERATIONS = 8, POSITIONITERATIONS = 3;

	private OrthographicCamera camera;
	private PolygonSpriteBatch batch;
	private RayHandler ray_handler;
	private World world;

	private Stage hud;

	private Table table_title;
	private Table table_about;
	private Table table_sound;
	private Table table_left;
	private Table table_right;
	private Table table_stars;
	private Colorized_Table table_slot;

	private TextButton title;
	//private TextButton about;
	private TextButton left;
	private TextButton right;

	private ImageButton slot_1;
	private ImageButton slot_2;
	private ImageButton slot_3;
	private ImageButton slot_4;
	private ImageButton slot_5;
	private ImageButton slot_6;

	private ImageButton slot_7;
	private ImageButton slot_8;
	private ImageButton slot_9;
	private ImageButton slot_10;
	private ImageButton slot_11;
	private ImageButton slot_12;

	private boolean reload;

	Preferences pref = Gdx.app.getPreferences("$scribbler%");
	private int page_number = pref.getInteger("page");
	Level_Handler level_handler = new Level_Handler();

	@Override
	public void show() {
		//init
		world = new World(new Vector2(0f, -9.81f), true);
		camera = new OrthographicCamera();
		batch  = new PolygonSpriteBatch();

		//lights
		ray_handler = new RayHandler(world);

		//hud
		table_title = new Table();
		table_title.setFillParent(true);
		table_about = new Table();
		table_about.setFillParent(true);
		table_sound = new Table();
		table_sound.setFillParent(true);
		table_left = new Table();
		table_left.setFillParent(true);
		table_right = new Table();
		table_right.setFillParent(true);
		table_stars = new Table();
		table_stars.setFillParent(true);
		table_slot = new Colorized_Table(true);
		table_slot.setFillParent(true);

		hud = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()), batch);

		TextButtonStyle style = new TextButtonStyle();

		BitmapFont font = Assets.manager.get("libby_regular_72_coral.ttf", BitmapFont.class);
		style.font = font;
		//about = new TextButton("? ", style);

		TextButton points = new TextButton((Integer.toString(pref.getInteger("points")) + "/48"), style);

		font = Assets.manager.get("journal_3.ttf", BitmapFont.class);
		style.font = font;

		left = new TextButton(" H", style);
		left.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (page_number > 0) {
					Audio.ping_sound.play();
					page_number--;
					reload = true;
				}
				return super.touchDown(event, x, y, pointer, button);
			}
		});

		font = Assets.manager.get("journal_2.ttf", BitmapFont.class);
		style.font = font;

		right = new TextButton("G ", style);
		right.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (page_number < 3) {
					Audio.ping_sound.play();
					page_number++;
					reload = true;
				}
				return super.touchDown(event, x, y, pointer, button);
			}
		});

		font = Assets.manager.get("libby_regular_96_thick.ttf", BitmapFont.class);
		style.font = font;

		title = new TextButton("Physics Scribbler", style);
		//		title.addListener(new InputListener() {
		//			@Override
		//			public boolean touchDown(InputEvent event, float x, float y,
		//					int pointer, int button) {
		//				Audio.ping_sound.play();
		//				((Game) Gdx.app.getApplicationListener()).setScreen(new _Dev());
		//				return super.touchDown(event, x, y, pointer, button);
		//			}
		//		});

		try {
			load_levels();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		table_title.top();
		table_title.add(title);
		table_title.addAction(Actions.alpha(0));
		table_title.addAction(
				Actions.parallel(
						Actions.moveTo(0, -40, 2f, Interpolation.bounceOut),
						Actions.fadeIn(1f)));

		//table_about.top().right();
		//table_about.add(about);

		//table_sound.top().left();

		table_left.bottom().left();
		table_left.add(left);

		table_right.bottom().right();
		table_right.add(right);

		table_stars.bottom();
		table_stars.add(points);

		//scale level selection table according to screen reso
		table_slot.setTransform(true);

		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		if (width < 400) 
			table_slot.setScale(.17f);

		if (width <= 499 && width >= 400) 
			table_slot.setScale(.2f);

		if (width <= 599 && width >= 500) 
			table_slot.setScale(.3f);

		if (width <= 699 && width >= 600) 
			table_slot.setScale(.3f);

		if (width <= 899 && width >= 700) 
			table_slot.setScale(.5f);

		if (width < 1280 && width >= 900) 
			table_slot.setScale(.5f);

		if (width >= 1280) 
			table_slot.setScale(.6f);

		if (width >= 1920) 
			table_slot.setScale(.9f);

		if (width > 2560) 
			table_slot.setScale(.8f);

		table_slot.setOrigin(width/2, height/2);

		hud.addActor(table_title);
		hud.addActor(table_about);
		hud.addActor(table_sound);
		hud.addActor(table_left);
		hud.addActor(table_right);
		hud.addActor(table_stars);
		hud.addActor(table_slot);

		Gdx.input.setInputProcessor(hud);

		//new Color(16/255f, 67/255f, 150/255f, 1f)

		new PointLight(ray_handler, 10, new Color(16/255f, 67/255f, 150/255f, 1f), 400f, 0, 0);
		new PointLight(ray_handler, 10, new Color(16/255f, 67/255f, 150/255f, 1f), 70f, 0, 0);

	}

	private void load_levels() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		if (page_number > 2) {

			Texture t = Assets.manager.get("levels/coming_soon.png");
			Drawable d = new TextureRegionDrawable(new TextureRegion(t));

			Class<?> clazz = Class.forName("scribbly.scribbles.scribbler.realm._37");
			final Object _0 = clazz.newInstance();
			slot_1 = new ImageButton(level_handler.pages.get(page_number).get(0).drawable);
			slot_1.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					Audio.ping_sound.play();
					((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _0);
					return super.touchDown(event, x, y, pointer, button);
				}
			});
			slot_1.addAction(Actions.alpha(0f));
			slot_1.addAction(Actions.fadeIn(.4f));

			slot_2 = new ImageButton(d);
			slot_2.addAction(Actions.alpha(0f));
			slot_2.addAction(Actions.sequence(Actions.delay(.1f), Actions.fadeIn(.4f)));

			slot_3 = new ImageButton(d);
			slot_3.addAction(Actions.alpha(0f));
			slot_3.addAction(Actions.sequence(Actions.delay(.2f), Actions.fadeIn(.4f)));

			slot_4 = new ImageButton(d);
			slot_4.addAction(Actions.alpha(0f));
			slot_4.addAction(Actions.sequence(Actions.delay(.3f), Actions.fadeIn(.4f)));

			slot_5 = new ImageButton(d);
			slot_5.addAction(Actions.alpha(0f));
			slot_5.addAction(Actions.sequence(Actions.delay(.4f), Actions.fadeIn(.4f)));

			slot_6 = new ImageButton(d);
			slot_6.addAction(Actions.alpha(0f));
			slot_6.addAction(Actions.sequence(Actions.delay(.5f), Actions.fadeIn(.4f)));

			slot_7 = new ImageButton(d);
			slot_7.addAction(Actions.alpha(0f));
			slot_7.addAction(Actions.sequence(Actions.delay(.6f), Actions.fadeIn(.4f)));

			slot_8 = new ImageButton(d);
			slot_8.addAction(Actions.alpha(0f));
			slot_8.addAction(Actions.sequence(Actions.delay(.7f), Actions.fadeIn(.4f)));

			slot_9 = new ImageButton(d);
			slot_9.addAction(Actions.alpha(0f));
			slot_9.addAction(Actions.sequence(Actions.delay(.8f), Actions.fadeIn(.4f)));

			slot_10 = new ImageButton(d);
			slot_10.addAction(Actions.alpha(0f));
			slot_10.addAction(Actions.sequence(Actions.delay(.9f), Actions.fadeIn(.4f)));

			slot_11 = new ImageButton(d);
			slot_11.addAction(Actions.alpha(0f));
			slot_11.addAction(Actions.sequence(Actions.delay(1f), Actions.fadeIn(.4f)));

			slot_12 = new ImageButton(d);
			slot_12.addAction(Actions.alpha(0f));
			slot_12.addAction(Actions.sequence(Actions.delay(1.1f), Actions.fadeIn(.4f)));

			table_slot.clearChildren();
			table_slot.add(slot_1).padTop(25).padRight(25);
			table_slot.add(slot_2).padTop(25).padRight(12.5f);
			table_slot.add(slot_3).padTop(25).padLeft(12.5f);
			table_slot.add(slot_4).padTop(25).padLeft(25);
			table_slot.row();
			table_slot.add(slot_5).padTop(25).padRight(25);
			table_slot.add(slot_6).padTop(25).padRight(12.5f);
			table_slot.add(slot_7).padTop(25).padLeft(12.5f);
			table_slot.add(slot_8).padTop(25).padLeft(25);
			table_slot.row();
			table_slot.add(slot_9).padTop(25).padRight(25);
			table_slot.add(slot_10).padTop(25).padRight(12.5f);
			table_slot.add(slot_11).padTop(25).padLeft(12.5f);
			table_slot.add(slot_12).padTop(25).padLeft(25);

			//draw border
			slot_1.debug();
			slot_2.debug();
			slot_3.debug();
			slot_4.debug();
			slot_5.debug();
			slot_6.debug();

			slot_7.debug();
			slot_8.debug();
			slot_9.debug();
			slot_10.debug();
			slot_11.debug();
			slot_12.debug();

			return;
		}

		Class<?> clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(0).name);
		final Object _0 = clazz.newInstance();
		slot_1 = new ImageButton(level_handler.pages.get(page_number).get(0).drawable);
		slot_1.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _0);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_1.addAction(Actions.alpha(0f));
		slot_1.addAction(Actions.fadeIn(.4f));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(1).name);
		final Object _1 = clazz.newInstance();
		slot_2 = new ImageButton(level_handler.pages.get(page_number).get(1).drawable);
		slot_2.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _1);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_2.addAction(Actions.alpha(0f));
		slot_2.addAction(Actions.sequence(Actions.delay(.1f), Actions.fadeIn(.4f)));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(2).name);
		final Object _2 = clazz.newInstance();
		slot_3 = new ImageButton(level_handler.pages.get(page_number).get(2).drawable);
		slot_3.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _2);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_3.addAction(Actions.alpha(0f));
		slot_3.addAction(Actions.sequence(Actions.delay(.2f), Actions.fadeIn(.4f)));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(3).name);
		final Object _3 = clazz.newInstance();
		slot_4 = new ImageButton(level_handler.pages.get(page_number).get(3).drawable);
		slot_4.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _3);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_4.addAction(Actions.alpha(0f));
		slot_4.addAction(Actions.sequence(Actions.delay(.3f), Actions.fadeIn(.4f)));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(4).name);
		final Object _4 = clazz.newInstance();
		slot_5 = new ImageButton(level_handler.pages.get(page_number).get(4).drawable);
		slot_5.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _4);
				return super.touchDown(event, x, y, pointer, button);
			} 
		});
		slot_5.addAction(Actions.alpha(0f));
		slot_5.addAction(Actions.sequence(Actions.delay(.4f), Actions.fadeIn(.4f)));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(5).name);
		final Object _5 = clazz.newInstance();
		slot_6 = new ImageButton(level_handler.pages.get(page_number).get(5).drawable);
		slot_6.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _5);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_6.addAction(Actions.alpha(0f));
		slot_6.addAction(Actions.sequence(Actions.delay(.5f), Actions.fadeIn(.4f)));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(6).name);
		final Object _6 = clazz.newInstance();
		slot_7 = new ImageButton(level_handler.pages.get(page_number).get(6).drawable);
		slot_7.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _6);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_7.addAction(Actions.alpha(0f));
		slot_7.addAction(Actions.sequence(Actions.delay(.6f), Actions.fadeIn(.4f)));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(7).name);
		final Object _7 = clazz.newInstance();
		slot_8 = new ImageButton(level_handler.pages.get(page_number).get(7).drawable);
		slot_8.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _7);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_8.addAction(Actions.alpha(0f));
		slot_8.addAction(Actions.sequence(Actions.delay(.7f), Actions.fadeIn(.4f)));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(8).name);
		final Object _8 = clazz.newInstance();
		slot_9 = new ImageButton(level_handler.pages.get(page_number).get(8).drawable);
		slot_9.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _8);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_9.addAction(Actions.alpha(0f));
		slot_9.addAction(Actions.sequence(Actions.delay(.8f), Actions.fadeIn(.4f)));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(9).name);
		final Object _9 = clazz.newInstance();
		slot_10 = new ImageButton(level_handler.pages.get(page_number).get(9).drawable);
		slot_10.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _9);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_10.addAction(Actions.alpha(0f));
		slot_10.addAction(Actions.sequence(Actions.delay(.9f), Actions.fadeIn(.4f)));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(10).name);
		final Object _10 = clazz.newInstance();
		slot_11 = new ImageButton(level_handler.pages.get(page_number).get(10).drawable);
		slot_11.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _10);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_11.addAction(Actions.alpha(0f));
		slot_11.addAction(Actions.sequence(Actions.delay(1f), Actions.fadeIn(.4f)));

		clazz = Class.forName("scribbly.scribbles.scribbler.realm._" + level_handler.pages.get(page_number).get(11).name);
		final Object _11 = clazz.newInstance();
		slot_12 = new ImageButton(level_handler.pages.get(page_number).get(11).drawable);
		slot_12.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen((Screen) _11);
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		slot_12.addAction(Actions.alpha(0f));
		slot_12.addAction(Actions.sequence(Actions.delay(1.1f), Actions.fadeIn(.4f)));

		table_slot.clearChildren();
		table_slot.add(slot_1).padTop(25).padRight(25);
		table_slot.add(slot_2).padTop(25).padRight(12.5f);
		table_slot.add(slot_3).padTop(25).padLeft(12.5f);
		table_slot.add(slot_4).padTop(25).padLeft(25);
		table_slot.row();
		table_slot.add(slot_5).padTop(25).padRight(25);
		table_slot.add(slot_6).padTop(25).padRight(12.5f);
		table_slot.add(slot_7).padTop(25).padLeft(12.5f);
		table_slot.add(slot_8).padTop(25).padLeft(25);
		table_slot.row();
		table_slot.add(slot_9).padTop(25).padRight(25);
		table_slot.add(slot_10).padTop(25).padRight(12.5f);
		table_slot.add(slot_11).padTop(25).padLeft(12.5f);
		table_slot.add(slot_12).padTop(25).padLeft(25);

		//draw border
		slot_1.debug();
		slot_2.debug();
		slot_3.debug();
		slot_4.debug();
		slot_5.debug();
		slot_6.debug();

		slot_7.debug();
		slot_8.debug();
		slot_9.debug();
		slot_10.debug();
		slot_11.debug();
		slot_12.debug();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);

		//Gdx.gl.glEnable(GL20.GL_BLEND);
		//Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		ray_handler.setCombinedMatrix(camera);
		ray_handler.updateAndRender();

		batch.setProjectionMatrix(hud.getCamera().combined);
		hud.act(delta);
		hud.draw();

		//flip page
		if (reload) {
			try {
				load_levels();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			reload = false;
		}

		//input 2
		Gdx.input.setCatchBackKey(true);
		if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = 1200 / 30;
		camera.viewportHeight = 800 / 30;
		hud.getViewport().update(width, height);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {
		pref.putInteger("page", page_number);
		pref.flush();
		dispose();
	}

	@Override
	public void dispose() {
		batch.dispose();
		world.dispose();
		hud.dispose();
		ray_handler.dispose();
	}
}