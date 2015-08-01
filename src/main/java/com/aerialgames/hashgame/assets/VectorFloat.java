package com.aerialgames.hashgame.assets;

import java.lang.reflect.Array;
import java.util.Arrays;

@SuppressWarnings("unused")
public class VectorFloat extends Vector<Float> {

	public VectorFloat(Float... elements) {
		super(elements);
	}

	@Override
	public int getIndex() {
		if (this.dim > 0) {
			Float flt = (Float) this.v[1];
			flt = flt * 10000.0f;
			return flt.intValue();
		}
		return 0;
	}

	@Override
	public Float getDim(int dim) {
		Float Result = null;
		if (dim > 0 && dim <= this.dim) {
			Result = (Float) this.v[dim - 1];
		}
		return Result;
	}

	@Override
	public int compareVector(Vector<Float> v) {
		if (v.getClass() != this.getClass()) {
			float error = 1 / 0;
		}
		double mag = this.getMagnitudeSq();
		double magCompare = v.getMagnitudeSq();
		if (mag > magCompare) {
			return 1;
		} else if (mag < magCompare) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public double vectorInner(Vector<Float> v2) {
		double Result = 0.0f;
		if (this.dim == v2.dim) {
			for (int i = 0; i < this.dim; i++) {
				Result += this.v[i] * v2.v[i];
			}
		}
		return Result;
	}

	@Override
	public String toString() {
		String Result = "";
		for (int i = 0; i < this.dim; i++) {
			Result = Result + " " + this.v[i];
		}
		return Result;
	}

	public VectorFloat vScalarMultiplyResult(float scalar) {
		Float[] Result = new Float[this.dim];
		for (int i = 0; i < this.dim; i++) {
			Result[i] = this.v[i] * scalar;
		}
		return new VectorFloat(Result);
	}

	@Override
	public VectorFloat vectorScaMult(float scalar) {
		Float[] Result = this.v;
		for (int i = 0; i < this.dim; i++) {
			Result[i] = Result[i] * scalar;
		}
		return new VectorFloat(Result);
	}

	@Override
	public void normalizeVector() {
		this.magnitude = Math.sqrt(this.getMagnitudeSq());
		for (int i = 0; i < this.dim; i++) {
			this.v[i] =  (this.v[i] / (float)this.magnitude);
		}
	}

	public void addScalar(float add) {
		for (int i = 0; i < this.dim; i++) {
			this.v[i] += add;
		}
	}

	public VectorFloat copy() {
		Float[] working = new Float[this.dim];
		for (int index = 0; index < this.dim; index++) {
			working[index] = this.v[index];
		}
		VectorFloat Result = new VectorFloat(working);
		return Result;
	}

	@Override
	public Vector<Float> getRangeDim(int start, int end) {
		if (end > this.dim || start <= 0 || start > end) {
			// maybe start throwing exceptions soon....especially in game code
			return null;
		} else {
			Float[] working = new Float[end - start];
			for (int index = 0; index < working.length; index++) {
				working[index] = this.v[start + index];
			}
			VectorFloat Result = new VectorFloat(working);
			return Result;
		}
	}

	public VectorFloat getSpecificDim(int... indices) {
		Float[] working = new Float[indices.length];
		for (int workingIndex = 0; workingIndex < working.length; workingIndex++) {
			working[workingIndex] = this.v[indices[workingIndex]];
		}
		VectorFloat Result = new VectorFloat(working);
		return Result;
	}

	public VectorFloat vNegativeResult() {
		Float[] Result = new Float[this.dim];
		for (int i = 0; i < this.dim; i++) {
			Result[i] = -this.v[i];
		}
		return new VectorFloat(Result);
	}

	public void vNegative() {
		for (int i = 0; i < this.dim; i++) {
			this.v[i] = -this.v[i];
		}
	}

	public VectorFloat vSumScalarResult(Float adding) {
		Float[] Result = new Float[this.dim];
		for (int i = 0; i < this.dim; i++) {
			Result[i] = this.v[i] + adding;
		}
		return new VectorFloat(Result);
	}

	public VectorFloat vSumResult(VectorFloat adding) {
		if (this.dim == adding.dim) {
			Float[] Result = new Float[this.dim];
			for (int i = 0; i < this.dim; i++) {
				Result[i] = this.v[i] + adding.v[i];
			}
			return new VectorFloat(Result);
		}
		return null;
	}

	@Override
	public void vectorSum(Vector<Float> adding) {
		if (this.dim == adding.dim) {
			for (int i = 0; i < this.dim; i++) {
				this.v[i] += adding.v[i];
			}
		}
	}

}
