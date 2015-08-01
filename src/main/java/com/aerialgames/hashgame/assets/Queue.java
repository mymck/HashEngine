package com.aerialgames.hashgame.assets;

import com.aerialgames.hashgame.InputPosition;

import android.util.Log;

public abstract class Queue<T> {
	protected QueueSpot head = null;
	protected QueueSpot tail = null;
	public int length = 0;
	protected static String DEBUG_QUEUE = "Queue Debug";

	public Queue() {

	}

	public Queue(Queue<T> queue) {
		this.head = queue.head;
		this.tail = queue.tail;
		this.length = queue.length;
	}

	public void push(T adding) {
		if (this.head == null) {
			this.head = new QueueSpot(adding);
			this.tail = this.head;
			this.length = 1;
		} else {
			QueueSpot temp = this.tail;
			this.tail = new QueueSpot(adding);
			temp.setNext(this.tail);
			this.length++;
		}
	}

	public T pop() {
		T Result = null;
		if (!isEmpty()) {
			if (this.head.getNext() == null) {
				Result = this.head.getPayload();
				this.head = null;
				this.tail = null;
				this.length = 0;
			} else {
				Result = this.head.getPayload();
				this.head = this.head.getNext();
				this.length--;
			}
		}
		return Result;
	}

	public T peek() {

		T Result = null;
		if (this.head != null) {
			Result = this.head.getPayload();
		}
		return Result;
	}
	public T peekHead() {

		T Result = null;
		if (this.head != null) {
			Result = this.head.getPayload();
		}
		return Result;
	}
	public T peekTail() {

		T Result = null;
		if (this.head != null) {
			Result = this.tail.getPayload();
		}
		return Result;
	}
	public void join(Queue<T> queue) {
		if(queue.head == null)
		{
			return;
		}
		if (this.tail != null) {
//			QueueSpot temp = new QueueSpot(queue.head.payload);
			this.tail.setNext(queue.head);
			this.tail = queue.tail;
		} else {
			this.head = queue.head;
			this.tail = queue.tail;
		}
		this.length += queue.length;
	}

	public boolean isEmpty() {
		return this.head == null;
	}

	protected class QueueSpot {
		private QueueSpot next;
		private T payload;

		public QueueSpot(T pay) {
			this.payload = pay;
		}

		public QueueSpot getNext() {
			return this.next;
		}

		public void setNext(QueueSpot next) {
			this.next = next;
		}

		public T getPayload() {
			return this.payload;
		}
	}
}
