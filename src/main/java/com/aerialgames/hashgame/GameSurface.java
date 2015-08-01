package com.aerialgames.hashgame;

import java.lang.reflect.Array;
import java.util.concurrent.Semaphore;

import com.aerialgames.hashgame.assets.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log;

@SuppressWarnings("unused")
@SuppressLint({ "WrongCall", "ClickableViewAccessibility" })
public class GameSurface extends SurfaceView {

	private String DEBUG_GAME_SURFACE = "Game Surface Debug";
	private Bitmap[] bmp;
	private SurfaceHolder holder;
	public Rect surface;
	public Paint defaultPaint;
	public Paint paint2;
	public GameLoopThread gameLoop;
	public int x = 0;
	public int speed = 1;
	private long frame = 0;
	private int drawnThisFrame = 0;
	private int[] pixels;
	private MainActivity mAct;
	EntityCamera camera;
	private VectorFloat gameMapDim;
	private VectorFloat entityMapDim;
	private Hashmap drawableEntities;
	private QueueInput inputs = null;
	private Bitmap[] bitmaps;
	private QueueDrawable drawingEntities;
	public int touchUps = 0;
	public int touchDowns = 0;
	private GameCore core;

	public GameSurface(MainActivity context) {
		super(context);
		this.mAct = context;
		this.core = context.getGameCore();
		this.holder = getHolder();
		// addBitmapToDrawStack();
		buildPaintScheme();
		// this.drawableEntities = new Hashmap(100);
		this.inputs = new QueueInput();
		this.drawingEntities = new QueueDrawable();
		// Change this to relevant size depending on performance and
		// requirements
		this.bitmaps = new Bitmap[GameConst.TOTAL_BITMAPS];
	}

	/**
	 * used to take a larger universe range and map between 0 and |subset|
	 * 
	 * @param index
	 * @return
	 */
	public int f(int index, int range) {
		return index;
	}

	public void initGameBitmaps(int start, int end) {
		// This need to map the index from start->0,end->n
		// n = end-start
		int range = end - start;
		for (int i = start; i < end; i++) {
			this.bitmaps[f(i, range)] = new BitmapManager(this.getContext(), this.core.gameBitmaps[i]).getBitmap();
		}
	}

	public void joinNewDrawablesToQueue(QueueDrawable queue) {
		// possibly examining entities that exist already
		// might do this before queue
		// maybe store drawable hash in GameCore
		this.drawingEntities.join(queue);
	}

	public void buildPaintScheme() {
		this.defaultPaint = new Paint();
		this.defaultPaint.setTextSize(20.0f);
		this.defaultPaint.setColor(Color.GREEN);
		// this.defaultPaint.setAlpha(255);

		this.paint2 = new Paint();
		this.paint2.setColor(Color.RED);
	}

	// blocks
	public QueueInput ripQueue() {
		QueueInput Result = null;
		if (this.inputs.length > 0) {
			this.inputs.acquireLock();
			Result = this.inputs.chopInputs();
			this.inputs.releaseLock();
		}
		return Result;
	}

	public void addDrawableEntityToQueue(Drawable ed) {
		this.drawingEntities.push(ed);
	}

	public void addEntityToSurface(Entity ent) {
		this.drawableEntities.addHashPack(ent);
	}

	public Hashmap currentDrawableEntities() {
		Hashmap Result = this.drawableEntities;
		return Result;
	}

	public void configureGameSurface() {
		Canvas c = this.holder.lockCanvas();
		this.surface = this.holder.getSurfaceFrame();
		this.core.setSurfaceDimensions(Math.abs(this.surface.width()), Math.abs(this.surface.height()));
		this.holder.unlockCanvasAndPost(c);
		this.camera = this.core.getCameraEntity();
	}

	public void drawSurfaceEntities() {
		// Entities from draw list are examined
		// Associated Bitmap Added to surface
		// Requires a location/offset
		Canvas c = null;
		try {
			c = this.holder.lockCanvas();
			if (c != null) {
				synchronized (this.holder) {
					this.onDraw(c);
				}
			}
		} finally {
			if (c != null) {
				this.holder.unlockCanvasAndPost(c);
			}
		}
	}

