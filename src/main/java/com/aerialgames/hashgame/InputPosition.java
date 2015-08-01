package com.aerialgames.hashgame;


import com.aerialgames.hashgame.assets.VectorFloat;
import com.aerialgames.hashgame.assets.VectorInt;

public class InputPosition{
	VectorInt coords;
	public int controllerIndex;
	private long actionTime;
	public InputAction action = InputAction.CANCEL;

	public enum InputAction {
		DOWN, NEXT, UP, CANCEL;
	}
	// coordinates for single Pointer Input
	// time of each input
	public InputPosition(int x, int y, int index, long actionTime, InputAction act) {
		this.coords = new VectorInt(x, y);
		this.controllerIndex = index;
		this.actionTime = actionTime;
		this.action = act;
	}

	public InputAction getAction() {
		return this.action;
	}
	
	public int getControllerIndex()
	{
		return this.controllerIndex;
	}

	public int getX() {
		int Result = this.coords.v[0];
		return Result;
	}

	public int getY() {
		int Result = this.coords.v[1];
		return Result;
	}

	public void setAction(InputAction act) {
		this.action = act;
	}

	public VectorFloat getVectorFloat() {
		VectorFloat Result = new VectorFloat(this.coords.v[0].floatValue(),this.coords.v[1].floatValue()); 
		return Result;
	}
}