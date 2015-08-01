package com.aerialgames.hashgame;

import com.aerialgames.hashgame.InputPosition;
import com.aerialgames.hashgame.assets.QueueLock;

public class QueueInput extends QueueLock<InputPosition> {
	// QueueInput should manage a HashQueue? or just array of Queues
	public QueueInput() {
		super();
	}

	/**
	 * bad
	 * 
	 * @param queue
	 *            the queue to assign an additional queue delegate
	 */
	@Deprecated
	public QueueInput(QueueInput queue) {
		
	}

	/**
	 * Blocks until queue is not being added to. Eliminates all pointers in this
	 * queue.
	 * 
	 * @return a direct copy of this queue with a separate lock.
	 */
	public QueueInput chopInputs() {
		QueueInput Result = new QueueInput();
		this.acquireLock();
		Result.head = this.head;
		this.head = null;
		Result.tail = this.tail;
		this.tail = null;
		Result.length = this.length;
		this.length = 0;
		this.releaseLock();
		return Result;
	}
}