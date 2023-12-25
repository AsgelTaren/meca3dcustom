package meca3dcustom.app;

import java.util.HashMap;
import java.util.function.Function;

import com.google.gson.JsonObject;

import meca3dcustom.meca.DefaultSolid;
import meca3dcustom.meca.Link;
import meca3dcustom.meca.SimpleSolid;
import meca3dcustom.meca.Solid;
import meca3dcustom.meca.SolidGroup;

public class GlobalModelRegistry {

	private HashMap<String, Function<JsonObject, Solid>> solidRegistry;
	private HashMap<String, Function<JsonObject, Link>> linkRegistry;

	public GlobalModelRegistry() {
		solidRegistry = new HashMap<>();
		linkRegistry = new HashMap<>();
	}

	public void init() {
		solidRegistry.put("default", (data) -> new DefaultSolid(data));
		solidRegistry.put("rectangle", (data) -> SimpleSolid.getRectangle(data));
		solidRegistry.put("arc", (data) -> SimpleSolid.getArc(data));
		solidRegistry.put("solid_group", (data) -> new SolidGroup(this, data));
	}

	public Solid getSolidFor(JsonObject solid) {
		return solidRegistry.get(solid.get("solid_type").getAsString())
				.apply(solid.get("solid_data").getAsJsonObject());
	}

}
