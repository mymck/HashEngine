package com.aerialgames.hashgame;

import java.util.Random;

import com.aerialgames.hashgame.assets.*;
//import com.aerialgames.hashgame.assets.Hashmap.HashIterator;


@SuppressWarnings("unused")
public class GameCore {
	public static int[] tens = { 1, 10, 100, 1000, 10000, 10000, 100000, 10000000, 100000000, 1000000000 };

	public static int fail() {
		return 1 / 0;
	}

	public enum GameState {
		PAUSE, RUN;
	}

	private GameState state = GameState.PAUSE;

	public GameState getState() {
		return this.state;
	}

	public void run() {
		this.state = GameState.RUN;
	}

	public void pause() {
		this.state = GameState.PAUSE;
	}

	public GameCore() {
		initControllers();
	}

	// Controls - using queues to store inputs for each controller index.
	final static int CONTROLLER_LIMIT = 10;
    private int connectedControllers = 0;
	Entity[] controlledEntities = new EntityWorld[CONTROLLER_LIMIT];
	QueueInput[] InputControllers = new QueueInput[CONTROLLER_LIMIT];
    QueueInput[] OutputControllers = new QueueInput[CONTROLLER_LIMIT];

	private void initControllers() {
		for (int i = 0; i < CONTROLLER_LIMIT; i++) {
			InputControllers[i] = new QueueInput();
            OutputControllers[i] = new QueueInput();
		}
	}

	public boolean processControllers(EntityCamera camera) {
		// Go through each controller input to see if it needs to be assigned to
		// an entity
		EntityWorld entityW = null;// new
									// EntityWorld(controllerHead.getVectorFloat());
		QueueInput controller = null;
		for (int controllerLoopIndex = 0; controllerLoopIndex < CONTROLLER_LIMIT; controllerLoopIndex++) {
			controller = InputControllers[controllerLoopIndex].chopInputs();
			while (!controller.isEmpty()) {
				InputPosition controllerInput = controller.popLock();
				switch (controllerInput.action) {
					case DOWN: {

//						camera.realPos = working;
                        if(connectedControllers < CONTROLLER_LIMIT)
                        {
                            connectedControllers++;
                            this.OutputControllers[controllerLoopIndex].pushLock(controllerInput);
                            if(connectedControllers == 2) {
                                camera.travel = null;
                            }
                            else {
                                if (camera.travel != null) {
                                    camera.travel.realPos = camera.getWorldPosFromScreen(controllerInput.coords);
                                } else {
                                    camera.travel = new EntityWorld(camera.getWorldPosFromScreen(controllerInput.coords));
                                }
                            }
                        }

						// this.world.
						break;
					}
					case NEXT: {

                        if(!this.OutputControllers[0].isEmpty() && !this.OutputControllers[1].isEmpty()) {
                            InputPosition start = this.OutputControllers[0].peek();
                            InputPosition end = this.OutputControllers[1].peek();
                            double differenceMag = Math.sqrt(start.coords.vectorInner(end.coords));
                            VectorFloat dimPixelPerUnit = new VectorFloat(camera.xPixelsPerUnit,camera.yPixelsPerUnit);
                            camera.adjustZoomLevel((float)(differenceMag/Math.sqrt(dimPixelPerUnit.getMagnitudeSq())));
                        }
                        else{
                            if(camera.travel != null)//move the camera to the next desired position
                            {camera.travel.realPos = camera.getWorldPosFromScreen(controllerInput.coords);}
                        }
//						VectorFloat working = camera.getWorldPosFromScreen(controllerInput.coords);
//						camera.realPos.v[0] = working.x();
//						camera.realPos.v[1] = working.y();
						break;
					}
					case UP: {
						switch (this.state) {
							case PAUSE: {
								this.state = GameState.RUN;
							}
								break;
							case RUN: {
								// this.state = GameState.PAUSE;
							}
								break;
							default: {
							}
								break;
						}
						playSoundAtIndex(GameConst.SOUND_MENU_CLICK);
                        this.OutputControllers[controllerLoopIndex].chopInputs();
                        connectedControllers--;
						break;
					}
					case CANCEL:
					default: {
						controller.chopInputs();
						break;
					}
				}
//				controllerInput = controller.popLock();
			}
		}
		
		return true;
	}

