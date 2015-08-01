package com.aerialgames.hashgame.assets;

public class Timer {
	String Timer_Debug_String;
	long start;
	long interval;
	long last;
	
	public Timer(String debug)
	{
		this.Timer_Debug_String = debug;
		this.start = System.currentTimeMillis();
		this.interval = this.start;
	}
	
	@Override
	public String toString()
	{
		this.last = System.currentTimeMillis();
		this.interval = this.last - this.start;
		
		return "Debugging "+this.Timer_Debug_String+" "+(this.interval)+"ms";
	}
	
	public void report()
	{
		System.out.println(this);
	}
}