	public void debug_addBitmapToDrawStack() {
		this.bmp = new Bitmap[3];
		BitmapManager bmM = new BitmapManager(this.mAct, R.drawable.red);
		this.bmp[0] = bmM.getBitmap();
		bmM = new BitmapManager(this.mAct, R.drawable.green);
		this.bmp[1] = bmM.getBitmap();
		bmM = new BitmapManager(this.mAct, R.drawable.blue);
		this.bmp[2] = bmM.getBitmap();
		// pixels = new int[this.bmp[1].getWidth() * this.bmp[1].getHeight()];
		// this.bmp[1].getPixels(pixels, 0, this.bmp[1].getWidth(), 0, 0,
		// this.bmp[1].getWidth(), this.bmp[0].getHeight());
	}

	private void renderPointOnScreen(VectorFloat entityDim, VectorFloat gameDim, VectorInt gameSurfaceDim, Canvas canvas) {
		Float RatioEntityX = entityDim.v[0] / gameDim.v[0];
		Float RatioEntityY = entityDim.v[1] / gameDim.v[1];

		int ScreenPosX = (int) (gameSurfaceDim.v[0] * RatioEntityX);
		int ScreenPosY = (int) (gameSurfaceDim.v[1] * RatioEntityY);

		addRect(canvas, ScreenPosX, ScreenPosY, 30, 30, 2);
	}

	private void renderQueuedDrawables(Canvas canvas) {
        VectorFloat screenRect = this.camera.worldRectToScreenRect(this.camera.getWorldRect());
		addBitmapWithScale(canvas, this.bitmaps[GameConst.BLACK_BALL], screenRect);
//        addBitmap(canvas, this.bitmaps[GameConst.POINT_RED],screenRect.x(),screenRect.y(),0,0);
        Drawable workingDrawable = this.drawingEntities.pop();
        screenRect = workingDrawable.getDrawRect();
        this.drawnThisFrame = 0;
		while (workingDrawable != null) {
            VectorFloat drawBounds = screenRect;

            boolean inFrame = (drawBounds.v[0] >= 0 && drawBounds.v[0] < this.camera.widthPx)
                    ||( drawBounds.v[1] >= 0 && drawBounds.v[1] < this.camera.heightPx)
                    ||( drawBounds.v[2] >= 0 && drawBounds.v[2] < this.camera.widthPx)
                    ||( drawBounds.v[3] >= 0 && drawBounds.v[3] < this.camera.heightPx);
            if(inFrame) {

                this.drawnThisFrame++;
                switch (workingDrawable.type) {
                    case box: {
                        addRect(canvas, (int) workingDrawable.getX(), (int) workingDrawable.getY(), (int) (screenRect.v[2] - workingDrawable.getY()), (int) (screenRect.v[1] - workingDrawable.getY()), workingDrawable.getDrawLevel());
                    }
                    break;
                    case resource: {
                        if (workingDrawable.resAddress == GameConst.BLACK_BALL || workingDrawable.resAddress == GameConst.BODY_BULB) {
                            Bitmap workingBitmap = this.bitmaps[workingDrawable.resAddress];
//                        way too slow
//                        Bitmap.createScaledBitmap(workingBitmap,(int)(screenRect.v[2]-screenRect.v[0]),(int)(screenRect.v[3]-screenRect.v[1]),false);
                            addBitmapWithScale(canvas, workingBitmap, screenRect);
//                        addBitmap(canvas, this.bitmaps[working.resAddress], working.getX(),working.getY(),-this.bitmaps[working.resAddress].getWidth()/2,-this.bitmaps[working.resAddress].getHeight()*0.9f);
                        } else if (workingDrawable.resAddress == GameConst.POINT_RED) {
//                        addBitmap(canvas, this.bitmaps[working.resAddress], working.getX(),working.getY(),-this.bitmaps[working.resAddress].getWidth()/2,-this.bitmaps[working.resAddress].getHeight()*0.9f);
                            addBitmapWithScale(canvas, this.bitmaps[workingDrawable.resAddress], screenRect);
                        }
                    }
                    break;
                    case line: {
                        addLine(canvas, workingDrawable.getDrawPositions());
                    }
                    break;
                    default: {
                    }
                    break;
                }
            }
			// if (bounds.v[0] > working.getX()) {
			// bounds.v[0] = working.getX();
			// }
			// if (bounds.v[1] > working.getY()) {
			// bounds.v[1] = working.getY();
			// }
			// if (bounds.v[2] < working.getX()) {
			// bounds.v[2] = working.getX();
			// }
			// if (bounds.v[3] < working.getY()) {
			// bounds.v[3] = working.getY();
			// }
			workingDrawable = this.drawingEntities.pop();
		}
		// addRect(canvas,bounds);
	}

