package meca3dcustom.meca;

import java.util.HashMap;
import java.util.Map.Entry;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import meca3dcustom.math.Vec3D;

public class SolidWrapper {

	private Solid solid;
	private HashMap<SolidWrapper, Link> links;
	private int id;
	private String name;

	public SolidWrapper(Solid solid) {
		this.solid = solid;
		this.links = new HashMap<>();
	}

	public void render(GLAutoDrawable drawable, boolean[] done) {
		GL2 gl = drawable.getGL().getGL2();
		solid.render(drawable);
		done[id] = true;
		for (Entry<SolidWrapper, Link> entry : links.entrySet()) {
			if (done[entry.getKey().id])
				continue;
			if (entry.getValue().getS1() == this) {
				gl.glPushMatrix();
				Vec3D attach1 = entry.getValue().getAttach1();
				Vec3D attach2 = entry.getValue().getAttach2();
				gl.glTranslated(attach1.x, attach1.y, attach1.z);
				entry.getValue().apply(drawable);
				gl.glTranslated(-attach2.x, -attach2.y, -attach2.z);
				entry.getKey().render(drawable, done);
				gl.glPopMatrix();
			} else {
				gl.glPushMatrix();
				Vec3D attach1 = entry.getValue().getAttach2();
				Vec3D attach2 = entry.getValue().getAttach1();
				gl.glTranslated(attach1.x, attach1.y, attach1.z);
				entry.getValue().applyReverse(drawable);
				gl.glTranslated(-attach2.x, -attach2.y, -attach2.z);
				entry.getKey().render(drawable, done);
				gl.glPopMatrix();
			}
		}
	}

	public Solid getSolid() {
		return solid;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public HashMap<SolidWrapper, Link> getLinks() {
		return links;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}