	private Entity assignInputToEntity(QueueInput input, Entity entity) {
		return null;
	}

	public boolean pushControllerInput(InputPosition controller) {
		if (controller.getControllerIndex() >= CONTROLLER_LIMIT) {
			return false;
		}
		InputControllers[controller.controllerIndex].pushLock(controller);
		return true;
	}

	// This handles Entities, Collisions, AI

	// Map/Map building
	int totalDimensions = 2;
	int worldSize = 4;
	int unitPower = 2;
	HashWorld world = new HashWorld(worldSize, totalDimensions, unitPower);

	public HashWorld getEntities() {
		HashWorld Result = this.world;
		return Result;
	}

	public EntityCamera getCameraEntity() {
		EntityCamera Result = new EntityCamera(this.worldSize, this.unitPower, this.world.cameraEntity.realPos, this.gameSurfaceDim);// this.world.cameraEntity;
		return Result;
	}

	/**
	 * This is an [min, max) check. Min included, Max excluded
	 * 
	 * @param value
	 *            the value to examine
	 * @param bounds
	 *            takes two points and if the point is between them
	 * @return true if value is less than the max of the bounds and the greater
	 *         than or equal to the min of the bounds
	 */
	public static boolean isBetweenBoundPoints(float value, VectorFloat bounds) {
		if (bounds.dim == 2) {
			if (bounds.x() > bounds.y()) {
				float temp = bounds.x();
				bounds.v[0] = bounds.y();
				bounds.v[1] = temp;
			}
			if (value >= bounds.x() && value < bounds.y()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	// Bounds as MinX,MinY,MaxX,MaxY
	public static boolean isWithin2DRect(VectorFloat pos, VectorFloat bounds) {
		if (pos.dim == 2 && bounds.dim == 4) {
			if (pos.x() >= bounds.v[0] && pos.x() < bounds.v[2] && pos.y() >= bounds.v[1] && pos.y() < bounds.v[3]) {
				return true;
			}
		}
		return false;
	}

	public static int getIndexFromVectorFloat(VectorFloat vector, int unitSize) {
		if (vector.dim > unitSize) {
			return -1;
		}
		double units = tens[unitSize];

		int Result = 0;// (int) Math.floor(vector.v[0] * units) + (int)
						// (Math.floor(vector.v[1] * units) * units);
		for (int i = 0; i < vector.dim; i++) {
			Result += (int) Math.floor(vector.v[i] * units) * Math.pow(units, i);
		}
		if (unitSize * vector.dim >= tens.length) {
			GameCore.fail();
		}
		return (int) (Result % tens[unitSize * vector.dim]);
	}

	class HashWorld extends Hashmap {
		VectorFloat startingPos1 = new VectorFloat(0.5f, 0.5f);
		EntityWorld cameraEntity = null;
		int boundStart = 0;
		VectorInt bounds;
		VectorInt increment;
		int indexPower = 0;
		int unitPower = 0;
		int unit = 0;

		public HashWorld(int indexPower, int dimensions, int unitPower) {
			super((int) GameCore.tens[indexPower]);
			this.indexPower = indexPower;
			this.unitPower = unitPower;
			this.cameraEntity = new EntityWorld(this.indexPower, this.unitPower, startingPos1);
			this.unit = (int) GameCore.tens[this.unitPower];
			Integer[] increments = new Integer[dimensions];
			int step = 1;
			for (int count = 0; count < dimensions; count++) {
				increments[count] = step;
				step = step * this.unit;
			}
			this.increment = new VectorInt(increments);
			generateRandomEntities();
		}

		public void addHashList(EntityWorld... packs) {
			for (EntityWorld hp : packs) {
				this.addHashPack(hp);
			}
		}

		private void generateRandomEntities() {
			VectorFloat startingPos = null;
			EntityWorld testEntity = null;
			Random rend = new Random();
			int ENTITY_SPAWN_COUNT = 900;
			for (int i = 0; i < ENTITY_SPAWN_COUNT; i++) {
                float pos = rend.nextFloat();
                if(pos < 0.5f)
                {
                    pos += 0.25f;
                }
                else {
                    pos -= 0.25f;
                }
				startingPos = new VectorFloat(pos,pos);
				testEntity = new EntityWorld(this.indexPower, this.unitPower, startingPos, ((int) (startingPos.x() * 100) % 2),GameConst.POINT_RED);
				
				this.addHashPack(testEntity);
				testEntity.travel = this.cameraEntity; //getTop
			}
			VectorFloat starting = new VectorFloat(0.5f, 0.5f);
			testEntity = new EntityWorld(this.indexPower, this.unitPower, starting, GameConst.BLACK_BALL, GameConst.BODY_BULB);
			testEntity.travel = this.cameraEntity;

			this.addHashPack(testEntity);
		}

		public void setIterBounds(int startIndex, VectorInt indexBounds) {
			this.boundStart = startIndex;
			this.bounds = indexBounds;
		}

		@Override
		public WorldIterator iterator() {
			WorldIterator Result = new WorldIterator(this, this.boundStart, this.unitPower, this.bounds);
			this.boundStart = 0;
			return Result;

		}

		class WorldIterator extends HashIterator {
			int startIndex = 0;
			int endIndex = 0;
			int currentStride = 0;
			int unitIncrement = 0;
			VectorInt bound;
			Hashmap hmPtr;

			public WorldIterator(Hashmap hm, int start, int unit, VectorInt bounds) {
				super(hm, start, start + bounds.x());
				this.hmPtr = hm;
				this.startIndex = start;
				this.currentStride = this.startIndex;
				this.bound = bounds;
				this.endIndex = this.startIndex + this.bound.sum();
				this.unitIncrement = unit;
			}

			@Override
			public boolean hasNext() {
				if (super.hasNext()) {
					return true;
				} else { // 1.check where the indexStride is
							// 2.Increment by the lowest stride
							// 3.If currentStride % dimCheck <= (StartIndex +
							// this.bound.sum) % dimCheck -> continue; with new
							// bounds
							// 4.else we are at the max stride so we can
							// subtract the difference of the start and the
							// bounds then increment the next dim
							// 5.run check if currentStride dimCheck at next dim
							// is within or equal to bounds. 5a.any lower
							// dimensions should be set to start of their bounds
							// 6a.stride in Iterator needs return true or be
							// outside of bounds.sum() to exit this loop
							// 6b.while the Iterator is inside bounds and not
							// finding anything it constantly fills new
							// iterators until it finds something or leaves the
							// bounds.sum
							// 6c.on new fill of Iterator check
							// if(super.hasNext()){true;} else get back to step
							// 2
					{
						// adjust stride and super.fill
						int examineUnitMultiplier = 2; // start at y unit
														// because our parent
														// iterator takes care
														// of x unit on its own
						int UnitBound = this.currentStride + this.bound.x();
						while (UnitBound <= this.endIndex) {
							int examineStrideDim = GameCore.tens[examineUnitMultiplier * this.unitIncrement];
							if (UnitBound % examineStrideDim < this.endIndex % examineStrideDim) {
								// we want the stride to be adjusted such that
								// the examinedStrideDim is incremented
								// and any lower UnitIndices are reset to the
								// Bounds starting point
								this.currentStride += GameCore.tens[(examineUnitMultiplier - 1) * this.unitIncrement] - (this.bound.sum() % GameCore.tens[(examineUnitMultiplier - 1) * this.unitIncrement] - this.bound.x());
								this.fillIterator(this.hmPtr, this.currentStride, this.currentStride + this.bound.x());
								if (super.hasNext()) {
									return true;
								} else {
									UnitBound = this.currentStride + this.bound.x();
									continue;
								}
							} else {
								// NOTE: this only works in 2D :(
								if (examineUnitMultiplier != this.bound.Dim()) {
									examineUnitMultiplier++;
									continue;

								} else {
									return false;
								}
							}
							// fill(currentStride)

							// find the next valid stride Index
							//
							// int boundDim = (this.currentStride +
							// this.bound.v[0]) % examineStrideDim;
							// while (boundDim == this.endIndex %
							// examineStrideDim) {
							// examineStrideDim =
							// GameCore.tens[examineUnitMultiplier *
							// this.unitIncrement];
							// boundDim = (this.currentStride + this.bound.v[0])
							// % examineStrideDim;
							// }

						}

					}
				}
				return super.hasNext();
			}
		}
	}// End of HashWorld

	public QueueDrawable getDrawables(EntityCamera camera) {
		// Find better way to iterate
		VectorInt bounds = camera.getIndexedCameraBounds();
		// upper bound this index to 9999 or make camera bounded
		QueueDrawable Result = new QueueDrawable();
		// for (int yIndex = bounds.v[0]; yIndex <= bounds.v[0] + bounds.v[2];
		// yIndex += 100) {
		// upper bound on this increase
		// this.world.setHashIterationRange(yIndex, yIndex + bounds.v[1]);
		this.world.setIterBounds(bounds.x(), (VectorInt) bounds.getRangeDim(1, bounds.dim - 1));
		for (Object ew : this.world) {
			EntityWorld willDraw = (EntityWorld) ew;
			VectorFloat ScreenPos = camera.getScreenPosFromWorld(willDraw.realPos);
			Drawable queueDraw = null;
			if (willDraw.resAddress == null) {
				queueDraw = new Drawable(ScreenPos);
				Result.push(queueDraw);
			} else {
                //draw with iterator?
				for (Integer resourceAddress : willDraw.toDraw()) {
                    if(resourceAddress == GameConst.POINT_RED)
                    {
                        ScreenPos = camera.getScreenPosFromWorld(willDraw.travel.realPos);
                    }
                    VectorFloat PixelDim = camera.worldRectToScreenRect(willDraw.getWorldRect());

					queueDraw = new Drawable(ScreenPos, PixelDim, resourceAddress);
					Result.push(queueDraw);
				}
			}
		}
		// }
		return Result;
	}
	/**
	 * draws the bounds of the unit in which the camera is located
	 * @param camera which camera to examin
	 * @return a queue of drawables based on where the lines need to be drawn
	 */
	public QueueDrawable drawUnitBounds(EntityCamera camera) {
		double unit = GameCore.tens[this.unitPower];
		QueueDrawable Result = new QueueDrawable();
		float xFloor = (float) (Math.floor(camera.getWorldX() * unit) / unit);
		float yFloor = (float) (Math.floor(camera.getWorldY() * unit) / unit);
		float xCeil = (float) (Math.floor(camera.getWorldX() * unit + 1) / unit);
		float yCeil = (float) (Math.floor(camera.getWorldY() * unit + 1) / unit);
		// find the difference between the corners and the camera
		VectorFloat checkCorner = new VectorFloat(xFloor, yFloor);
		VectorFloat ScreenPos1 = camera.getScreenPosFromWorld(checkCorner);
		VectorFloat ScreenPos2 = null;
		Drawable queueDraw = null;
		float lastDrawX = xFloor;
		float lastDrawY = yFloor;

		// counter clockwise....maybe should change? does it matter?
		for (int i = 0; i < 4; i++) {
			switch (i) {
				case 0: {
					checkCorner.v[0] = xCeil;
					checkCorner.v[1] = yFloor;
				}
					break;

				case 1: {
					checkCorner.v[0] = xCeil;
					checkCorner.v[1] = yCeil;
				}
					break;

				case 2: {
					checkCorner.v[0] = xFloor;
					checkCorner.v[1] = yCeil;
				}
					break;

				case 3: {
					checkCorner.v[0] = xFloor;
					checkCorner.v[1] = yFloor;
				}
					break;
			}
			ScreenPos2 = camera.getScreenPosFromWorld(checkCorner);
			queueDraw = new Drawable(ScreenPos1, ScreenPos2);
			Result.push(queueDraw);
			ScreenPos1 = ScreenPos2;
		}
		return Result;
	}

	// Mechanics/Behaviours

	// Process Direction vectors of inputs, magnitudes, etc...

	// Breakdown of capability at 21/5/2015
	/**
	 * Borderline no sleep on frame complete at: 50,000 entities generated 400
	 * worldIndex/frame ~10 entities/worldIndex 4000 entities updated/frame
	 */
	// Screen To World Methods
	float xPixelsPerUnit = 100.0f;
	float yPixelsPerUnit = 100.0f;
	private VectorInt gameSurfaceDim;

	public void setSurfaceDimensions(int x, int y) {
		this.gameSurfaceDim = new VectorInt(x, y);
	}

	public int getSurfaceWidth() {
		int Result = this.gameSurfaceDim.v[0];
		return Result;
	}

	public int getSurfaceHeight() {
		int Result = this.gameSurfaceDim.v[1];
		return Result;
	}

	@Deprecated
	// located in CameraEntity
	public VectorInt getScreenPosFromWorld(EntityWorld camera, EntityWorld check) {
		VectorFloat working = new VectorFloat(check.getWorldX() - camera.getWorldX(), check.getWorldY() - camera.getWorldY());
		float xUnits = gameSurfaceDim.v[0] / xPixelsPerUnit;
		float yUnits = gameSurfaceDim.v[1] / yPixelsPerUnit;
		float xPercent = (working.v[0] * xPixelsPerUnit) / xUnits;
		float yPercent = (working.v[1] * xPixelsPerUnit) / yUnits;
		// working.normalizeVector();
		VectorInt Result = new VectorInt((int) (this.gameSurfaceDim.v[0] * xPercent), (int) (this.gameSurfaceDim.v[1] * yPercent));
		return Result;
	}

	@Deprecated
	// located in CameraEntity
	public VectorFloat getWorldPosFromScreen(EntityWorld camera, VectorInt screenPos) {

		// units <- screenUnits, screenPos
		float xUnits = gameSurfaceDim.v[0] / xPixelsPerUnit;
		float yUnits = gameSurfaceDim.v[1] / yPixelsPerUnit;
		float xPercent = (1.0f * screenPos.v[0] / gameSurfaceDim.v[0]) / 100;
		float yPercent = (1.0f * screenPos.v[1] / gameSurfaceDim.v[1]) / 100;
		VectorFloat Result = new VectorFloat(camera.getWorldX() + xPercent * xUnits, camera.getWorldY() + yPercent * yUnits);
		return Result;
	}

	// ResourceMap
	int[] gameBitmaps = new int[GameConst.TOTAL_BITMAPS];

	public void setBitmapTargetAtIndex(int index, int value) {
		if (index < this.gameBitmaps.length) {
			this.gameBitmaps[index] = value;
		}
	}

	// Sound

	// TODO: Can turn this into a sound object
	int[] gameSounds = new int[GameConst.TOTAL_SOUND_SAMPLES];

	QueueSound playableSounds = new QueueSound();
	boolean soundsLoaded = false;

	public void setSoundTargetAtIndex(int index, int value) {
		if (index < this.gameSounds.length) {
			this.gameSounds[index] = value;
		}
	}

	public int getIndexOfSample(int sampleId) {
		for (int i = 0; i < GameConst.TOTAL_SOUND_SAMPLES; i++) {
			if (this.gameSounds[i] == sampleId) {
				return i;
			}
		}
		return -1;
	}

	public void playSoundAtIndex(int index) {
		if (!this.playableSounds.contains(this.gameSounds[index])) {
			playableSounds.push(this.gameSounds[index]);
		}
	}

	public QueueSound getSoundPlayables() {
		QueueSound Result = this.playableSounds;
		return Result;
	}

	public class QueueSound extends Queue<Integer> {
		public boolean contains(int value) {
			QueueSpot current = this.head;
			while (current != null) {
				if (current.getPayload() == value) {
					return true;
				}
				current = current.getNext();
			}
			return false;
		}
	}
}
