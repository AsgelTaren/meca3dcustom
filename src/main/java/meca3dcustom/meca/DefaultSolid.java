package meca3dcustom.meca;

import com.jogamp.opengl.GLAutoDrawable;

import meca3dcustom.math.Matrix;
import meca3dcustom.math.Vec3D;

public class DefaultSolid extends Solid {

	private Vec3D inertiaCenter;
	private Matrix inertiaMatrix;
	private double mass;

	public DefaultSolid(Vec3D inertiaCenter, Matrix inertiaMatrix, double mass) {
		this.inertiaCenter = inertiaCenter;
		this.inertiaMatrix = inertiaMatrix;
		this.mass = mass;
	}

	@Override
	public Vec3D inertiaCenter() {
		return inertiaCenter;
	}

	@Override
	public double getMass() {
		return mass;
	}

	@Override
	public Matrix inertiaMatrix() {
		return inertiaMatrix;
	}

	@Override
	public void render(GLAutoDrawable drawable) {

	}

	@Override
	public void setup() {

	}

}
