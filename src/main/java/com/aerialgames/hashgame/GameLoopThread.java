package com.aerialgames.hashgame;

import com.aerialgames.hashgame.GameCore.QueueSound;
import com.aerialgames.hashgame.assets.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

@SuppressWarnings("unused")
//Track events. Catogorized log
@SuppressLint("WrongCall")
public class GameLoopThread extends Thread {
	static final long FPS = 30;
	private static String DEBUG_GAME_LOOP = "Game Loop Debug";
	private GameSurface surface;
	public long sleepTime = 0;
	private GameCore core;
	private MainActivity system;
	private boolean running = false;
	private SoundPool gameSounds;
	private AndroidGameSoundLoader agsl;
	VectorFloat size = new VectorFloat(30.0f, 30.0f);
	private int currentIterRange = 0;
	private int sleepOver = 0;

	// GameLoop should only run (static?) methods from the core.
	// Do we want a pointer in the constructer
	// Any actual Game init done in GameCore constructor or this
	public GameLoopThread(MainActivity mAct) {
		this.system = mAct;
		this.surface = this.system.surface;
		this.surface.gameLoop = this;
		this.core = this.system.core;
		initGameSounds();
		initGameBitmapTargets();
		//Should be called whenever the bitmap array should be changed
		this.surface.initGameBitmaps(0,GameConst.TOTAL_BITMAPS);
	}
	
	private void initGameBitmapTargets()
	{
		this.core.setBitmapTargetAtIndex(GameConst.BLACK_BALL, R.drawable.ballblack);
		this.core.setBitmapTargetAtIndex(GameConst.BODY_BULB, R.drawable.bulb);
        this.core.setBitmapTargetAtIndex(GameConst.POINT_RED, R.drawable.point_red);

	}
//TODO:this needs to be re-examined. All resources should be targeted and loaded the same way.
	private void initGameSounds() {
		this.gameSounds = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		this.agsl = new AndroidGameSoundLoader(this.system, this.core, this.gameSounds);
		this.gameSounds.setOnLoadCompleteListener(this.agsl);
		this.agsl.loadSoundSampleAtIndex(GameConst.SOUND_MENU_CLICK, R.raw.test);

	}

	public void setRunning(boolean run) {
		this.running = run;
	}

	@Override
	public void run() {
		long startTime = 0;
		long lastTime = 0;
		while (this.running) {
			lastTime = startTime;
			startTime = System.currentTimeMillis();
			this.core.processControllers(this.surface.camera);
			switch(this.core.getState())
			{
				case RUN:
				{
					gameUpdate((startTime - lastTime)/1000.f);
 				}
				case PAUSE:
				{
					this.surface.joinNewDrawablesToQueue(this.core.getDrawables(this.surface.camera));
					this.surface.joinNewDrawablesToQueue(this.core.drawUnitBounds(this.surface.camera));
					this.surface.drawSurfaceEntities();
					loadSoundQueue(this.system, this.core.playableSounds);	
					
				}break;
				default:
				{}break;
			}
			sleepToTargetFPS(startTime);
		}
	}

	private void loadSoundQueue(Context con, QueueSound soundPlayables) {
		while (!soundPlayables.isEmpty()) {
			this.agsl.playSoundSample(soundPlayables.pop());
		}
	}

	public int countUPs = 0;
	public int countDOWNs = 0;
	@Deprecated
	private void processInputQueue(QueueInput inputs) {
		if (inputs != null) {
			Drawable entity = null;
			int startLength = inputs.length;
			int count = 0;

			InputPosition workingInput = inputs.popLock();
			while (workingInput != null) {
				count++;
				VectorInt pos = new VectorInt(workingInput.getX(), workingInput.getY());
				switch (workingInput.getAction()) {
					case DOWN: {
						this.countDOWNs++;
						break;
					}
					case NEXT: {
						break;
					}
					case UP: {
						this.countUPs++;
						break;
					}
					default: {
						break;
					}
				}
				if (entity != null) {
					//entity.setPositionVectorForCamera(pos);
				}
				if (!inputs.isEmpty()) {
					workingInput = inputs.popLock();
				} else {
					workingInput = null;
				}
			}
		}
	}

	private void sleepToTargetFPS(long startTime) {
		long ticksPS = 1000 / FPS;
		sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
		try {
			if(sleepTime < 0)
			{
				this.sleepOver -= sleepTime;
				sleep(1);
			}
			else if (sleepTime > 0)
			{
				if(sleepOver > 0)
				{
					if(sleepTime > sleepOver)
					{
						sleepTime -= sleepOver;
						sleepOver = 1;
					}
					else
					{
						sleepOver -= sleepTime;
						sleepTime = 1;
					}
				}
				sleep(sleepTime);
			}
		} catch (Exception e) {
		}
	}

