package meca3dcustom.math;

public class Vec3D {

	public double x, y, z;

	public Vec3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3D add(Vec3D v) {
		return new Vec3D(this.x + v.x, this.y + v.y, this.z + v.z);
	}

	public Vec3D sub(Vec3D v) {
		return new Vec3D(this.x - v.x, this.y - v.y, this.z - v.z);
	}

	public Vec3D scale(double s) {
		return new Vec3D(this.x * s, this.y * s, this.z * s);
	}

	public Vec3D vecProd(Vec3D v) {
		return new Vec3D(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
	}

	public double normSQ() {
		return x * x + y * y + z * z;
	}

	public double norm() {
		return Math.sqrt(normSQ());
	}

	public Vec3D normalize() {
		return scale(1.0 / norm());
	}

	public boolean equals(Vec3D v, double epsilon) {
		if (Math.abs(this.x - v.x) > epsilon)
			return false;
		if (Math.abs(this.y - v.y) > epsilon)
			return false;
		if (Math.abs(this.z - v.z) > epsilon)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}
}