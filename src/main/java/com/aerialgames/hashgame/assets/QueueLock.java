package com.aerialgames.hashgame.assets;

import java.util.concurrent.Semaphore;

public class QueueLock<T> extends Queue<T> {
	private int lockCount = 1;
	protected Semaphore access = new Semaphore(this.lockCount, true);

	public boolean acquireLock() {
		try {
			this.access.acquire();
			return true;
		} catch (InterruptedException ie) {
			// Log.w(DEBUG_GAME_SURFACE, "Done waiting to for lock");
			return false;
		}
	}

	private boolean tryInputLock() {
		if (this.access.tryAcquire()) {
			return true;
		} else
			return false;

	}

	public void releaseLock() {
		this.access.release();
	}

	public void pushLock(T t) {
		acquireLock();
		super.push(t);
		releaseLock();

	}
	
	@Override
	public void join(Queue<T> queue)
	{
		acquireLock();
		super.join(queue);
		releaseLock();
	}

	@Override
	public void push(T t) {
	}

	public T popLock() {
		T Result = null;
		acquireLock();
		Result = super.pop();
		releaseLock();
		return Result;

	}

	@Override
	public T pop() {
		return null;
	}
}
