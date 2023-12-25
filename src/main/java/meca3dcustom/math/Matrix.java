package meca3dcustom.math;

import com.google.gson.JsonArray;

public class Matrix {

	public double[][] data;

	public Matrix(int rows, int cols) {
		this.data = new double[rows][cols];
	}

	public Matrix(double[]... data) {
		this.data = data;
	}

	public Matrix(Matrix mat) {
		this.data = new double[mat.getRows()][mat.getCols()];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				this.data[i][j] = mat.data[i][j];
			}
		}
	}

	public static final Matrix ofJson3x3(JsonArray array) {
		double[][] data = new double[3][3];
		for (int i = 0; i < 3; i++) {
			JsonArray arr = array.get(i).getAsJsonArray();
			for (int j = 0; j < 3; j++) {
				data[i][j] = arr.get(j).getAsDouble();
			}
		}
		return new Matrix(data);
	}

	public Matrix add(Matrix mat) {
		Matrix result = new Matrix(getRows(), getCols());
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				result.data[i][j] = this.data[i][j] + mat.data[i][j];
			}
		}
		return result;
	}

	public void directAdd(Matrix mat) {
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				this.data[i][j] += mat.data[i][j];
			}
		}
	}

	public Matrix(Vec3D... vecs) {
		this.data = new double[3][vecs.length];
		for (int i = 0; i < vecs.length; i++) {
			this.data[0][i] = vecs[i].x;
			this.data[1][i] = vecs[i].y;
			this.data[2][i] = vecs[i].z;
		}
	}

	public Matrix sub(Matrix mat) {
		Matrix result = new Matrix(getRows(), getCols());
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				result.data[i][j] = this.data[i][j] + mat.data[i][j];
			}
		}
		return result;
	}

	public Matrix scale(double s) {
		Matrix result = new Matrix(getRows(), getCols());
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				result.data[i][j] = this.data[i][j] * s;
			}
		}
		return result;
	}

	public Matrix dot(Matrix mat) {
		Matrix result = new Matrix(getRows(), mat.getCols());
		for (int i = 0; i < getRows(); i++) {
			for (int k = 0; k < getCols(); k++) {
				for (int j = 0; j < mat.getCols(); j++) {
					result.data[i][j] += this.data[i][k] * mat.data[k][j];
				}
			}
		}
		return result;
	}

	public Matrix transpose() {
		Matrix result = new Matrix(getCols(), getRows());
		for (int i = 0; i < result.getRows(); i++) {
			for (int j = 0; j < result.getCols(); j++) {
				result.data[i][j] = this.data[j][i];
			}
		}
		return result;
	}

	public Vec3D dot(Vec3D v) {
		return new Vec3D(data[0][0] * v.x + data[0][1] * v.y + data[0][2] * v.z,
				data[1][0] * v.x + data[1][1] * v.y + data[1][2] * v.z,
				data[2][0] * v.x + data[2][1] * v.y + data[2][2] * v.z);
	}

	public double max() {
		double max = data[0][0];
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				max = Math.max(max, data[i][j]);
			}
		}
		return max;
	}

	public Matrix invert(double epsilon) {
		if (getRows() != getCols())
			return null;
		int n = getRows();
		Matrix left = new Matrix(this), right = new Matrix(n, n);
		for (int i = 0; i < n; i++) {
			right.data[i][i] = 1;
		}

		for (int line = 0; line < n; line++) {
			for (int i = line; i < n; i++) {
				if (Math.abs(left.data[i][line]) > epsilon) {
					if (i != line) {
						right.lineSwitch(i, line);
						left.lineSwitch(i, line);
					}
					break;
				}
			}

			if (Math.abs(left.data[line][line]) < epsilon)
				return null;
			right.lineScale(line, 1.0 / left.data[line][line]);
			left.lineScale(line, 1.0 / left.data[line][line]);
			for (int i = line + 1; i < n; i++) {
				right.lineComb(i, line, -left.data[i][line]);
				left.lineComb(i, line, -left.data[i][line]);
			}

		}

		for (int i = n - 1; i >= 0; i--) {
			for (int j = i - 1; j >= 0; j--) {
				right.lineComb(j, i, -left.data[j][i]);
				left.lineComb(j, i, -left.data[j][i]);
			}
		}

		return right;
	}

	public void lineComb(int i, int j, double s) {
		for (int k = 0; k < getCols(); k++) {
			this.data[i][k] += this.data[j][k] * s;
		}
	}

	public void lineScale(int i, double s) {
		for (int k = 0; k < getCols(); k++) {
			this.data[i][k] *= s;
		}
	}

	public void lineSwitch(int i, int j) {
		double[] temp = data[i];
		data[i] = data[j];
		data[j] = temp;
	}

	public int getRows() {
		return data.length;
	}

	public int getCols() {
		return data[0].length;
	}

	public static final Matrix diag(double... data) {
		Matrix result = new Matrix(data.length, data.length);
		for (int i = 0; i < data.length; i++) {
			result.data[i][i] = data[i];
		}
		return result;
	}

	public static final Matrix doubleVecProd(Vec3D vec) {
		double x = vec.x, y = vec.y, z = vec.z;
		return new Matrix(new double[][] { //
				{ -y * y - z * z, x * y, x * z }, //
				{ y * x, -x * x - z * z, y * z }, //
				{ z * x, z * y, -x * x - y * y } //
		});
	}

	public static final Matrix vecProd(Vec3D vec) {
		double x = vec.x, y = vec.y, z = vec.z;
		return new Matrix(new double[][] { //
				{ 0, -z, y }, //
				{ z, 0, -x }, //
				{ -y, x, 0 } //
		});
	}

	public static final Matrix rotMat(Vec3D axis, double rot) {
		axis = axis.normalize();
		double x = axis.x, y = axis.y, z = axis.z;
		double c = Math.cos(rot), s = Math.sin(rot);
		return new Matrix(new double[][] { //
				{ x * x * (1 - c) + c, x * y * (1 - c) - z * s, x * z * (1 - c) + y * s }, //
				{ x * y * (1 - c) + s * z, y * y * (1 - c) + c, y * z * (1 - c) - s * x }, //
				{ x * z * (1 - c) - s * y, y * z * (1 - c) + s * x, z * z * (1 - c) + c }//
		});
	}

	public boolean equals(Matrix a, double epsilon) {
		if (a.getRows() != getRows() || a.getCols() != getCols())
			return false;
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				if (Math.abs(this.data[i][j] - a.data[i][j]) > epsilon)
					return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				builder.append(String.format("%.2f", this.data[i][j]) + (j == getCols() - 1 ? "" : " "));
			}
			builder.append(i == getRows() - 1 ? "" : "\n");
		}
		return builder.toString();
	}

}