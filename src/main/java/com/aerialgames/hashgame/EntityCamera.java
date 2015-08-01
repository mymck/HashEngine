package com.aerialgames.hashgame;

import com.aerialgames.hashgame.assets.*;

public class EntityCamera extends EntityWorld {

	private VectorInt dim = null;
	int widthPx = 0;
	int heightPx = 0;
	float xPixelsPerUnit = 10000.0f;
	float yPixelsPerUnit = 10000.0f;
	float unit = 0.0f;
	float screenR = 0.0f;
    float zoomLevel = 2.0f;

	public EntityCamera(Integer worldSize,int unitPower, VectorFloat startV, VectorInt cameraDim) {
		super(worldSize, unitPower, startV,GameConst.BLACK_BALL,GameConst.POINT_RED);
        //TODO(myles):draw zoom level as change in the input vector's magnitude * c (constant to set range)
		if (cameraDim.Dim() == 2&& cameraDim.x() > 0 && cameraDim.y() > 0) {
            this.widthPx = cameraDim.x();
            this.heightPx = cameraDim.y();
		}
		this.xPixelsPerUnit = this.widthPx*this.zoomLevel;
		this.yPixelsPerUnit = this.heightPx*this.zoomLevel;
		this.unit = (float) GameCore.tens[this.unitPower];

		this.screenR = 0.5f*getFloatFromUnit((float)Math.sqrt(this.xScreenUnits()*this.xScreenUnits() + this.yScreenUnits()*this.yScreenUnits()));
	}

    public void adjustZoomLevel(float zoom)
    {
        this.xPixelsPerUnit = this.widthPx*this.zoomLevel;
        this.yPixelsPerUnit = this.heightPx*this.zoomLevel;
        this.screenR = 0.6f*getFloatFromUnit((float)Math.sqrt(this.xScreenUnits()*this.xScreenUnits() + this.yScreenUnits()*this.yScreenUnits()));
    }
	private boolean badCamera()
	{
		return (widthPx == 0 || heightPx == 0);
	}

    public VectorFloat worldRectToScreenRect(VectorFloat worldRect)
    {
        if(worldRect.Dim() == 4)
        {
            VectorFloat LeftTop = this.getScreenPosFromWorld(new VectorFloat(worldRect.v[0],worldRect.v[1]));
            VectorFloat RightBottom = this.getScreenPosFromWorld(new VectorFloat(worldRect.v[2], worldRect.v[3]));
            VectorFloat Result = new VectorFloat(LeftTop.v[0],LeftTop.v[1],RightBottom.v[0],RightBottom.v[1]);
            return Result;
        }
        else
        {
            return null;
        }
    }

	public VectorFloat getScreenPosFromWorld(VectorFloat check) {
		if (badCamera()) {
			return null;
		}
        //NOTE(myles): why is the getUnitFromFloat necessary here?
		VectorFloat working = new VectorFloat(check.v[0]- this.getWorldX(), check.v[1]- this.getWorldY());
        working.vectorScaMult(GameCore.tens[this.unitPower]);
		float xUnits = working.x() * this.xPixelsPerUnit;
		float yUnits = working.y() * this.yPixelsPerUnit;

		// float xPercent = (working.v[0] * this.xPixelsPerUnit) / xUnits;
		// float yPercent = (working.v[1] * this.xPixelsPerUnit) / yUnits;
		// working.normalizeVector();
		VectorFloat Result = new VectorFloat((xUnits + this.widthPx / 2), (yUnits + this.heightPx / 2));
		return Result;
	}

	public VectorFloat getWorldPosFromScreen(VectorInt screenPos) {
		VectorFloat working = new VectorFloat(getFloatFromUnit(screenPos.x() - this.widthPx / 2.0f), getFloatFromUnit(screenPos.y() - this.heightPx / 2.0f));
		// units <- screenUnits, screenPos
		float x = working.x() / this.xPixelsPerUnit;
		float y = working.y() / this.yPixelsPerUnit;
		VectorFloat Result = new VectorFloat(x + this.getWorldX(), y + this.getWorldY());
		return Result;
	}


	private float xScreenUnits() {
		return this.widthPx / this.xPixelsPerUnit;
	}

	private float yScreenUnits() {
		return this.heightPx / this.yPixelsPerUnit;
	}

	private float getUnitFromFloat(float inReals) {
		return inReals * this.unit;
	}

	private float getFloatFromUnit(float inUnits) {
		return inUnits / this.unit;
	}

	/**
	 * Returns the dimensions of the camera in the world.
	 * 
	 * @return VectorFloat by dim xMin,yMin,xMax,yMax
	 */
	public VectorFloat getCameraWorldRect() {
		VectorFloat Result = new VectorFloat(this.getWorldX() - this.screenR, this.getWorldY() - this.screenR, this.getWorldX() + this.screenR, this.getWorldY() + this.screenR);
		return Result;
	}

	/**
	 * 
	 * @return a VectorInt as startIndex, xStride, yStride
	 */
	public VectorInt getIndexedCameraBounds() {
		VectorFloat dim = getCameraWorldRect();

		for(int dimIndex = 0;
		dimIndex < dim.Dim();
		dimIndex++) {
			if ((dimIndex == 0 || dimIndex ==1) && dim.v[dimIndex] < 0) {
				dim.v[dimIndex] = 0.0f;
			}
			if ((dimIndex == 2 || dimIndex == 3) && dim.v[dimIndex] > 1.0f) {
				dim.v[dimIndex] = 0.99f;
			}
		}
		int start = GameCore.getIndexFromVectorFloat(dim.getSpecificDim(0, 1), this.unitPower);
		int xStride = GameCore.getIndexFromVectorFloat(dim.getSpecificDim(2, 1), this.unitPower) - start;
		int yStride = GameCore.getIndexFromVectorFloat(dim.getSpecificDim(0, 3), this.unitPower) - start;
		VectorInt Result = new VectorInt(start, xStride, yStride);
		return Result;
	}
}
