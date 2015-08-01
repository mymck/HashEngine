package com.aerialgames.hashgame.assets;

import java.lang.reflect.Array;

public class VectorInt extends Vector<Integer> {

	public VectorInt(Integer... elements) {
		super(elements);
	}

	@Override
	public double vectorInner(Vector<Integer> v2) {
		double Result = -1.0;
		if (v2.getClass() == this.getClass() && (this.dim == v2.dim)) {
		for (int i = 0; i < this.dim; i++) {
			Result += (Integer) this.v[i] * v2.v[i];
		}
		}
		return Result;
	}

	@Override
	public Integer getDim(int dim) {
		Integer Result = null;
		if (dim > 0 && dim <= this.dim) {
			Result = (Integer) this.v[dim - 1];
		}
		return Result;
	}

	@Override
	public int getIndex() {
		if(this.dim > 0)
			return this.v[0];
		else
			return 0;
	}

	@Override
	public int compareVector(Vector<Integer> v) {
		double inner1 = this.vectorInner(this);
		double inner2 = v.vectorInner(v);
		if (inner1 < inner2) {
			return -1;
		} else if (inner1 > inner2) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public VectorInt vectorScaMult(float scalar) {
		Integer[] Result = this.v;
		for (int i = 0; i < this.dim; i++) {
			Result[i] = (int) (Result[i] * scalar);
		}
		return new VectorInt(Result);
	}

	@Override
	public void normalizeVector() {

	}

	@Override
	public Vector<Integer> getRangeDim(int start, int end) {
		if (end > this.dim || start <= 0 || start > end) {
			//maybe start throwing exceptions soon....especially in game code
			return null;
		}else{
			Integer[] working = new Integer[end-start+1];
			for(int index=0;index < working.length;index++)
			{
				working[index] = this.v[start+index]; 
			}
			VectorInt Result = new VectorInt(working);
			return Result;
		}
	}
	
	public int sum()
	{
		int Result = 0;
		for(Integer i : this.v)
		{
			Result += i;
		}
		return Result;
	}
	public VectorInt getSpecificDim(int... indices)
	{
		Integer[] working = new Integer[indices.length];
		for(int workingIndex = 0;workingIndex < working.length; workingIndex++)
		{
			working[workingIndex] = this.v[indices[workingIndex]];
		}
		VectorInt Result = new VectorInt(working);
		return Result;
	}

	@Override
	public void vectorSum(Vector<Integer> adding) {
		if(this.dim == adding.dim)
		{
			for(int i=0;i<this.dim;i++)
			{
				this.v[i] += adding.v[i];
			}
		}
	}

}
