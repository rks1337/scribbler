package scribbly.scribbles.scribbler.realm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import scribbly.scribbles.scribbler.core.Assets;
import scribbly.scribbles.scribbler.core.Audio;
import scribbly.scribbles.scribbler.core.Gesture;
import scribbly.scribbles.scribbler.core.Screenshot;
import scribbly.scribbles.scribbler.main.Menu;
import scribbly.scribbles.scribbler.main.Particle;
import scribbly.scribbles.scribbler.main.Polygon;
import scribbly.scribbles.scribbler.main.Zone_Loader;
import bitfire.postprocessing.PostProcessor;
import bitfire.postprocessing.effects.Bloom;
import bitfire.utils.ShaderLoader;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class _38 implements Screen {
	private static final float TIMESTEP = 1 / 60f;
	private static final int VELOCITYITERATIONS = 8, POSITIONITERATIONS = 3;

	//private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
	private ImmediateModeRenderer20 gl20;
	private PostProcessor post_processor;
	private Zone_Loader zone_handler;
	private OrthographicCamera camera;
	private PolygonSpriteBatch batch;	
	private RayHandler ray_handler;
	private ShapeRenderer sr;
	private World world;

	private Array<Vector2> line_vertices = new Array<Vector2>();
	private Array<Fixture> fixtures_to_render = new Array<Fixture>();
	private Array<Body> bodies_to_render = new Array<Body>();
	private Array<Body> bodies_currently_rendered = new Array<Body>();
	private Array<Vector2> triangle_strip = new Array<Vector2>();
	private Vector2 perpendicular_point = new Vector2();
	private Vector3 mouse;
	private Texture tex;

	private Stage hud;
	private Table table0;
	private Table table1;
	private Table table2;
	private Table table3;
	private Table table4;

	private TextButton result;
	private TextButton time_display;
	private TextButton menu;
	private TextButton reset;

	private boolean show_intrsuctions = true;
	private boolean spawner_switch;
	private boolean save_in_progress;
	private boolean enable_tri_strip;
	private boolean take_screenshot;
	private boolean start_timer;
	private boolean game_over;
	private boolean spawn_goo; 
	private boolean fade_out;
	private boolean complete;
	private boolean fade_in;
	private boolean pass;

	private float fade = 0f; 
	private float time_limit = 15;
	private int triangle_batch;

	Preferences pref = Gdx.app.getPreferences("$scribbler%");
	private String level = "32";

	@Override
	public void show() {
		//init
		world = new World(new Vector2(0f, -9.81f), true);
		gl20 = new ImmediateModeRenderer20(false, true, 1);
		camera = new OrthographicCamera();
		batch  = new PolygonSpriteBatch();
		sr = new ShapeRenderer();
		mouse = new Vector3();
		tex = new Texture(Gdx.files.internal("colors/line_white.png"), true);
		zone_handler = new Zone_Loader();

		//lights
		ray_handler = new RayHandler(world);

		init_hud();

		//input
		InputProcessor input_0 = new GestureDetector(new Gesture() {
		}) {

			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
				if (complete && fade == 0.5f) {
					Audio.ping_sound.play();
					if (take_screenshot) {
						Screenshot.capture(level);
						//next
						((Game) Gdx.app.getApplicationListener()).setScreen(new _33());
					} else {
						//reset
						((Game) Gdx.app.getApplicationListener()).setScreen(new _38());
					}
				}

				if (pointer == 0) {
					if (!show_intrsuctions) {
						if (spawner_switch) {
							spawn_goo = true;
							Audio.goo_sound.play();
						} else {
							enable_tri_strip = true;
							start_timer = true;
						}
					} else {
						if (!game_over) {
							fade_out = true;

							table1.addAction(Actions.sequence(Actions.fadeOut(.25f), Actions.hide()));
							table3.addAction(Actions.sequence(Actions.fadeOut(.25f), Actions.hide()));
						}
					}
				}

				return super.touchDown(x, y, pointer, button);
			}

			@Override
			public boolean touchUp(float x, float y, int pointer, int button) {
				if (pointer == 0) {
					if (!show_intrsuctions) {
						if (spawner_switch) {
							spawn_goo = false;
							Audio.goo_sound.stop();
						} else {
							start_timer = true;
							generate_polygons_from(line_vertices);

							//clear
							line_vertices.clear();
							enable_tri_strip = false;
						}
					}
				}

				return super.touchUp(x, y, pointer, button);
			}

			@Override
			public boolean touchDragged(float x, float y, int pointer) {
				if (pointer > 0) {
					generate_polygons_from(line_vertices);
					line_vertices.clear();
					enable_tri_strip = false;
					return super.touchDragged(x, y, pointer);
				}
				if (pointer == 0) {
					if (!show_intrsuctions) {

						start_timer = true;
						enable_tri_strip = true;

						if (!spawn_goo) {
							if (line_vertices.size < 200) {
								camera.unproject(mouse.set(x, y, 0));
								if (mouse.x < 22f && mouse.x > -22f && mouse.y > -16f) {
									line_vertices.add(new Vector2(mouse.x, mouse.y));
								}
							}
						}
					}
				}

				return super.touchDragged(x, y, pointer);
			}

			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Keys.F1:
					save_in_progress = true;
					break;
				case Keys.F5:
					//load zone from json
					zone_handler.load_zone(Gdx.files.local("zone.json"));
					load_zone();
					break;
				case Keys.F9:
					Screenshot.capture2(level);
					break;
				case Keys.TAB:
					if (spawner_switch) {
						spawner_switch = false;
					} else {
						spawner_switch = true;
					}
					break;
				}
				return super.keyDown(keycode);
			}
		};

		InputMultiplexer input_multiplexer = new InputMultiplexer();
		input_multiplexer.addProcessor(hud);
		input_multiplexer.addProcessor(input_0);
		Gdx.input.setInputProcessor(input_multiplexer);

		Body body;
		Polygon ground = new Polygon(10, 2, new Vector2(22,-14), 1);

		//plat1
		ground = new Polygon(1, 1f, new Vector2(-14, 7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(-7, 7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(0, 7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(7, 7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(14, 7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();

		//plat2
		ground = new Polygon(1, 1f, new Vector2(-14, 0), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(-7, 0), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(0, 0), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(7, 0), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();

		//plat3
		ground = new Polygon(1, 1f, new Vector2(-14, -7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(-7, -7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(0, -7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(7, -7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(10.5f, -7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(14, -7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		ground = new Polygon(1, 1f, new Vector2(17.5f, -7), 1);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();

		//dot
		Particle dot = new Particle(-14, -7, 1, 2);
		dot.fixtureDef.filter.categoryBits = 4; //is a
		dot.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		dot.bodyDef.gravityScale = -5f;
		body = world.createBody(dot.bodyDef);
		body.createFixture(dot.fixtureDef);
		body.setUserData(dot.sprite);
		dot.circle_shape.dispose();

		//cup
		ground = new Polygon(1.75f, .1f, new Vector2(14, 6), 0);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		ground.bodyDef.type = BodyType.KinematicBody;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		//+4, 0
		ground = new Polygon(.1f, 2f, new Vector2(12, 4), 0);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		ground.bodyDef.angle = -.1f;
		ground.bodyDef.type = BodyType.KinematicBody;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();
		//-2, -2
		ground = new Polygon(.1f, 2f, new Vector2(16, 4), 0);
		ground.fixtureDef.filter.categoryBits = 4; //is a
		ground.fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with
		ground.bodyDef.gravityScale = -4f;
		ground.bodyDef.angle = .1f;
		ground.bodyDef.type = BodyType.KinematicBody;
		body = world.createBody(ground.bodyDef);
		body.createFixture(ground.fixtureDef).setUserData(ground.polygon_sprite);
		ground.polygon_shape.dispose();

		//load cup from json
		zone_handler.load_zone(Gdx.files.internal("json/" + level + ".json"));
		load_zone();

		//shader
		ShaderLoader.BasePath = "shaders/";
		post_processor = new PostProcessor(false, false, true);

		Bloom bloom = new Bloom(1000, 1000);
		bloom.setBaseIntesity(0);
		bloom.setBaseSaturation(0);
		bloom.setBloomSaturation(1);
		bloom.setBloomIntesity(10);
		bloom.setBlurAmount(0);
		bloom.setThreshold(.97f);

		post_processor.addEffect(bloom);

		//check for objective completion
		world.setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void beginContact(Contact contact) {
				if (contact.getFixtureB().getBody().getGravityScale() == -4.2f &&
						contact.getFixtureA().getBody().getGravityScale() == -5f && !complete) {

					Audio.splash_sound.play(.1f);

					table4.setVisible(false);
					pass = true;
				}
			}
		});

		//new Color(16/255f, 67/255f, 150/255f, 1f)
		PointLight l1 = new PointLight(ray_handler, 10, Color.ROYAL, 100f, 0, 30);
		l1.setContactFilter((short)8, (short)1, (short)(1 | 2 | 4));
		PointLight l2 = new PointLight(ray_handler, 10, new Color(16/255f, 67/255f, 150/255f, 1f), 100f, 0, -2);
		l2.setContactFilter((short)8, (short)1, (short)(1 | 2 | 4));
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		world.getFixtures(fixtures_to_render);
		world.getBodies(bodies_to_render);

		post_processor.capture();
		batch.begin();
		for (Body body : bodies_to_render) {
			if (body.getUserData() instanceof Sprite) {
				Sprite sprite = (Sprite) body.getUserData();
				sprite.setAlpha(1);
				sprite.setSize(2f, 2f);
				sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
				sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				sprite.draw(batch);
			}
		}

		for (Fixture fixture : fixtures_to_render) {
			if (fixture.getUserData() instanceof PolygonSprite) {
				PolygonSprite polygon_sprite = (PolygonSprite) fixture.getUserData();

				polygon_sprite.setPosition(
						fixture.getBody().getPosition().x - polygon_sprite.getWidth() / 30, 
						fixture.getBody().getPosition().y - polygon_sprite.getHeight() / 30);
				polygon_sprite.setRotation(fixture.getBody().getAngle() * MathUtils.radiansToDegrees);
				polygon_sprite.draw(batch);
			}

			//remove line and goo that are off screen
			if (fixture.getBody().getPosition().y < -40 || fixture.getBody().getPosition().y > 40) {
				fixture.getBody().destroyFixture(fixture.getBody().getFixtureList().get(0));
			}
		}
		batch.end();

		if (enable_tri_strip && line_vertices.size > 5) {
			tex.bind();
			update(line_vertices);
			draw_line(camera);
		}

		post_processor.render();

		ray_handler.setCombinedMatrix(camera);
		ray_handler.updateAndRender();

		batch.setProjectionMatrix(hud.getCamera().combined);
		hud.act(delta);
		hud.draw();

		table_animations_and_pass_detection(delta);

		//debug
		//debugRenderer.render(world, camera.combined);
		//System.out.println("fps: " + Gdx.graphics.getFramesPerSecond());

		//input 2
		Gdx.input.setCatchBackKey(true);
		if (Gdx.input.isKeyPressed(Input.Keys.BACK) || Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			((Game) Gdx.app.getApplicationListener()).setScreen(new Menu());
		}
	}

	private void init_hud() {
		//hud
		table0 = new Table();
		table0.setFillParent(true);
		table1 = new Table();
		table1.setFillParent(true);
		table2 = new Table();
		table2.setFillParent(true);
		table3 = new Table();
		table3.setFillParent(true);
		table4 = new Table();
		table4.setFillParent(true);

		hud = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera()), batch);

		/////////////////////////////////libby_regular_84/////////////////////////////////
		TextButtonStyle style = new TextButtonStyle();
		BitmapFont font = Assets.manager.get("libby_regular_84.ttf", BitmapFont.class);
		style.font = font;

		//result button
		result = new TextButton("", style);
		result.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				if (take_screenshot && fade == 0.5f) {
					Screenshot.capture(level);
					//next
					((Game) Gdx.app.getApplicationListener()).setScreen(new _33());
				} else {
					//reset
					((Game) Gdx.app.getApplicationListener()).setScreen(new _38());
				}
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		/////////////////////////////////libby_regular_84/////////////////////////////////

		/////////////////////////////////libby_regular_84_shadow//////////////////////////
		font = Assets.manager.get("libby_regular_84_shadow.ttf", BitmapFont.class);
		style.font = font;

		//time button
		time_display = new TextButton("15.00", style);
		time_display.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f)));
		/////////////////////////////////libby_regular_84_shadow//////////////////////////

		/////////////////////////////////arrows///////////////////////////////////////////
		font = Assets.manager.get("arrows.ttf", BitmapFont.class);
		style.font = font;

		//home button
		menu = new TextButton("RRR", style);
		menu.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				if (take_screenshot && fade == 0.5f) {
					Screenshot.capture(level);
				}
				((Game) Gdx.app.getApplicationListener()).setScreen(new Menu());
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		menu.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f)));

		//home button
		reset = new TextButton("y", style);
		reset.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Audio.ping_sound.play();
				((Game) Gdx.app.getApplicationListener()).setScreen(new _38());
				return super.touchDown(event, x, y, pointer, button);
			}
		});
		reset.setVisible(false);
		/////////////////////////////////arrows///////////////////////////////////////////

		table0.addActor(new Actor() {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				batch.end();

				Gdx.gl.glEnable(GL20.GL_BLEND);

				Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				sr.begin(ShapeType.Filled);
				sr.setColor(new Color(0,0,0,fade));
				sr.rect(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				sr.end();

				Gdx.gl.glDisable(GL20.GL_BLEND);

				batch.begin();
				super.draw(batch, parentAlpha);
			}	
		});

		table1.center();
		table1.add(result).left();
		table1.row();

		table2.top().right();
		table2.add(time_display);

		table3.top().left();
		table3.add(menu);
		table3.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(.25f), Actions.show()));

		table4.top().left();
		table4.add(reset);

		hud.addActor(table0);
		hud.addActor(table1);
		hud.addActor(table2);
		hud.addActor(table3);
		hud.addActor(table4);
	}

	private void table_animations_and_pass_detection(float delta) {
		if (spawn_goo) {
			spawn_goo();
		}

		if (save_in_progress) {
			save_zone();
		}

		if (fade_out) {
			show_intrsuctions = false;
			reset.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1f)));
			reset.setVisible(true);
			fade_out = false;
		}

		if (fade_in) {
			if (fade < 0.5f) {
				fade+=(delta);
			} else {
				fade = 0.5f;
				fade_in = false;
			}
		}

		if (start_timer) {
			if (time_limit > 0.1f) {

				time_limit -= delta;

				DecimalFormat sb = new DecimalFormat("#.0");
				time_display.setText(sb.format(time_limit));

			} else {
				//fail
				reset.setVisible(false);
				time_display.setText("0.0");

				show_intrsuctions = true;
				game_over = true;
				fade_in = true;

				result.setText("fail");

				table1.addAction(Actions.sequence(Actions.fadeIn(.25f), Actions.show()));
				table3.addAction(Actions.sequence(Actions.fadeIn(.25f), Actions.show()));

				complete = true;
				start_timer = false;
			}
		}

		if (pass) {
			//win
			start_timer = false;

			show_intrsuctions = true;
			game_over = true;
			fade_in = true;

			result.setText("pass");

			table1.addAction(Actions.sequence(Actions.fadeIn(.25f), Actions.show()));
			table3.addAction(Actions.sequence(Actions.fadeIn(.25f), Actions.show()));

			complete = true;

			if (!pref.getBoolean(level)) {
				int e = pref.getInteger("points");
				e++;
				pref.putInteger("points", e);
				pref.putBoolean(level, true);	
				pref.flush();
			}

			take_screenshot = true;
			pass = false;
		}
	}

	private void load_zone() {
		Array<Array<Polygon>> line_list = new Array<Array<Polygon>>();
		Vector2[] polygon_vertices = new Vector2[4];
		int e = 0;

		//create polygons from loaded line vectors
		for (int i = 0; i < zone_handler.loaded_lines.size; i++) {

			//System.out.println(zone_handler.loaded_lines.get(i).size);

			//create empty polygon list
			Array<Polygon> polygon_list = new Array<Polygon>();

			//check if total amount of line vertices are a multiple of 4
			//break to avoid segmenation error
			//line won't be rendered
			if (!(zone_handler.loaded_lines.get(i).size % 4 == 0)) {
				System.out.println("\nError: line with triangle polygon (" + 
						zone_handler.loaded_lines.get(i).size + " vertices)");
				break;
			}

			//loop through every 4 vectors and create polygon
			for (int j = 0; j < zone_handler.loaded_lines.get(i).size; j+=4) {
				polygon_vertices[e] = zone_handler.loaded_lines.get(i).get(j);
				polygon_vertices[e+1] = zone_handler.loaded_lines.get(i).get(j+1);
				polygon_vertices[e+2] = zone_handler.loaded_lines.get(i).get(j+2);
				polygon_vertices[e+3] = zone_handler.loaded_lines.get(i).get(j+3);

				//store polygons to array
				polygon_list.add(new Polygon(polygon_vertices, 3));
			}

			//add polygon array as a new line
			line_list.add(polygon_list);

			//reset polygon vertice index
			e = 0;
		}

		//send lines to renderer
		Body polygon_body;
		for (Array<Polygon> ap : line_list) {
			ap.get(0).fixtureDef.filter.categoryBits = 4; //is a
			ap.get(0).fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with

			polygon_body = world.createBody(ap.get(0).bodyDef);
			polygon_body.setUserData(ap.get(0).polygon_sprite);
			ap.get(0).polygon_shape.dispose();

			for (int i = 1; i < ap.size-1; i++) {
				ap.get(i).fixtureDef.filter.categoryBits = 4; //is a
				ap.get(i).fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with

				polygon_body.createFixture(ap.get(i).fixtureDef)
				.setUserData(ap.get(i).polygon_sprite);

				ap.get(i).polygon_shape.dispose();
			}
		}

		//send goo to renderer
		Body goo_body;
		for (Vector2 goo : zone_handler.loaded_goo) {
			Particle particle = new Particle(goo.x, goo.y, 2);
			particle.fixtureDef.filter.categoryBits = 2; //is a
			particle.fixtureDef.filter.maskBits = 2 | 4; //collides with
			particle.bodyDef.gravityScale = -4.2f;

			goo_body = world.createBody(particle.bodyDef);
			goo_body.createFixture(particle.fixtureDef);
			goo_body.setUserData(particle.sprite);
			particle.circle_shape.dispose();
		}
	}

	private void save_zone() {
		world.getBodies(bodies_currently_rendered);

		Array<Array<Vector2>> save_lines = new Array<Array<Vector2>>();
		Array<Vector2> save_goo = new Array<Vector2>();

		for (int i = 0; i < bodies_currently_rendered.size; i++) {

			//save line vertices
			//head vertice found (4.1f gravity scale)
			if (bodies_currently_rendered.get(i).getGravityScale() == 4.1f) {

				Array<Vector2> verts = new Array<Vector2>();

				//store tail
				for (int j = 0; j < bodies_currently_rendered.get(i).getFixtureList().size; j++) {

					Fixture f = bodies_currently_rendered.get(i).getFixtureList().get(j);
					Shape s = f.getShape();

					Vector2 tmp = new Vector2();
					for (int e = 0; e < ((PolygonShape) s).getVertexCount(); e++) {
						((PolygonShape) s).getVertex(e, tmp);

						verts.add(new Vector2(tmp));
					}
				}
				//place seperator in order to determine end of tail
				verts.add(new Vector2(-1, -1));

				//save line verts
				save_lines.add(verts);
			}

			//save goo positions
			if (bodies_currently_rendered.get(i).getGravityScale() == -4.2f) {
				save_goo.add(bodies_currently_rendered.get(i).getPosition());
			}
		}

		//send to handler
		zone_handler.save_zone(save_lines, save_goo);
		save_in_progress = false;
	}

	private void spawn_goo() {
		camera.unproject(mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		Particle particle = new Particle(mouse.x, mouse.y, 1);
		particle.fixtureDef.filter.categoryBits = 2; //is a
		particle.fixtureDef.filter.maskBits = 4 | 2; //collides with
		particle.bodyDef.gravityScale = -4.2f;

		Body goo_body;
		goo_body = world.createBody(particle.bodyDef);
		goo_body.createFixture(particle.fixtureDef);
		goo_body.setUserData(particle.sprite);
		particle.circle_shape.dispose();
	}

	private void generate_polygons_from(Array<Vector2> line) {
		Vector2 perpendicular_point = new Vector2();

		Array<Polygon> polygon_list = new Array<Polygon>();

		List<Float> line_vertices_list = new ArrayList<Float>();

		// loop and store perpendicular vertices
		for (int i = 0; i < line.size-1; i++) {

			//direction
			perpendicular_point.set(line.get(i)).sub(line.get(i+1)).nor();

			//perpendicular line
			perpendicular_point.set(-perpendicular_point.y, perpendicular_point.x);

			//perpendicular line length
			perpendicular_point.scl(.128f);

			//add first perpendicular point
			perpendicular_point.scl(1);
			line_vertices_list.add(line.get(i).x+perpendicular_point.x); 
			line_vertices_list.add(line.get(i).y+perpendicular_point.y);

			//add second perpendicular point
			perpendicular_point.scl(-1);
			line_vertices_list.add(line.get(i).x+perpendicular_point.x); 
			line_vertices_list.add(line.get(i).y+perpendicular_point.y);
		}

		//polygon
		float[] polygon_vertices = new float[8];

		//polygon_vertices increment
		int e = 0;

		//loop over line vertices
		//increment every 4 vertices
		for (int i = 0; i < line_vertices_list.size(); i+=4) {

			//make sure there's 8 vertices for the polygon
			if (i < line_vertices_list.size()-8) {

				//vertex 1
				polygon_vertices[e] = line_vertices_list.get(i);
				polygon_vertices[e+1] = line_vertices_list.get(i+1);

				//vertex 2
				polygon_vertices[e+2] = line_vertices_list.get(i+2);
				polygon_vertices[e+3] = line_vertices_list.get(i+3);

				//vertex 3
				polygon_vertices[e+4] = line_vertices_list.get(i+4);
				polygon_vertices[e+5] = line_vertices_list.get(i+5);

				//vertex 4
				polygon_vertices[e+6] = line_vertices_list.get(i+6);
				polygon_vertices[e+7] = line_vertices_list.get(i+7);
			}

			//filter polygons
			//make sure polygon vertices aren't 0
			if (polygon_vertices[0] != 0f) {

				//check for degenerate polygons by making 
				//sure the width of each polygon is >= 4f
				Vector2 a = new Vector2(polygon_vertices[e], polygon_vertices[e+1]);
				Vector2 b = new Vector2(polygon_vertices[e+6], polygon_vertices[e+7]);

				if (!(a.dst(b) < .2f)) { //.4 minimum
					polygon_list.add(new Polygon(polygon_vertices, 1));
				}
			}

			//reset for new polygon
			e = 0;
			polygon_vertices = new float[8];
		}

		//return if no polygons
		if (polygon_list.size <= 0f) {
			return;
		}

		//send to renderer
		Body polygon_body;

		//create first polygons body then create its fixtures
		//send polygon sprite to renderer
		polygon_list.get(0).bodyDef.gravityScale = 4.1f;
		polygon_list.get(0).fixtureDef.filter.categoryBits = 4; //is a
		polygon_list.get(0).fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with

		polygon_body = world.createBody(polygon_list.get(0).bodyDef);
		polygon_body.setUserData(polygon_list.get(0).polygon_sprite);
		polygon_list.get(0).polygon_shape.dispose();

		for (int i = 1; i < polygon_list.size; i++) {
			polygon_list.get(i).fixtureDef.filter.categoryBits = 4; //is a
			polygon_list.get(i).fixtureDef.filter.maskBits = 1 | 2 | 4; //collides with

			polygon_body.createFixture(polygon_list.get(i).fixtureDef).setUserData(polygon_list.get(i).polygon_sprite);
			polygon_list.get(i).polygon_shape.dispose();
		}
	}

	private int generate_line_from(Array<Vector2> line, int switch_point) {
		int j = triangle_strip.size;

		for (int i = 1; i < line.size-1; i++) {

			//direction
			perpendicular_point.set(line.get(i)).sub(line.get(i+1)).nor();

			//perpendicular line
			perpendicular_point.set(-perpendicular_point.y, perpendicular_point.x);

			//perpendicular line length
			perpendicular_point.scl(.128f);

			//add perpendicular point
			perpendicular_point.scl(switch_point);
			triangle_strip.add(new Vector2(line.get(i).x+perpendicular_point.x, line.get(i).y+perpendicular_point.y));
			triangle_strip.add(new Vector2(line.get(i).x, line.get(i).y));
		}

		return triangle_strip.size - j;
	}

	public void draw_line(Camera camera) {
		if (triangle_strip.size <= 0) {
			return;
		}

		gl20.begin(camera.combined, GL20.GL_TRIANGLE_STRIP);
		for (int i = 0; i < triangle_strip.size; i++) {
			if (i == triangle_batch) {
				gl20.end();
				gl20.begin(camera.combined, GL20.GL_TRIANGLE_STRIP);
			}	
			Vector2 point = triangle_strip.get(i);
			gl20.color(Color.WHITE);
			gl20.vertex(point.x, point.y, 0f);
		}
		gl20.end();
	}

	public void update(Array<Vector2> line) {
		triangle_strip.clear();

		if (line.size < 10) {
			return;
		}

		triangle_batch = generate_line_from(line, 1);
		generate_line_from(line, -1);
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = 1200 / 30;
		camera.viewportHeight = 800 / 30;
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {
		post_processor.rebind();
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		//debugRenderer.dispose();
		batch.dispose();
		world.dispose();
		hud.dispose();
		ray_handler.dispose();
		post_processor.dispose();
		sr.dispose();
		tex.dispose();
	}
}