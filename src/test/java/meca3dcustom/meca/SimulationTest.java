package meca3dcustom.meca;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import meca3dcustom.app.Model;
import meca3dcustom.app.Simulation;
import meca3dcustom.math.Matrix;
import meca3dcustom.math.Vec3D;

public class SimulationTest {

	@Test
	@DisplayName("Validating of global calculations #1")
	void validateGlobal1() {
		Model model = new Model();

		model.addSolid("base", new DefaultSolid(new Vec3D(0, 0, 0), new Matrix(3, 3), 1));

		model.addSolid("s1", new DefaultSolid(new Vec3D(0, 0, 0), new Matrix(3, 3), 1));
		model.addLink("rot1", new RotationLink(model.getSolids().get("base"), model.getSolids().get("s1"),
				new Vec3D(0, 0, 0), new Vec3D(1, -2, 3), new Vec3D(1, 0, 0)));

		model.addSolid("s2", new DefaultSolid(new Vec3D(0, 0, 0), new Matrix(3, 3), 1));
		model.addLink("rot2", new RotationLink(model.getSolids().get("s1"), model.getSolids().get("s2"),
				new Vec3D(4, 5, 6), new Vec3D(1, 2, 3), new Vec3D(0, 1, 0)));

		model.setBase(model.getSolids().get("base"));

		model.setup();

		Simulation sim = new Simulation(model);
		sim.setDefaultRotation("rot1", Math.PI / 6);
		sim.setDefaultRotation("rot2", Math.PI / 3);
		sim.solveFor();

		Vec3D target = new Vec3D(1, 3, 5);

		// Validating coords for s1
		assertTrue(
				sim.getCoordsInBasisByOrder(1, target).equals(new Vec3D(0, 2.5 * Math.sqrt(3) - 1, 2.5 + Math.sqrt(3)),
						0.000001),
				sim.getCoordsInBasisByOrder(1, target) //
						+ " -> "//
						+ new Vec3D(0, 2.5 * Math.sqrt(3) - 1, 2.5 + Math.sqrt(3))//
		);

		// Validating coords for s2
		assertTrue(
				sim.getCoordsInBasisByOrder(2, target)
						.equals(new Vec3D(Math.sqrt(3) + 3, 4 * Math.sqrt(3) - 2, 2 * Math.sqrt(3) + 4), 0.000001),
				sim.getCoordsInBasisByOrder(2, target) //
						+ " -> "//
						+ new Vec3D(Math.sqrt(3) + 3, 4 * Math.sqrt(3) - 2, 2 * Math.sqrt(3) + 4)//
		);

		// Validating gamma #1
		Matrix gamma = sim.getGamma(sim.getOrder()[1], sim.getPosition(), sim.getSpeed(), sim.getCoorRot(),
				sim.getCoorTrans(), sim.getOmegas(), new Vec3D(3, 2, 4));
		assertTrue(gamma.equals(
				new Matrix(new Vec3D(0, -2 - 0.5 * Math.sqrt(3), 2 * Math.sqrt(3) - 0.5), new Vec3D(0, 0, 0)),
				0.000001));
		gamma = sim.getGamma(sim.getOrder()[2], sim.getPosition(), sim.getSpeed(), sim.getCoorRot(), sim.getCoorTrans(),
				sim.getOmegas(), new Vec3D(3, 2, 4));

		// Validating gamma #2
		assertTrue(gamma.equals(
				new Matrix(new Vec3D(0, -7.0 / 4.0 * Math.sqrt(3) - 2, 4 * Math.sqrt(3) - 7.0 / 4.0),
						new Vec3D(-Math.sqrt(3) + 0.5, Math.sqrt(3) / 4 + 0.5, -0.5 * Math.sqrt(3) - 3.0 / 4.0)),
				0.000001));
	}

	@Test
	@DisplayName("")
	void validateInertia1() {
		SolidGroup group = new SolidGroup();
		group.addSolid(new DefaultSolid(//
				new Vec3D(1, 2, 3), //
				new Matrix(//
						new double[] { 1, 0, 0 }, //
						new double[] { 0, 2, 0 }, //
						new double[] { 0, 0, 3 }//
				), 1), new Vec3D(2, -3, -1), Math.PI / 3, Math.PI / 6, 0);
		group.setup();
		System.out.println(group.inertiaMatrix());
	}

}