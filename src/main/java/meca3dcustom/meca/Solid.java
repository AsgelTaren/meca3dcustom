package meca3dcustom.meca;

import com.jogamp.opengl.GLAutoDrawable;

import meca3dcustom.math.Matrix;
import meca3dcustom.math.Vec3D;

public abstract class Solid {

	public abstract Vec3D inertiaCenter();
	public abstract double getMass();

	public abstract Matrix inertiaMatrix();

	public abstract void render(GLAutoDrawable drawable);
	public abstract void setup();

}