	// maybe have this call something more generic
	// Store entities and such alittle further down
	private void gameUpdate(float dTForFrame) {
		// This handles choosing Entities by Region
//		Examine regions by some priority device
//		priority heap. camera units are always highest priorities
//		regions with camera entities should be updated every frame
//		Consider every index an individual update
		this.surface.camera.update(dTForFrame);
		updateHashedEntities(this.core.world,dTForFrame);
		// collideEntities();
	}


	private void collideEntities() {
		// Collision Tests done here
		// Can this be done on separate thread?
	}

	private Drawable createDrawableEntityAtPosition(GameSurface gameS, VectorInt position, VectorFloat size, int drawLevel) {
//		Drawable eDrawable = new Drawable(size, position, drawLevel);
		// gameS.addEntityToSurface(eDrawable);
		return null;
	}

	private void addEntitiesToSurface() {
		Integer[] pos = { 200, 311 };
		createDrawableEntityAtPosition(this.surface, new VectorInt(pos), this.size, 2);

		Integer[] pos1 = { 200, 311 };
		createDrawableEntityAtPosition(this.surface, new VectorInt(pos1), this.size, 1);
	}

	public int updatedThisFrame = 0;
	private void updateHashedEntities(GameCore.HashWorld allEntities,float dTForFrame) {
		updatedThisFrame = 0;
		VectorInt indexCamera = this.surface.camera.getIndexedCameraBounds();
		allEntities.setIterBounds(indexCamera.v[0], (VectorInt)indexCamera.getRangeDim(1, 2));
		for(Object cameraBlock : allEntities)
		{
			EntityWorld entity = (EntityWorld)cameraBlock;
			int oldHashIndex = entity.getIndex();
			entity.update(dTForFrame);
            entity.isDrawn = true;
			if (oldHashIndex != entity.getIndex()) {
				allEntities.reassignHashIndex(entity, oldHashIndex);
			}
		}
		// This is where we can decide the range of entities/areas to update
		int iterIncrease = 200;
		int iterEnd = this.currentIterRange + iterIncrease;
		boolean atMax = (iterEnd >= allEntities.indexMax);
		if (atMax) {
//			allEntities.setHashIterationRange(this.currentIterRange, allEntities.indexMax-1);
			allEntities.setIterBounds(this.currentIterRange, new VectorInt(allEntities.indexMax-this.currentIterRange-1,0));
		} else {
//			allEntities.setHashIterationRange(this.currentIterRange, iterEnd);
			allEntities.setIterBounds(this.currentIterRange, new VectorInt(iterIncrease,0));
		}
		for (Object update : allEntities) {
			EntityWorld entity = ((EntityWorld) update);
            entity.isDrawn = false;
			if(GameCore.isWithin2DRect(entity.realPos,this.surface.camera.getCameraWorldRect()))
			{
				continue;
			}
			int oldHashIndex = entity.getIndex();
			entity.update(dTForFrame);
			if (oldHashIndex != entity.getIndex()) {
				allEntities.reassignHashIndex(entity, oldHashIndex);
			}
			this.updatedThisFrame++;
		}
		this.currentIterRange += iterIncrease;
		if (atMax) {
			this.currentIterRange = 0;
		}
	}

	private class AndroidGameSoundLoader implements OnLoadCompleteListener {
		boolean[] isPlayable = new boolean[GameConst.TOTAL_SOUND_SAMPLES];
		GameCore core;
		SoundPool gameSounds;
		MainActivity context;

		public AndroidGameSoundLoader(MainActivity con, GameCore gCore, SoundPool gSounds) {
			this.context = con;
			this.core = gCore;
			this.gameSounds = gSounds;
		}

		// decrypt gameId of sound to match platform naming conventions
		public void loadSoundSampleAtIndex(int index, int resourceId) {
			if (index < GameConst.TOTAL_SOUND_SAMPLES) {
				isPlayable[index] = false;
				this.core.setSoundTargetAtIndex(index, this.gameSounds.load(this.context, resourceId, 0));
			}
		}

		public void playSoundSample(int sampleId) {
			if (isPlayable[this.core.getIndexOfSample(sampleId)]) {
				this.gameSounds.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f);
			}
		}

		@Override
		public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
			isPlayable[this.core.getIndexOfSample(sampleId)] = true;
		}
	}
}
