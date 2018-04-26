package scribbly.scribbles.scribbler.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

public final class Screenshot {

	private static byte[] pixels;
	private static Pixmap pixmap;

	//capture finished zone
	public static void capture(String level) {
		pixels = ScreenUtils.getFrameBufferPixels(0, 0,
				Gdx.graphics.getBackBufferWidth(),
				Gdx.graphics.getBackBufferHeight(),
				true);

		pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(),
				Gdx.graphics.getBackBufferHeight(),
				Pixmap.Format.RGBA8888);

		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);

		Pixmap pixmap_resize = new Pixmap(400, 200, pixmap.getFormat());
		pixmap_resize.drawPixmap(
				pixmap, 0, 0, 
				pixmap.getWidth(), 
				pixmap.getHeight(), 
				0, 0, 400, 200);

		PixmapIO.writePNG(Gdx.files.local("./_" + level + ".png"), pixmap_resize);

		pixmap.dispose();
		pixmap_resize.dispose();
		pixels = null;
	}

	//capture unfinished zone
	public static void capture2(String level) {
		pixels = ScreenUtils.getFrameBufferPixels(0, 0,
				Gdx.graphics.getBackBufferWidth(),
				Gdx.graphics.getBackBufferHeight(),
				true);

		pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(),
				Gdx.graphics.getBackBufferHeight(),
				Pixmap.Format.RGBA8888);

		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
		PixmapIO.writePNG(Gdx.files.local("./" + level + ".png"), pixmap);

		pixmap.dispose();
		pixels = null;
	}
}