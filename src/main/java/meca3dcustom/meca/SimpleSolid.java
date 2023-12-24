package meca3dcustom.meca;

import java.awt.Color;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import meca3dcustom.math.Matrix;
import meca3dcustom.math.Vec3D;

public class SimpleSolid extends Solid {

	private Vec3D inertiaCenter;
	private Matrix inertiaMatrix;
	private double mass;

	private DoubleBuffer verticesBuff, colorsBuff;
	private int size;

	public SimpleSolid(Vec3D inertiaCenter, Matrix inertiaMatrix, double mass, Vec3D[] vertices, Color[] colors) {
		this.inertiaCenter = inertiaCenter;
		this.inertiaMatrix = inertiaMatrix;
		this.mass = mass;
		this.size = vertices.length;

		double[] temp = new double[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			temp[3 * i] = vertices[i].x;
			temp[3 * i + 1] = vertices[i].y;
			temp[3 * i + 2] = vertices[i].z;
		}

		verticesBuff = Buffers.newDirectDoubleBuffer(temp);

		temp = new double[colors.length * 12];
		for (int i = 0; i < colors.length; i++) {
			for (int j = 0; j < 4; j++) {
				Color c = colors[i];
				temp[12 * i + 3 * j] = c.getRed() / 255.0;
				temp[12 * i + 3 * j + 1] = c.getGreen() / 255.0;
				temp[12 * i + 3 * j + 2] = c.getBlue() / 255.0;
			}
		}

		colorsBuff = Buffers.newDirectDoubleBuffer(temp);

	}

	public SimpleSolid(Vec3D inertiaCenter, Matrix inertiaMatrix, double mass, List<? extends Vec3D> vertices,
			List<? extends Color> colors) {
		this.inertiaCenter = inertiaCenter;
		this.inertiaMatrix = inertiaMatrix;
		this.mass = mass;
		this.size = vertices.size();

		double[] temp = new double[vertices.size() * 3];
		for (int i = 0; i < vertices.size(); i++) {
			temp[3 * i] = vertices.get(i).x;
			temp[3 * i + 1] = vertices.get(i).y;
			temp[3 * i + 2] = vertices.get(i).z;
		}

		verticesBuff = Buffers.newDirectDoubleBuffer(temp);

		temp = new double[colors.size() * 12];
		for (int i = 0; i < colors.size(); i++) {
			for (int j = 0; j < 4; j++) {
				Color c = colors.get(i);
				temp[12 * i + 3 * j] = c.getRed() / 255.0;
				temp[12 * i + 3 * j + 1] = c.getGreen() / 255.0;
				temp[12 * i + 3 * j + 2] = c.getBlue() / 255.0;
			}
		}

		colorsBuff = Buffers.newDirectDoubleBuffer(temp);

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
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl.glVertexPointer(3, GL2.GL_DOUBLE, 0, verticesBuff);
		gl.glColorPointer(3, GL2.GL_DOUBLE, 0, colorsBuff);

		gl.glDrawArrays(GL2.GL_QUADS, 0, size);
	}

	public static final SimpleSolid getRectangle(double lx, double ly, double lz, double mass, Color c) {
		Vec3D[] points = new Vec3D[] { //
				new Vec3D(lx / 2, ly / 2, -lz / 2), new Vec3D(-lx / 2, ly / 2, -lz / 2),
				new Vec3D(-lx / 2, -ly / 2, -lz / 2), new Vec3D(lx / 2, -ly / 2, -lz / 2),
				new Vec3D(lx / 2, ly / 2, lz / 2), new Vec3D(-lx / 2, ly / 2, lz / 2),
				new Vec3D(-lx / 2, -ly / 2, lz / 2), new Vec3D(lx / 2, -ly / 2, lz / 2), };

		List<Vec3D> vertices = new ArrayList<>();
		List<Color> colors = new ArrayList<>();

		// Down face
		vertices.addAll(Arrays.asList(points[0], points[1], points[2], points[3]));
		colors.add(scale(c, 0.8));

		// Up face
		vertices.addAll(Arrays.asList(points[4], points[5], points[6], points[7]));
		colors.add(scale(c, 0.8));

		// Front face
		vertices.addAll(Arrays.asList(points[0], points[4], points[7], points[3]));
		colors.add(c);

		// Back face
		vertices.addAll(Arrays.asList(points[1], points[2], points[6], points[5]));
		colors.add(scale(c, 0.6));

		// Left face
		vertices.addAll(Arrays.asList(points[2], points[3], points[7], points[6]));
		colors.add(scale(c, 0.7));

		// Right face
		vertices.addAll(Arrays.asList(points[0], points[1], points[5], points[4]));
		colors.add(scale(c, 0.7));

		Matrix inertiaMatrix = Matrix.diag(ly * ly + lz * lz, lx * lx + lz * lz, lx * lx + ly * ly).scale(mass / 12.0);

		return new SimpleSolid(new Vec3D(0, 0, 0), inertiaMatrix, mass, vertices, colors);
	}

	public static final SimpleSolid getArc(double r1, double r2, double a, double theta1, double theta2, double mass,
			int subdiv, Color c) {
		Vec3D[] points = new Vec3D[(subdiv + 1) * 4];
		for (int i = 0; i <= subdiv; i++) {
			double cos = Math.cos(theta1 + (theta2 - theta1) / subdiv * i);
			double sin = Math.sin(theta1 + (theta2 - theta1) / subdiv * i);
			points[4 * i] = new Vec3D(cos * r1, sin * r1, -a / 2);
			points[4 * i + 1] = new Vec3D(cos * r2, sin * r2, -a / 2);
			points[4 * i + 2] = new Vec3D(cos * r2, sin * r2, a / 2);
			points[4 * i + 3] = new Vec3D(cos * r1, sin * r1, a / 2);
		}

		List<Vec3D> vertices = new ArrayList<>();
		List<Color> colors = new ArrayList<>();
		for (int i = 0; i < subdiv; i++) {
			for (int j = 0; j < 4; j++) {
				vertices.addAll(Arrays.asList(points[4 * i + j], points[4 * i + ((j + 1) % 4)],
						points[4 * i + 4 + ((j + 1) % 4)], points[4 * i + 4 + j]));
				colors.add(scale(c, 1 - 0.05 * j));
			}
		}

		vertices.addAll(Arrays.asList(points[0], points[1], points[2], points[3]));
		colors.add(c);

		vertices.addAll(Arrays.asList(points[4 * subdiv], points[4 * subdiv + 1], points[4 * subdiv + 2],
				points[4 * subdiv + 3]));
		colors.add(c);

		return new SimpleSolid(new Vec3D(0, 0, 0), new Matrix(3, 3), mass, vertices, colors);
	}

	public static final Color scale(Color c, double scale) {
		return new Color((int) (c.getRed() * scale), (int) (c.getGreen() * scale), (int) (c.getBlue() * scale),
				(int) (c.getAlpha() * scale));
	}

	@Override
	public double getMass() {
		return mass;
	}

	@Override
	public void setup() {

	}

}
