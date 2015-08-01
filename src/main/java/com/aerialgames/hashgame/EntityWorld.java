package com.aerialgames.hashgame;

import java.util.Random;

import com.aerialgames.hashgame.assets.*;

public class EntityWorld extends Entity {
	Random r = new Random();
    float size = 0.025f;
    VectorFloat WorldDim;
	private VectorFloat direction;// direction of momentum
	protected static float MAX_SPEED = 0.015f;
	VectorFloat vel;
	private float acc = 0.0f;
	VectorInt resAddress;
	InputPosition lastInput = null;
	int unitPower = 0;
	public double tDistance = 0.0f;
	public double tStop = 0.0f;
	EntityWorld travel = null;

	public EntityWorld(int worldSize, int unitPower, VectorFloat startV, Integer... res) {
		this.unitPower = unitPower;
		this.realPos = startV;
		this.resAddress = new VectorInt(res);
		this.direction = new VectorFloat(0.0f, 0.0f);
		this.vel = new VectorFloat(0.0f, 0.0f);

	}

	public EntityWorld(VectorFloat startV) {
		this.realPos = startV;
	}

    public void collide(EntityWorld entity)
    {

    }

	@Override
	public void update(float dT) {
        float time = dT;
        if(time > 0.06f)
        {
            time = 0.06f;
        }
		// If we have somewhere to travel to. Go there at max acceleration
		// this happens if we want to adjust direction/confirm direction
        //TODO(myles): tune these to feel right; JUST DO IT
		float accel = 70.0f; //NOTE(myles): range 30.0f - 240.0f
        float mass = 200.0f;
		float friction = -0.085f;
		VectorFloat forces = this.vel.vScalarMultiplyResult(friction*mass);
		//this is close. Once collision detection is built, use that instead of exact coordinates
		if (this.travel != null) {
            this.drawState = DrawState.traveling;
			VectorFloat vTravel = this.travel.realPos.vSumResult(this.realPos.vNegativeResult());
			this.direction = vTravel.copy();
			this.direction.normalizeVector();

			if(forces.x() != 0.0f || forces.y() != 0.0f) {
                double travelDistance = Math.sqrt(vTravel.getMagnitudeSq());
				this.tDistance =  travelDistance/ Math.sqrt(this.vel.getMagnitudeSq());
                double maxDistance = this.vel.getMagnitudeSq()/Math.sqrt(forces.vectorScaMult(2.0f).getMagnitudeSq());
//                double speedMag = Math.sqrt(this.vel.getMagnitudeSq());
                if(travelDistance <= maxDistance)
                {
					this.acc = 0.0f; //Can glide in :)
                    this.travel = null;
                    this.drawState = DrawState.stopped;
				}
				else {
					this.acc = accel; // since we're confident in direction
				}
			}else {
				this.acc = accel; // since we're confident in direction
			}

		}
		else
        {
            this.drawState = DrawState.stopped;
        }
			// this.realPos = working.vectorSum(this.realPos);
			VectorFloat traveling = this.direction.vScalarMultiplyResult(this.acc*mass/GameCore.tens[unitPower*3]);


//			double minDistance = 0.00001755f;
			traveling.vectorSum(forces);

			VectorFloat workingPos = traveling.vScalarMultiplyResult(0.5f * time * time);
            Vector velAdjust = this.vel.vScalarMultiplyResult(time);
			workingPos.vectorSum(velAdjust);
			// this.direction.vScalarMultiplyResult(0.00f);//this.vel.vectorScaMult(dT));
			workingPos.vectorSum(this.realPos);
			if (workingPos.x() >= 0.0f && workingPos.x() < 1.0f && workingPos.y() >= 0.0f && workingPos.y() < 1.0f) {
				this.realPos = null; // trigger clean up of old position
				this.realPos = workingPos;
			}
			this.vel.vectorSum(traveling.vectorScaMult(time));
//			double brake = 1 / GameCore.tens[7];

			/*if (this.vel.x() < brake && this.vel.y() < brake) {
				this.vel.v[0] = 0.0f;
				this.vel.v[1] = 0.0f;
				this.direction = null;
			}*/

	}
    public enum DrawState{
        traveling, stopped;
    }

    public void toSound()
    {
        //TODO(myles): toPlaySound() maybe rename
    }
    DrawState drawState = DrawState.stopped;
    boolean isDrawn = false;
    public Integer[] toDraw()
    {
        //TODO(myles): return as queue, or iterator decisions;
        if(isDrawn) {
            switch (this.drawState) {
                case stopped:
                {
                    if (this.resAddress.dim >= 1) {
                        Integer[] Result = {this.resAddress.v[0]};
                        return Result;
                    } else {
                        GameCore.fail();
                    }
                }
                case traveling: {
                    if (this.resAddress.dim >= 2) {
                        Integer[] Result = {this.resAddress.v[0], this.resAddress.v[1]};
                        return Result;
                    } else {
                        GameCore.fail();
                    }
                }
                default: {
                    GameCore.fail();
                    return null;
                }
            }
        }
        else
        {
            if (this.resAddress.dim >= 1) {
                Integer[] Result = {this.resAddress.v[0]};
                return Result;
            } else {
                GameCore.fail();
                return null;
            }
        }
    }
	@Override
	public int getIndex() {
		int Result = 0;
		switch (this.realPos.dim) {
			case 2: {
				Result = GameCore.getIndexFromVectorFloat(this.realPos, this.unitPower);

				// Result = (int) Math.floor(this.realPos.v[0] * 100) + (int)
				// Math.floor(this.realPos.v[1] * 100) * 100;
				break;
			}
			default: {
				break;
			}
		}
		return Result;
	}

    public VectorFloat getWorldRect()
    {
        VectorFloat scale = new VectorFloat(this.size/GameCore.tens[this.unitPower],this.size/GameCore.tens[this.unitPower]);
        VectorFloat RightBottom = this.realPos.vSumResult(scale);
        VectorFloat LeftTop = this.realPos.vSumResult(scale.vNegativeResult());
        VectorFloat Result = new VectorFloat(LeftTop.v[0],LeftTop.v[1],RightBottom.v[0],RightBottom.v[1]);
        return Result;
    }

	public float getWorldX() {
		float Result = this.realPos.v[0];
		return Result;
	}

	public float getWorldY() {
		float Result = this.realPos.v[1];
		return Result;
	}
}
