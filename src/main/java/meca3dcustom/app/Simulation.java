package meca3dcustom.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import meca3dcustom.math.Matrix;
import meca3dcustom.math.Vec3D;
import meca3dcustom.meca.Link;
import meca3dcustom.meca.RotationLink;
import meca3dcustom.meca.SolidWrapper;
import meca3dcustom.meca.TranslationLink;

public class Simulation {

	private Matrix position, speed;
	private double delta = 1.0 / 60.0;
	private Model model;
	private int rotations, translations;
	private int[] parent, parentToLink, order, distanceToBase, rotationIndex, translationIndex;
	private boolean logging = false;

	// Simulation data
	private Vec3D[] omegas;
	private Vec3D[] coorTrans;
	private Matrix[] coorRot;

	public Simulation(Model model) {
		this.model = model;
		parent = new int[model.getSolidArr().length];
		parentToLink = new int[parent.length];
		order = new int[parent.length];
		distanceToBase = new int[parent.length];
		for (int i = 0; i < parent.length; i++) {
			parent[i] = -1;
			parentToLink[i] = -1;
		}
		List<SolidWrapper> next = new ArrayList<>();
		next.add(model.getBase());

		int i = 0;
		distanceToBase[model.getBase().getID()] = 0;
		while (!next.isEmpty()) {
			List<SolidWrapper> temp = new ArrayList<>();
			for (SolidWrapper s : next) {
				order[i++] = s.getID();
				for (Link l : s.getLinks().values()) {
					SolidWrapper other = l.getOther(s);
					if (parent[other.getID()] == -1 && other != model.getBase()) {
						parent[other.getID()] = s.getID();
						parentToLink[other.getID()] = l.getID();
						distanceToBase[other.getID()] = distanceToBase[s.getID()] + 1;
						temp.add(other);
						if (l instanceof RotationLink) {
							rotations++;
						}
						if (l instanceof TranslationLink) {
							translations++;
						}
					}
				}
			}
			next = temp;
		}

		System.out.println("Translations: " + translations);
		System.out.println("Rotations: " + rotations);
		System.out.println("Used order: " + Arrays.stream(order)
				.mapToObj(index -> model.getSolidArr()[index].toString()).collect(Collectors.joining("->")));
		position = new Matrix(rotations + translations, 1);
		speed = new Matrix(rotations + translations, 1);
		rotationIndex = new int[rotations + translations];
		translationIndex = new int[translations + translations];
		int tr = 0, ro = 0;
		for (i = 1; i < order.length; i++) {
			if (model.getLinkArr()[parentToLink[order[i]]] instanceof TranslationLink) {
				translationIndex[parentToLink[order[i]]] = tr++;
			}

			if (model.getLinkArr()[parentToLink[order[i]]] instanceof RotationLink) {
				rotationIndex[parentToLink[order[i]]] = ro++;
			}
		}

	}

	public void setDefaultRotation(String name, double value) {
		Link l = model.getLinks().get(name);
		if (l instanceof RotationLink rot) {
			position.data[rotationIndex[rot.getID()] + translations][0] = value;
		}
	}

	public void update() {

	}

	public Matrix solveFor() {
		return solveFor(position, speed);
	}

	private Matrix solveFor(Matrix position, Matrix speed) {
		// Rotation vectors
		omegas = new Vec3D[order.length];
		omegas[order[0]] = new Vec3D(0, 0, 0);

		// Affine coordinates changes
		coorTrans = new Vec3D[order.length];
		coorRot = new Matrix[order.length];
		coorTrans[order[0]] = new Vec3D(0, 0, 0);
		coorRot[order[0]] = Matrix.diag(1, 1, 1);

		// Matrix of the system to solve
		Matrix A = new Matrix(translations + rotations, translations + rotations);
		Matrix B = new Matrix(translations + rotations, 1);
		Matrix C = new Matrix(translations + rotations, 1);
		Matrix F = new Matrix(translations + rotations, 1);

		for (int i = 1; i < order.length; i++) {
			Link link = model.getLinkArr()[parentToLink[order[i]]];
			SolidWrapper previous = model.getSolidArr()[order[i - 1]];
			SolidWrapper solid = model.getSolidArr()[order[i]];

			if (link instanceof RotationLink rotLink) {
				Matrix rot = Matrix.rotMat(rotLink.axis, position.data[translations + rotationIndex[link.getID()]][0]);
				coorRot[order[i]] = coorRot[order[i - 1]].dot(rot);
				coorTrans[order[i]] = coorTrans[order[i - 1]]
						.add(coorRot[order[i - 1]].dot(rotLink.getAttach(previous)))
						.sub(coorRot[order[i]].dot(rotLink.getAttach(solid)));
				omegas[order[i]] = omegas[order[i - 1]]
						.add(rotLink.axis.scale(speed.data[translations + rotationIndex[link.getID()]][0]));
			}
			if (link instanceof TranslationLink transLink) {
				coorRot[order[i]] = coorRot[order[i - 1]];
				coorTrans[order[i]] = coorTrans[order[i - 1]].add(transLink.getAttach(previous))
						.sub(transLink.getAttach(solid))
						.add(transLink.axis.scale(position.data[translationIndex[link.getID()]][0]));
				omegas[order[i]] = omegas[order[i - 1]];
			}
			if (logging) {
				System.out.println("For solid " + solid);
				System.out.println(coorRot[order[i]]);
				System.out.println(coorTrans[order[i]]);
			}
		}

		// System.out.println(A);

		// return A.invert(0.000001).dot(F.sub(B.add(C)));
		return null;
	}

