package meca3dcustom.meca;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import meca3dcustom.math.Vec3D;

public class TranslationLink extends Link {

	public double t;
	public Vec3D axis;

	public TranslationLink(SolidWrapper s1, SolidWrapper s2, Vec3D attach1, Vec3D attach2, Vec3D axis) {
		super(s1, s2, attach1, attach2);
		this.axis = axis;
	}

	@Override
	public void apply(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		Vec3D temp = axis.scale(t);
		gl.glTranslated(temp.x, temp.y, temp.z);
	}

	@Override
	public void applyReverse(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		Vec3D temp = axis.scale(-t);
		gl.glTranslated(temp.x, temp.y, temp.z);

	}

}
