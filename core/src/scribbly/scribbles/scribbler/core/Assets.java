package scribbly.scribbles.scribbler.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

public class Assets {

	public static AssetManager manager = new AssetManager();

	public static final AssetDescriptor<Texture> blue_goo =  new AssetDescriptor<Texture>("colors/goo_blue.png", Texture.class);
	public static final AssetDescriptor<Texture> green_goo =  new AssetDescriptor<Texture>("colors/goo_green.png", Texture.class);
	public static final AssetDescriptor<Texture> magenta_goo =  new AssetDescriptor<Texture>("colors/goo_magenta.png", Texture.class);

	public static final AssetDescriptor<Texture> blue_line =  new AssetDescriptor<Texture>("colors/line_blue.png", Texture.class);
	public static final AssetDescriptor<Texture> green_line =  new AssetDescriptor<Texture>("colors/line_green.png", Texture.class);
	public static final AssetDescriptor<Texture> magenta_line =  new AssetDescriptor<Texture>("colors/line_magenta.png", Texture.class);
	public static final AssetDescriptor<Texture> white_line =  new AssetDescriptor<Texture>("colors/line_white.png", Texture.class);

	public static final AssetDescriptor<Texture> magenta_circle =  new AssetDescriptor<Texture>("colors/circle_magenta.png", Texture.class);
	public static final AssetDescriptor<Texture> blue_circle =  new AssetDescriptor<Texture>("colors/circle_blue.png", Texture.class);

	public static final AssetDescriptor<Music> action_1 =  new AssetDescriptor<Music>("sounds/Puzzle-Action-2.mp3", Music.class);
	public static final AssetDescriptor<Music> action_2 =  new AssetDescriptor<Music>("sounds/Puzzle-Action-3.mp3", Music.class);
	public static final AssetDescriptor<Sound> goo_sound =  new AssetDescriptor<Sound>("sounds/goo.ogg", Sound.class);
	public static final AssetDescriptor<Sound> ping_sound =  new AssetDescriptor<Sound>("sounds/ping.ogg", Sound.class);
	public static final AssetDescriptor<Sound> splash_sound =  new AssetDescriptor<Sound>("sounds/splash.ogg", Sound.class);

