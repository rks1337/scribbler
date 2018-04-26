package scribbly.scribbles.scribbler.main;

import scribbly.scribbles.scribbler.core.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Particle {

	public CircleShape circle_shape;
	public BodyDef bodyDef;
	public FixtureDef fixtureDef;
	public Sprite sprite;

	public Particle(float x, float y, int color) {
		circle_shape = new CircleShape();
		circle_shape.setRadius(.25f);

		switch (color) {
		case 1:
			sprite = new Sprite(Assets.manager.get(Assets.magenta_goo));
			break;
		case 2:
			sprite = new Sprite(Assets.manager.get(Assets.blue_goo));
			break;
		}

		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.gravityScale = 4.2f;

		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle_shape;
		fixtureDef.friction = 0f;
		fixtureDef.restitution = .1f;
		fixtureDef.density = .005f;
	}

	public Particle(float x, float y, int r, int color) {
		circle_shape = new CircleShape();
		circle_shape.setRadius(r);

		switch (color) {
		case 1:
			sprite = new Sprite(Assets.manager.get(Assets.magenta_circle));
			break;
		case 2:
			sprite = new Sprite(Assets.manager.get(Assets.blue_circle));
			break;
		}

		sprite.setSize(r*2, r*2);

		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.gravityScale = 5f;

		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle_shape;
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle_shape;
		fixtureDef.friction = .05f;
		fixtureDef.restitution = .1f;
		fixtureDef.density = .1f;
	}

	public void update(float delta) {

	}

	public void draw(PolygonSpriteBatch batch) {
		update(Gdx.graphics.getDeltaTime());
		sprite.draw(batch);
	}
}
