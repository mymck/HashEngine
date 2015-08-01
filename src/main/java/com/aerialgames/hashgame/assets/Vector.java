package com.aerialgames.hashgame.assets;

import java.lang.reflect.Array;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public abstract class Vector<T> implements HashPack<Vector<T>> {
	public T[] v;
	public int dim = 0;
	protected double magnitude = 0.0f;
	public static final int INVALID_POSITIVE_VALUE = -1;

	public Vector(T[] elements) {
		this.dim = elements.length;
		if (this.dim > 0 && this.v == null) {
			this.v = elements;
			getMagnitudeSq();
		}
	}
	
	public abstract void vectorSum(Vector<T> adding);

	public abstract double vectorInner(Vector<T> v2);

	public abstract void normalizeVector();

	public int Dim() {
		int Result = this.dim;
		return Result;
	}

	public T x() {
		if (this.dim > 0) {
			return this.v[0];
		} else {
			return null;
		}
	}

	public T y() {
		if (this.dim > 1) {
			return this.v[1];
		} else {
			return null;
		}
	}

	public abstract Vector<T> vectorScaMult(float scalar);

	public abstract T getDim(int dim);

	public abstract Vector<T> getRangeDim(int start, int end);

	public abstract Vector<T> getSpecificDim(int... indices);

	public void setDim(int dim, T value) {
		if (dim > 0 && dim <= this.dim) {
			if (value.getClass() == this.v[dim - 1].getClass()) {
				this.v[dim - 1] = value;
			}
		}
	}

	// {
	// Object Result = null;
	// if (dim > 0 && dim <= this.dim) {
	// Result = this.tempVector[dim - 1];
	// }
	// return Result;
	// }
	public boolean setVectorDim(int dim, T value) {
		if (dim > 0 && dim <= this.dim) {
			this.v[dim - 1] = value;
		}

		if (this.v[dim - 1] == value) {
			this.getMagnitudeSq();
			return true;
		} else {
			return false;
		}
	}

	// public abstract boolean setVectorDim(int dim, int value);

	public abstract int getIndex();

	public Vector<T> getPayload() {
		Vector<T> Result = this;
		return Result;
	}

	public double getMagnitudeSq() {
		double Result = 0.0f;
		if (Result == 0.0f) {
			Result = Math.abs(vectorInner(this));
		}
		return Result;
	}
	@Override
	public int comparePayload(HashPack<Vector<T>> comp)
	{
		return this.compareVector(comp.getPayload());
	}

	public abstract int compareVector(Vector<T> v);

	@Override
	public String toString() {
		String Result = "";
		for (int i = 1; i <= this.dim; i++) {
			Result += this.v[i] + " ";
		}
		return Result;
	}

	@Override
	public boolean equals(Object v) {
		if (v == null) {
			return false;
		}
		VectorFloat testVector = (VectorFloat) v;
		for (int i = 0; i < this.dim; i++) {
			if (this.v[i] != testVector.v[i]) {
				return false;
			}
		}
		return true;
	}

	public Class<?> getType() {
		Class<?> Result = this.v[0].getClass();
		return Result;
	}

    public void addDim(T... entrys) {
        int vPos = this.v.length;
        this.v = Arrays.copyOf(this.v,vPos+entrys.length);

        for(int indexEntrys = 0; indexEntrys < this.v.length; indexEntrys++ )
        {
            this.v[vPos+indexEntrys] = entrys[indexEntrys];
        }
    }

    public void removeDim(int dim)
    {
        for(int indexV = dim-1;indexV < this.v.length; indexV++)
        {
            if(indexV+1 < this.v.length)
            {
                this.v[indexV] = this.v[indexV+1];
            }
            else
            {
                this.v[indexV] = null;
            }
        }
        this.v = Arrays.copyOf(this.v,this.v.length-1);
    }
}
