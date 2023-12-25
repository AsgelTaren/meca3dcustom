package meca3dcustom.app;

import java.util.HashMap;

import com.google.gson.JsonObject;

import meca3dcustom.meca.Link;
import meca3dcustom.meca.Solid;
import meca3dcustom.meca.SolidWrapper;

public class Model {

	private HashMap<String, SolidWrapper> solids;
	private SolidWrapper base;
	private HashMap<String, Link> links;
	private SolidWrapper[] solidArr;
	private Link[] linkArr;

	public Model() {
		solids = new HashMap<>();
		links = new HashMap<>();
	}

	public Model(JsonObject obj) {
		solids = new HashMap<>();
		links = new HashMap<>();
		JsonObject solidsData = obj.get("solids").getAsJsonObject();
	}

	public void setup() {
		int i = 0;
		solidArr = new SolidWrapper[solids.size()];
		for (SolidWrapper s : solids.values()) {
			solidArr[i] = s;
			s.setID(i++);
			s.getSolid().setup();
		}
		i = 0;
		linkArr = new Link[links.size()];
		for (Link l : links.values()) {
			linkArr[i] = l;
			l.setID(i++);
		}
	}

	public void addSolid(String name, Solid solid) {
		SolidWrapper temp = new SolidWrapper(solid);
		temp.setName(name);
		solids.put(name, temp);
	}

	public void addLink(String name, Link l) {
		l.getS1().getLinks().put(l.getS2(), l);
		l.getS2().getLinks().put(l.getS1(), l);
		l.setName(name);
		links.put(name, l);
	}

	public void setBase(SolidWrapper s) {
		this.base = s;
	}

	public SolidWrapper getBase() {
		return base;
	}

	public HashMap<String, SolidWrapper> getSolids() {
		return solids;
	}

	public HashMap<String, Link> getLinks() {
		return links;
	}

	public SolidWrapper[] getSolidArr() {
		return solidArr;
	}

	public Link[] getLinkArr() {
		return linkArr;
	}

}
