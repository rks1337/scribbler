package scribbly.scribbles.scribbler.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class Zone_Loader {

	private Json json;
	private FileHandle file = Gdx.files.local("zone.json");

	public Array<Array<Vector2>> loaded_lines = new Array<Array<Vector2>>();
	public Array<Vector2> loaded_goo = new Array<Vector2>();

	public Zone_Loader() {
		json = new Json();
	}

	public void save_zone(Array<Array<Vector2>>lines, Array<Vector2> goo) {

		if (file.exists()) {
			file.delete();
		}

		Zone z = new Zone(lines, goo);

		System.out.println(json.prettyPrint(z));
		String s = json.toJson(z);

		file.writeString(s, true);
	}

	public void load_zone(FileHandle f) { //FileHandle file
		loaded_lines.clear();
		loaded_goo.clear();

		JsonValue json = new JsonReader().parse(f);

		//load lines
		JsonValue line_values = json.get("lines");

		for (int j = 0; j < line_values.size; j++) {

			Array<Vector2> line = new Array<Vector2>();

			for(int i = 0; i < line_values.get(j).size ; i++){

				//detect seperate lines
				if (line_values.get(j).get(i).getFloat("x") == -1) {
					break;
				}
				line.add(new Vector2(line_values.get(j).get(i).getFloat("x"), line_values.get(j).get(i).getFloat("y")));
			}

			loaded_lines.add(line);
		}


		//load goo
		JsonValue goo_values = json.get("goo");

		for (int i = 0; i < goo_values.size; i++) {
			loaded_goo.add(new Vector2(goo_values.get(i).getFloat("x"), goo_values.get(i).getFloat("y")));
		}
	}

	public static class Zone {
		public Array<Array<Vector2>> lines;
		public Array<Vector2> goo;

		public Zone(Array<Array<Vector2>> lines, Array<Vector2> goo) {
			this.lines = lines;
			this.goo = goo;
		}
	}
}
