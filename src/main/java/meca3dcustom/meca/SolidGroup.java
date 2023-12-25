package meca3dcustom.meca;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import meca3dcustom.app.GlobalModelRegistry;
import meca3dcustom.math.Matrix;
import meca3dcustom.math.Vec3D;

public class SolidGroup extends Solid {

	private List<SolidPart> parts;
	private Vec3D inertiaCenter;
	private Matrix inertiaMatrix;
	private double mass;

	public SolidGroup() {
		this.parts = new ArrayList<>();
	}

	public SolidGroup(GlobalModelRegistry registry, JsonObject data) {
		this.parts = new ArrayList<>();
		JsonArray array = data.get("solids").getAsJsonArray();
		for (int i = 0; i < array.size(); i++) {
			// parts.add();
		}
	}

	public void addSolid(Solid solid, Vec3D pos, double rx, double ry, double rz) {
		parts.add(new SolidPart(solid, pos, rx, ry, rz));
	}

	@Override
	public Vec3D inertiaCenter() {
		return inertiaCenter;
	}

	@Override
	public Matrix inertiaMatrix() {
		return inertiaMatrix;
	}

	@Override
	public void render(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		for (SolidPart part : parts) {
			gl.glPushMatrix();
			gl.glTranslated(part.pos.x, part.pos.y, part.pos.z);
			gl.glRotated(part.rx, 1, 0, 0);
			gl.glRotated(part.ry, 0, 1, 0);
			gl.glRotated(part.rz, 0, 0, 1);
			part.solid.render(drawable);
			gl.glPopMatrix();
		}
	}

	private class SolidPart {
		private Solid solid;
		private Vec3D pos;
		private double rx, ry, rz;

		private SolidPart(Solid solid, Vec3D pos, double rx, double ry, double rz) {
			this.solid = solid;
			this.pos = pos;
			this.rx = rx;
			this.ry = ry;
			this.rz = rz;
		}

	}

	@Override
	public double getMass() {
		return mass;
	}

	@Override
	public void setup() {
		mass = 0;
		inertiaCenter = new Vec3D(0, 0, 0);
		inertiaMatrix = new Matrix(3, 3);
		for (SolidPart s : parts) {
			mass += s.solid.getMass();
			Matrix rot = Matrix.rotMat(new Vec3D(1, 0, 0), s.rx).dot(Matrix.rotMat(new Vec3D(0, 1, 0), s.ry))
					.dot(Matrix.rotMat(new Vec3D(0, 0, 1), s.rz));
			inertiaCenter = inertiaCenter.add(rot.dot(s.solid.inertiaCenter()).add(s.pos).scale(s.solid.getMass()));
			inertiaMatrix = inertiaMatrix.add(rot.dot(s.solid.inertiaMatrix()).dot(rot.transpose())
					.sub(Matrix.doubleVecProd(rot.dot(s.solid.inertiaCenter()).add(s.pos)).scale(s.solid.getMass())));
		}
		inertiaCenter = inertiaCenter.scale(1.0 / mass);
	}

}