	public Vec3D getCoordsInBasis(int solid, Matrix[] coorRot, Vec3D[] coorTrans, Vec3D target) {
		return coorRot[solid].dot(target).add(coorTrans[solid]);
	}

	public Vec3D getCoordsInBasis(int solid, Vec3D target) {
		return coorRot[solid].dot(target).add(coorTrans[solid]);
	}

	public Vec3D getCoordsInBasisByOrder(int solidInOrder, Matrix[] coorRot, Vec3D[] coorTrans, Vec3D target) {
		return coorRot[order[solidInOrder]].dot(target).add(coorTrans[order[solidInOrder]]);
	}

	public Vec3D getCoordsInBasisByOrder(int solidInOrder, Vec3D target) {
		return getCoordsInBasisByOrder(solidInOrder, coorRot, coorTrans, target);
	}

	public Matrix getGamma(int solid, Matrix position, Matrix speed, Matrix[] coorRot, Vec3D[] coorTrans,
			Vec3D[] omegas, Vec3D target) {
		Matrix result = new Matrix(3, translations + rotations);
		int current = solid;
		Vec3D currentPoint = coorRot[current].dot(target).add(coorTrans[current]);
		// System.out.println("Getting gamma");
		while (current != order[0]) {
			int parentID = parent[current];
			// System.out.println("Currently at " + model.getSolidArr()[current]);
			Link link = model.getLinkArr()[parentToLink[current]];
			// System.out.println("Looking at link " + link);
			if (link instanceof TranslationLink transLink) {
				Vec3D axis = coorRot[parentID].dot(transLink.axis)
						.scale(link.isDirect(model.getSolidArr()[current]) ? 1 : -1);
				result.data[0][translationIndex[transLink.getID()]] = axis.x;
				result.data[1][translationIndex[transLink.getID()]] = axis.y;
				result.data[2][translationIndex[transLink.getID()]] = axis.z;
			}
			if (link instanceof RotationLink rotLink) {
				Vec3D axis = coorRot[parentID].dot(rotLink.axis)
						.vecProd(currentPoint
								.sub(getCoordsInBasis(current, rotLink.getAttach(model.getSolidArr()[current]))))
						.scale(link.isDirect(model.getSolidArr()[current]) ? -1 : 1);
				result.data[0][rotationIndex[rotLink.getID()] + translations] = axis.x;
				result.data[1][rotationIndex[rotLink.getID()] + translations] = axis.y;
				result.data[2][rotationIndex[rotLink.getID()] + translations] = axis.z;
			}
			current = parent[current];
		}
		return result;
	}

	public Matrix getLambda(int solid, Matrix position, Matrix speed, Matrix[] coorRot, Vec3D[] coorTrans,
			Vec3D[] omegas, Vec3D target) {
		Vec3D result = new Vec3D(0, 0, 0);
		int current = solid;
		Vec3D currentPoint = getCoordsInBasis(solid, coorRot, coorTrans, target);
		Vec3D previous = currentPoint;
		while (current != order[0]) {
			int parentID = parent[current];
			Link link = model.getLinkArr()[parentToLink[current]];
			if (link instanceof TranslationLink transLink) {
				result = result.add(omegas[parentID].vecProd(coorRot[parentID].dot(transLink.axis))
						.scale(speed.data[translationIndex[transLink.getID()]][0]));
			}
			if (link instanceof RotationLink rotLink) {
				result = result.add(previous);
			}
		}
		return new Matrix(result);
	}

	public Matrix getPosition() {
		return position;
	}

	public Matrix getSpeed() {
		return speed;
	}

	public Vec3D[] getCoorTrans() {
		return coorTrans;
	}

	public Matrix[] getCoorRot() {
		return coorRot;
	}

	public Vec3D[] getOmegas() {
		return omegas;
	}

	public int[] getOrder() {
		return order;
	}

}