	public void load() {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

		//levels
		FreeTypeFontLoaderParameter libby_regular_84 = new FreeTypeFontLoaderParameter();
		libby_regular_84.fontFileName = "font/LibbyRegular.ttf";
		libby_regular_84.fontParameters.size = scale_fonts(84);
		libby_regular_84.fontParameters.color = Color.WHITE;
		manager.load("libby_regular_84.ttf", BitmapFont.class, libby_regular_84);

		FreeTypeFontLoaderParameter libby_regular_84_shadow = new FreeTypeFontLoaderParameter();
		libby_regular_84_shadow.fontFileName = "font/LibbyRegular.ttf";
		libby_regular_84_shadow.fontParameters.size = scale_fonts(84);
		libby_regular_84_shadow.fontParameters.shadowOffsetX = 3;
		libby_regular_84_shadow.fontParameters.shadowOffsetY = 3;
		libby_regular_84_shadow.fontParameters.color = Color.WHITE;
		manager.load("libby_regular_84_shadow.ttf", BitmapFont.class, libby_regular_84_shadow);

		FreeTypeFontLoaderParameter arrows = new FreeTypeFontLoaderParameter();
		arrows.fontFileName = "font/Arrows.ttf";
		arrows.fontParameters.size = scale_fonts(175);
		arrows.fontParameters.shadowOffsetX = 3;
		arrows.fontParameters.shadowOffsetY = 3;
		arrows.fontParameters.color = Color.WHITE;
		manager.load("arrows.ttf", BitmapFont.class, arrows);

		//main menu
		FreeTypeFontLoaderParameter libby_regular_96_thick = new FreeTypeFontLoaderParameter();
		libby_regular_96_thick.fontFileName = "font/LibbyRegular.ttf";
		libby_regular_96_thick.fontParameters.size = scale_fonts(108);
		libby_regular_96_thick.fontParameters.shadowOffsetX = 5;
		libby_regular_96_thick.fontParameters.shadowOffsetY = 5;
		libby_regular_96_thick.fontParameters.borderWidth = 5;
		libby_regular_96_thick.fontParameters.borderColor = Color.WHITE;
		libby_regular_96_thick.fontParameters.color = Color.WHITE;
		manager.load("libby_regular_96_thick.ttf", BitmapFont.class, libby_regular_96_thick);

		FreeTypeFontLoaderParameter libby_regular_72_coral = new FreeTypeFontLoaderParameter();
		libby_regular_72_coral.fontFileName = "font/LibbyRegular.ttf";
		libby_regular_72_coral.fontParameters.size = scale_fonts(72);
		libby_regular_72_coral.fontParameters.color = Color.CHARTREUSE;
		manager.load("libby_regular_72_coral.ttf", BitmapFont.class, libby_regular_72_coral);

		FreeTypeFontLoaderParameter journal_2 = new FreeTypeFontLoaderParameter();
		journal_2.fontFileName = "font/JournalDingbats3.ttf";
		journal_2.fontParameters.size = scale_fonts(190);
		journal_2.fontParameters.shadowOffsetX = 3;
		journal_2.fontParameters.shadowOffsetY = 3;
		journal_2.fontParameters.color = Color.WHITE;
		manager.load("journal_2.ttf", BitmapFont.class, journal_2);
		
		FreeTypeFontLoaderParameter journal_3 = new FreeTypeFontLoaderParameter();
		journal_3.fontFileName = "font/JournalDingbats3.ttf";
		journal_3.fontParameters.size = scale_fonts(190);
		journal_3.fontParameters.shadowOffsetX = -3;
		journal_3.fontParameters.shadowOffsetY = 3;
		journal_3.fontParameters.color = Color.WHITE;
		manager.load("journal_3.ttf", BitmapFont.class, journal_3);

		manager.load(blue_goo);
		manager.load(green_goo);
		manager.load(magenta_goo);

		manager.load(blue_line);
		manager.load(green_line);
		manager.load(magenta_line);
		manager.load(white_line);

		manager.load(magenta_circle);
		manager.load(blue_circle);

		manager.load(action_1);
		manager.load(action_2);
		manager.load(goo_sound);
		manager.load(ping_sound);
		manager.load(splash_sound);

		TextureParameter param = new TextureParameter();
		param.minFilter = TextureFilter.Linear;
		param.magFilter = TextureFilter.Linear;
		param.genMipMaps = true;

		manager.load("levels/1.png", Texture.class, param);
		manager.load("levels/2.png", Texture.class, param);
		manager.load("levels/3.png", Texture.class, param);
		manager.load("levels/4.png", Texture.class, param);
		manager.load("levels/5.png", Texture.class, param);
		manager.load("levels/6.png", Texture.class, param);
		manager.load("levels/7.png", Texture.class, param);
		manager.load("levels/8.png", Texture.class, param);
		manager.load("levels/9.png", Texture.class, param);
		manager.load("levels/10.png", Texture.class, param);
		manager.load("levels/11.png", Texture.class, param);
		manager.load("levels/12.png", Texture.class, param);
		manager.load("levels/13.png", Texture.class, param);
		manager.load("levels/14.png", Texture.class, param);
		manager.load("levels/15.png", Texture.class, param);
		manager.load("levels/16.png", Texture.class, param);
		manager.load("levels/17.png", Texture.class, param);
		manager.load("levels/18.png", Texture.class, param);
		manager.load("levels/19.png", Texture.class, param);
		manager.load("levels/20.png", Texture.class, param);
		manager.load("levels/21.png", Texture.class, param);
		manager.load("levels/22.png", Texture.class, param);
		manager.load("levels/23.png", Texture.class, param);
		manager.load("levels/24.png", Texture.class, param);
		manager.load("levels/25.png", Texture.class, param);
		manager.load("levels/26.png", Texture.class, param);
		manager.load("levels/27.png", Texture.class, param);
		manager.load("levels/28.png", Texture.class, param);
		manager.load("levels/29.png", Texture.class, param);
		manager.load("levels/30.png", Texture.class, param);
		manager.load("levels/31.png", Texture.class, param);
		manager.load("levels/32.png", Texture.class, param);
		manager.load("levels/33.png", Texture.class, param);
		manager.load("levels/34.png", Texture.class, param);
		manager.load("levels/35.png", Texture.class, param);
		manager.load("levels/36.png", Texture.class, param);
		manager.load("levels/37.png", Texture.class, param);
		manager.load("levels/38.png", Texture.class, param);
		manager.load("levels/39.png", Texture.class, param);
		manager.load("levels/40.png", Texture.class, param);
		manager.load("levels/41.png", Texture.class, param);
		manager.load("levels/42.png", Texture.class, param);
		manager.load("levels/43.png", Texture.class, param);
		manager.load("levels/44.png", Texture.class, param);
		manager.load("levels/45.png", Texture.class, param);
		manager.load("levels/46.png", Texture.class, param);
		manager.load("levels/47.png", Texture.class, param);
		manager.load("levels/48.png", Texture.class, param);

		manager.load("levels/coming_soon.png", Texture.class, param);				

	}

	public int scale_fonts(int size) {
		// divide the new screen width by the old width
		int font = Gdx.graphics.getWidth() * size / 1920;
		return font;
	}

	public void dispose() {
		manager.dispose();
	}
}