	private void renderDrawables(Canvas canvas) {
		// in Raw as foreach can't cast to CameraDrawable
		for (Object raw : this.drawableEntities) {
			CameraDrawable eCam = (CameraDrawable) raw;
			addRect(canvas, eCam.getX(), eCam.getY(), eCam.getSizeX(), eCam.getSizeY(), eCam.getDrawLevel());
		}
	}

	public int queueLength = 0;
	float angle = 0;
	public int queueFrameDifference = 0;

	String debug1 = "";
	String debug2 = "";
	String debug3 = "";
	String debug4 = "";
    String debug5 = "";
	float xTouch = 0.0f;
	float yTouch = 0.0f;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.GRAY);
		float xCenter = this.camera.widthPx / 2.0f;
		float yCenter = this.camera.heightPx / 2.0f;
		// angle += (float)Math.sin(queueLength++/Math.PI);
		// angle += 0.2f;
		// canvas.rotate(angle,xCenter,yCenter);
		renderQueuedDrawables(canvas);
		if (this.gameLoop != null) {
			debug3 = "FRAME " + this.gameLoop.sleepTime;
			debug4 = "Updated this Frame " + this.gameLoop.updatedThisFrame;
            debug5 = "Drawn this Frame " + this.drawnThisFrame;

		}
			VectorFloat world = this.camera.realPos;
			debug1 = "Camera Pos X: " + world.v[0] + " Y: " + world.v[1];
			debug2 = "X: " + this.xTouch+ " Y: " + this.yTouch;
		canvas.drawText(debug1, xCenter, 100.0f, this.defaultPaint);
		canvas.drawText(debug2, xCenter, 125.0f, this.defaultPaint);
		canvas.drawText(debug3, xCenter, 160.0f, this.defaultPaint);
		canvas.drawText(debug4, xCenter, 190.0f, this.defaultPaint);
        canvas.drawText(debug5, xCenter, 220.0f, this.defaultPaint);
	}

	// String debugRect = this.gameSurfaceDim.getDim(1) / 2 + " \n" +
	// this.surface.left + " \n" + this.surface.right + " \n" +
	// this.surface.bottom;

	protected void addRect(Canvas canvas, int x, int y, int width, int height, int drawLevel) {
		Rect r = new Rect(x, y, x + width, y + height);
		canvas.drawRect(r, this.defaultPaint);
		// canvas.drawText("" + x + "," + y, x, y, this.paint2);
	}

	protected void addRect(Canvas canvas, VectorInt bounds) {
		Rect r = new Rect(bounds.v[0], bounds.v[1], bounds.v[2], bounds.v[3]);
		canvas.drawRect(r, this.defaultPaint);
		// canvas.drawText("" + x + "," + y, x, y, this.paint2);
	}

	// add offset?
	protected void addBitmap(Canvas canvas, Bitmap bmap, float x, float y,float offsetX, float offsetY) {
		//Bitmaps are drawn with offset baked in
		canvas.drawBitmap(bmap, x+offsetX, y+offsetY, this.defaultPaint);
	}
    protected void addBitmapWithScale(Canvas canvas, Bitmap bmap, VectorFloat scaleRect) {
        //Bitmaps are drawn with offset baked in
        canvas.drawBitmap(bmap, new Rect(0,0,bmap.getWidth(),bmap.getHeight()),new RectF(scaleRect.v[0],scaleRect.v[1],scaleRect.v[2],scaleRect.v[3]),this.defaultPaint);
//        canvas.drawBitmap(bmap, new Rect(0,0,bmap.getWidth(),bmap.getHeight()),new Rect(scaleRect.v[0].intValue(),scaleRect.v[1].intValue(),scaleRect.v[2].intValue(),scaleRect.v[3].intValue()),this.defaultPaint);
    }
	protected void addLine(Canvas canvas, VectorFloat vector) {
		if (vector.dim == 4) {
			canvas.drawLine(vector.v[0], vector.v[1], vector.v[2], vector.v[3], this.defaultPaint);
		}
	}

	MotionEvent.PointerProperties[] pntrProperties;
	MotionEvent.PointerCoords[] pntrCoords;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// this.inputs.acquireInputLock();
		InputPosition inputInstance = null;
		int action = event.getActionMasked();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL: {
				inputInstance = new InputPosition((int) event.getX(), (int) event.getY(), event.getActionIndex(), event.getEventTime(), InputPosition.InputAction.CANCEL);
				VectorFloat pos = this.camera.getWorldPosFromScreen(inputInstance.coords);
				this.xTouch = pos.x();
				this.yTouch = pos.y();
				break;
			}
		}
		// QueueInput[] bundle = new QueueInput[10];//new InputPosition((int)
		// event.getX(), (int) event.getY(), event.getActionIndex(),
		// event.getEventTime(), GameSurface.InputAction.DOWN);
		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				inputInstance.setAction(InputPosition.InputAction.DOWN);
				this.core.pushControllerInput(inputInstance);
