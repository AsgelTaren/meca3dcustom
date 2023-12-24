package meca3dcustom.meca;

import com.jogamp.opengl.GLAutoDrawable;

import meca3dcustom.math.Vec3D;

public abstract class Link {

	private SolidWrapper s1, s2;
	private Vec3D attach1, attach2;
	private int id;
	private String name;

	public Link(SolidWrapper s1, SolidWrapper s2, Vec3D attach1, Vec3D attach2) {
		this.s1 = s1;
		this.s2 = s2;
		this.attach1 = attach1;
		this.attach2 = attach2;
	}

	public SolidWrapper getS1() {
		return s1;
	}

	public SolidWrapper getS2() {
		return s2;
	}

	public Vec3D getAttach1() {
		return attach1;
	}

	public Vec3D getAttach2() {
		return attach2;
	}

	public Vec3D getAttach(SolidWrapper s) {
		return isDirect(s) ? attach1 : attach2;
	}

	public SolidWrapper getOther(SolidWrapper s) {
		return isDirect(s) ? s2 : s1;
	}

	public boolean isDirect(SolidWrapper s) {
		return s == s1;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public abstract void apply(GLAutoDrawable drawable);

	public abstract void applyReverse(GLAutoDrawable drawable);

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}