package scribbly.scribbles.scribbler.core;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Audio {

	public static Music action_1;
	public static Music action_2;
	public static Sound goo_sound;
	public static Sound ping_sound;
	public static Sound splash_sound;

	public static void create() {
		action_1 = Assets.manager.get(Assets.action_1);
		action_2 = Assets.manager.get(Assets.action_2);
		goo_sound = Assets.manager.get(Assets.goo_sound);
		ping_sound = Assets.manager.get(Assets.ping_sound);
		splash_sound = Assets.manager.get(Assets.splash_sound);
	}

	public static void dispose() {
		action_1.dispose();
		action_2.dispose();
		goo_sound.dispose();
		ping_sound.dispose();
		splash_sound.dispose();
	}
}
