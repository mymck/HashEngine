package com.aerialgames.hashgame;


import com.aerialgames.hashgame.assets.*;

public class Drawable {
	public enum DrawType{
		none,line,box,resource;
	}
    private VectorFloat pixelDim;
	private VectorFloat cameraPos;
	private int drawLevel = 0;
	//Add offset into the drawing here
	int resAddress = -1;
	DrawType type = DrawType.none;

    public Drawable(VectorFloat cameraPos, VectorFloat pixelDim,int resource)
    {
        this.cameraPos = cameraPos;
        this.resAddress = resource;
        this.type = DrawType.resource;
        this.pixelDim = pixelDim;
    }

	public Drawable(VectorFloat cameraPos, int resource)
	{
		this.cameraPos = cameraPos;
		this.resAddress = resource;
		this.type = DrawType.resource;
	}
	
	public Drawable(VectorFloat cameraPos)
	{
		this.cameraPos = cameraPos;
		this.type = DrawType.box;
	}
	
	public Drawable(VectorFloat cameraPos1,VectorFloat cameraPos2)
	{
		Float[] working = new Float[4];
		working[0] = cameraPos1.v[0];
		working[1] = cameraPos1.v[1];
		working[2] = cameraPos2.v[0];
		working[3] = cameraPos2.v[1];		
		this.cameraPos = new VectorFloat(working);
		this.type = DrawType.line;
	}
	
	
//	@Override
	public int getIndex()
	{
		//if cameraX > 100 and cameraY < 100 then the entity is pushed into the next bracket
		int Result = ((int)((Math.abs(this.cameraPos.v[0]))/100.0f)%10)+((int)(Math.abs(this.cameraPos.v[1])/100.0f)*10)%100;
		return Result;
	}
	
	public VectorFloat getDrawPositions()
	{
		VectorFloat Result = this.cameraPos;
		return Result;
	}

	public int getDrawLevel() {
		return this.drawLevel;
	}
	

	public float getX() {
		return cameraPos.v[0];
	}
	

	public float getY() {
		return cameraPos.v[1];
	}

    public VectorFloat getDrawRect()
    {
        VectorFloat Result = this.pixelDim;
        return Result;
    }

	public void setPositionVectorForCamera(VectorFloat vectorFloat) {
		this.cameraPos = vectorFloat;
	}

}
