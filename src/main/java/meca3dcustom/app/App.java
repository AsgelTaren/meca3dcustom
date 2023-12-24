package meca3dcustom.app;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import meca3dcustom.math.Vec3D;
import meca3dcustom.meca.RotationLink;
import meca3dcustom.meca.SimpleSolid;
import meca3dcustom.meca.Solid;
import meca3dcustom.meca.SolidGroup;

public class App implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	// Camera
	public float alpha, beta, zoom = 1.0f;
	private float lastX, lastY;

	@SuppressWarnings("unused")
	private GLU glu = new GLU();

	private Model model;
	private Simulation sim;

	public App() {

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

		model = new Model();

		model.addSolid("base", SimpleSolid.getRectangle(1, 1, 3, 10, Color.GREEN));

		SolidGroup piece1 = new SolidGroup();
		piece1.addSolid(SimpleSolid.getRectangle(0.5, 1.5, 1, 10, Color.RED), new Vec3D(0.25, 0.25, 0), 0, 0, 0);
		piece1.addSolid(SimpleSolid.getRectangle(2, 0.5, 1, 10, Color.RED), new Vec3D(1.5, 0.75, 0), 0, 0, 0);
		model.addSolid("l-piece", piece1);

		model.addLink("rot1", new RotationLink(model.getSolids().get("base"), model.getSolids().get("l-piece"),
				new Vec3D(0.5, 0, 1), new Vec3D(0, 0, 0), new Vec3D(1, 0, 0)));

		SolidGroup arcGroup = new SolidGroup();
		Solid radioBasis = SimpleSolid.getRectangle(0.75, 1, 0.75, 2, Color.GRAY);
		// Solid radioPillar = SimpleSolid.getRectangle(0.5, 0.8, 1, 2, Color.MAGENTA);

		arcGroup.addSolid(SimpleSolid.getArc(1, 1.5, 0.5, -Math.PI / 8, Math.PI + Math.PI / 8, 20, Color.BLUE),
				new Vec3D(0, 0, 0), 90, 0, 90);
		arcGroup.addSolid(radioBasis, new Vec3D(0, -0.75, 1.25), 0, 0, 0);
		arcGroup.addSolid(radioBasis, new Vec3D(0, -0.75, -1.25), 0, 0, 0);
		model.addSolid("arc", arcGroup);

		model.addLink("rot2", new RotationLink(model.getSolids().get("l-piece"), model.getSolids().get("arc"),
				new Vec3D(4, 0.75, 0), new Vec3D(0, 0, 0), new Vec3D(0, 1, 0)));

		model.setBase(model.getSolids().get("base"));

		model.setup();

		sim = new Simulation(model);
		sim.setDefaultRotation("rot1", 0*Math.PI / 2);
		sim.solveFor();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0f, 0f, -5.0f);
		gl.glScalef(zoom, zoom, zoom);
		gl.glRotatef(alpha, 1, 0, 0);
		gl.glRotatef(beta, 0, 0, 1);

		boolean[] done = new boolean[model.getSolids().size()];
		((RotationLink) model.getLinks().get("rot1")).rot += 0.6;
		((RotationLink) model.getLinks().get("rot2")).rot += 0.8;
		model.getBase().render(drawable, done);

		gl.glEnd(); // Done Drawing The Quad
		gl.glFlush();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final GL2 gl = drawable.getGL().getGL2();

		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		gl.glOrtho(-10, 10, -10 / h, 10 / h, -5, 10);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void start() {
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);

		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseWheelListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseListener(this);
		canvas.setSize(400, 400);

		JFrame frame = new JFrame("Meca3DCustom");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.add(canvas);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		FPSAnimator anim = new FPSAnimator(canvas, 300, true);
		anim.start();

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoom *= Math.pow(1.12, e.getWheelRotation());

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		beta += (e.getX() - lastX) / 4.0;
		alpha += (e.getY() - lastY) / 4.0;
		lastX = e.getX();
		lastY = e.getY();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		lastX = e.getX();
		lastY = e.getY();

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
