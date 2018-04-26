package scribbly.scribbles.scribbler.main;

import scribbly.scribbles.scribbler.core.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

public class Polygon {

	public PolygonShape polygon_shape;
	public BodyDef bodyDef;
	public FixtureDef fixtureDef;
	public PolygonSprite polygon_sprite;

	public Polygon(float[] f, int color) {
		polygon_shape = new PolygonShape();
		polygon_shape.set(f);

		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.gravityScale = 4f;
		bodyDef.angularVelocity = 0f;

		fixtureDef = new FixtureDef();
		fixtureDef.shape = polygon_shape;
		fixtureDef.friction = .3f;
		fixtureDef.restitution = 0f;
		fixtureDef.density = .4f;

		FloatArray float_array = new FloatArray();
		Vector2 tmp = new Vector2();
		for (int i = 0; i < polygon_shape.getVertexCount(); i++) {
			polygon_shape.getVertex(i, tmp);
			float_array.add(tmp.x);
			float_array.add(tmp.y);
		}

		TextureRegion texture_region = null;

		switch (color) {
		case 1:
			texture_region = new TextureRegion(Assets.manager.get(Assets.white_line));
			break;
		case 2:
			texture_region = new TextureRegion(Assets.manager.get(Assets.blue_line));
			break;
		case 3:
			texture_region = new TextureRegion(Assets.manager.get(Assets.magenta_line));
			break;
		}

		EarClippingTriangulator ear = new EarClippingTriangulator();
		ShortArray triangles = ear.computeTriangles(float_array);

		PolygonRegion poly_region = new PolygonRegion(texture_region, float_array.toArray(), triangles.toArray());
		polygon_sprite = new PolygonSprite(poly_region);
		polygon_sprite.setOrigin(
				polygon_sprite.getWidth() / 30, 
				polygon_sprite.getHeight() / 30);
	}

	public Polygon(Vector2[] v, int color) {
		polygon_shape = new PolygonShape();

		if (!(v == null)) {
			polygon_shape.set(v);
		} else {
			polygon_shape.setAsBox(1, 1);
		}

		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.gravityScale = 4f;
		bodyDef.angularVelocity = 0f;

		fixtureDef = new FixtureDef();
		fixtureDef.shape = polygon_shape;
		fixtureDef.friction = .3f;
		fixtureDef.restitution = 0f;
		fixtureDef.density = 1f;

		FloatArray float_array = new FloatArray();
		Vector2 tmp = new Vector2();
		for (int i = 0; i < polygon_shape.getVertexCount(); i++) {
			polygon_shape.getVertex(i, tmp);
			float_array.add(tmp.x);
			float_array.add(tmp.y);
		}

		TextureRegion texture_region = null;

		switch (color) {
		case 1:
			texture_region = new TextureRegion(Assets.manager.get(Assets.white_line));
			break;
		case 2:
			texture_region = new TextureRegion(Assets.manager.get(Assets.blue_line));
			break;
		case 3:
			texture_region = new TextureRegion(Assets.manager.get(Assets.magenta_line));
		}

		EarClippingTriangulator ear = new EarClippingTriangulator();
		ShortArray triangles = ear.computeTriangles(float_array);

		PolygonRegion poly_region = new PolygonRegion(texture_region, float_array.toArray(), triangles.toArray());
		polygon_sprite = new PolygonSprite(poly_region);
		polygon_sprite.setOrigin(
				polygon_sprite.getWidth() / 30, 
				polygon_sprite.getHeight() / 30);
	}

	public Polygon(float x, float y, Vector2 pos, int color) {
		polygon_shape = new PolygonShape();
		polygon_shape.setAsBox(x, y);

		bodyDef = new BodyDef();
		bodyDef.position.set(pos);

		fixtureDef = new FixtureDef();
		fixtureDef.shape = polygon_shape;

		FloatArray float_array = new FloatArray();

		Vector2 tmp = new Vector2();
		for (int i = 0; i < polygon_shape.getVertexCount(); i++) {
			polygon_shape.getVertex(i, tmp);
			float_array.add(tmp.x);
			float_array.add(tmp.y);
		}

		TextureRegion texture_region = null;

		switch (color) {
		case 0:
			texture_region = new TextureRegion(Assets.manager.get(Assets.white_line));
			break;
		case 1:
			texture_region = new TextureRegion(Assets.manager.get(Assets.green_line));
			break;
		case 2:
			texture_region = new TextureRegion(Assets.manager.get(Assets.blue_line));
			break;
		case 3:
			texture_region = new TextureRegion(Assets.manager.get(Assets.magenta_line));
			break;
		}

		EarClippingTriangulator ear = new EarClippingTriangulator();
		ShortArray triangles = ear.computeTriangles(float_array);

		PolygonRegion poly_region = new PolygonRegion(texture_region, float_array.toArray(), triangles.toArray());
		polygon_sprite = new PolygonSprite(poly_region);
		polygon_sprite.setOrigin(
				polygon_sprite.getWidth() / 30, 
				polygon_sprite.getHeight() / 30);

	}

	public void update(float delta) {

	}

	public void draw(PolygonSpriteBatch batch) {
		update(Gdx.graphics.getDeltaTime());
		polygon_sprite.draw(batch);
	}
}
