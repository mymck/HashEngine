package com.aerialgames.hashgame;

import com.aerialgames.hashgame.assets.*;

public abstract class Entity implements HashPack<Entity> {
	VectorFloat realPos;
	static Float[] basePos = {0.0f,0.0f};
	
	public Entity()
	{		
		this.realPos = new VectorFloat(basePos);
	}
	
	public abstract void update(float dtForFrame);
	
	public int getIndex() {
		return (int)(Math.pow(realPos.v[0], 2) + Math.pow(realPos.v[1], 2));
	}
	
	public Entity getPayload() {
		return null;
	}

	@Override
	public int comparePayload(HashPack<Entity> payload) {
//		think about some EntityId generation to make comparisons simple
		return 0;//this.realPos.comparePayload(payload.realPos);
	}
	
	@Override
	public boolean equals(Object compare)
	{
		Entity entCompare = ((Entity)compare);
		if(this.realPos.dim != entCompare.realPos.dim)
		{
			return false;
		}
		for(int i=0;i<this.realPos.dim;i++)
		{
			if(this.realPos.v[i] != entCompare.realPos.v[i])
			{
				return false;
			}
		}
		return true;
	}

	public Class<?> getType() {
		return this.getClass();
	}
}