//				this.touchDowns++;
				break;
			}
			case MotionEvent.ACTION_UP: {
				inputInstance.setAction(InputPosition.InputAction.UP);
				this.core.pushControllerInput(inputInstance);
//				this.touchUps++;
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				for (int h = 0; h < event.getHistorySize(); h++) {
					for (int p = 0; p < event.getPointerCount(); p++) {
						this.core.pushControllerInput(new InputPosition((int) event.getHistoricalX(p, h), (int) event.getHistoricalY(p, h), event.getActionIndex(), event.getEventTime(), InputPosition.InputAction.NEXT));
					}
				}
				for (int p = 0; p < event.getPointerCount(); p++) {
					this.core.pushControllerInput(new InputPosition((int) event.getX(p), (int) event.getY(p), event.getActionIndex(), event.getEventTime(), InputPosition.InputAction.NEXT));
				}
				break;
			}
			case MotionEvent.ACTION_POINTER_DOWN: {
				inputInstance.setAction(InputPosition.InputAction.DOWN);
				this.core.pushControllerInput(inputInstance);
//				this.touchDowns++;
				break;
			}
			case MotionEvent.ACTION_POINTER_UP: {
				inputInstance.setAction(InputPosition.InputAction.UP);
				this.core.pushControllerInput(inputInstance);
//				this.touchUps++;
				break;
			}
			case MotionEvent.ACTION_CANCEL: {
				this.core.pushControllerInput(inputInstance);
				// this.inputs.push(new InputPosition((int)
				// event.getX(event.getActionIndex()), (int)
				// event.getY(event.getActionIndex()), event.getActionIndex(),
				// event.getEventTime(), GameSurface.InputAction.UP));
				// treat this as action up without any interaction?
				// this.touchUps++;
				break;
			}
		}
		// this.inputs.releaseInputLock();
		return true;
	}

	private void queueInput() {

	}

	void printSamples(MotionEvent ev) {
		final int historySize = ev.getHistorySize();
		final int pointerCount = ev.getPointerCount();
		for (int h = 0; h < historySize; h++) {

			Log.w(DEBUG_GAME_SURFACE, "At time " + ev.getHistoricalEventTime(h) + ":");
			for (int p = 0; p < pointerCount; p++) {
				Log.w(DEBUG_GAME_SURFACE, " pointer (" + ev.getPointerId(p) + ": (" + ev.getHistoricalX(p, h) + ", " + ev.getHistoricalY(p, h) + ")\n");
			}
		}
		Log.w(DEBUG_GAME_SURFACE, "At time " + ev.getEventTime() + ":");
		for (int p = 0; p < pointerCount; p++) {
			Log.w(DEBUG_GAME_SURFACE, "  pointer " + ev.getPointerId(p) + ": (" + ev.getX(p) + ", " + ev.getY(p) + ")\n");
		}

	}
}
