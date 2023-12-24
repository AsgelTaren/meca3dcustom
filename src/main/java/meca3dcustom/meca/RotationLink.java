package meca3dcustom.meca;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import meca3dcustom.math.Vec3D;

public class RotationLink extends Link {

	public double rot;
	public Vec3D axis;

	public RotationLink(SolidWrapper s1, SolidWrapper s2, Vec3D attach1, Vec3D attach2, Vec3D axis) {
		super(s1, s2, attach1, attach2);
		this.axis = axis;
	}

	@Override
	public void apply(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glRotated(rot, axis.x, axis.y, axis.z);
	}

	@Override
	public void applyReverse(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glRotated(-rot, axis.x, axis.y, axis.z);
	}

}