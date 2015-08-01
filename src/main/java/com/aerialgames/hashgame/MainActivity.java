package com.aerialgames.hashgame;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.*;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private String DEBUG_AMAIN = "Main Activity Debug"; 
	GameLoopThread thread;
	GameCore core;
	GameSurfaceHolder holder;
	GameSurface surface;
	SoundPool gameSounds; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        requestWindowFeature(Window.)
        //Process Controller input in the surface?
        Log.i(DEBUG_AMAIN, "Building Core");
		this.core = new GameCore();
        Log.i(DEBUG_AMAIN, "Creating Surface");
        this.surface = new GameSurface(this);
        Log.i(DEBUG_AMAIN, "Starting Thread");
        this.thread = new GameLoopThread(this);
		this.surface.getHolder().addCallback(new GameSurfaceHolder(this));
        setContentView(surface);
	}

	public GameSurface getGameSurface()
	{
		GameSurface Result = this.surface;
		return Result;
	}
	public GameCore getGameCore()
	{
		GameCore Result = this.core;
		return Result;
	}
	public GameLoopThread getGameLoop()
	{
		GameLoopThread thread = this.thread;
		return thread;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private class GameSurfaceHolder implements SurfaceHolder.Callback
	{
		GameCore core;
		GameSurface surface;
		GameLoopThread thread;
		
		public GameSurfaceHolder(MainActivity mAct)
		{
//			this.core = mAct.getGameCore();
			this.surface = mAct.getGameSurface();
			this.thread = mAct.getGameLoop();
		}
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			boolean retry = true;
			thread.setRunning(false);
//			thread.interrupt();
         	
            while (retry) {
                   try {
                	   Thread.sleep(50);
                	   thread.join();
                       retry = false;
                   } catch (InterruptedException e) {
                   }
            }            
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
            
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			try{
    			this.surface.configureGameSurface();
    			while(this.thread == null)
    			{
    				Thread.sleep(10);
    			}
    			this.thread.setRunning(true);
    			this.thread.start();
//            	Thread.sleep(10000);
//            	gameLoop.setRunning(false);
            }
            catch(Exception e)
            {
            	float err = 1/0;
            }
		}
	}
}

//Some points might warrant a buffer android layer that 
//takes elements from the game and ships them to platform
//In Platform Layer
/**
 * Render
 * Bitmap handed to Game - Canvas Handed to game
 * Bitmap versatility - Canvas efficiency 
 * Sound
 * File I/O
 * Input
 * Timing
 * Connectivity
 * Memory Allocation
 * Dynamic Code Loading
 * Notifications
 * Fullscreen
 * Specifications
 * 
 */
