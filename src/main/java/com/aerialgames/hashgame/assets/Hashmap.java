package com.aerialgames.hashgame.assets;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/*
 * implement hash sorting into tree
 */
@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
public class Hashmap implements Iterable {

	protected HashPointer<HashPack<?>>[] hash;
	private int hashElementDim = 0;
	public int indexMax = 0;
	private int countAtIndex[];
	public Timer interval;
	private int startHash = -1;
	private int endHash = -1;

	@Override
	public String toString() {
		for (int i = 0; i < this.indexMax; i++) {
			HashPointer<HashPack<?>> hashIndexPack = this.hash[i];
			while (hashIndexPack != null) {
				System.out.println(hashIndexPack.getPayload());
				hashIndexPack = hashIndexPack.getNextHashPointer();
			}
		}
		return "";
	}

	public Hashmap(int indexSize) {
		this.indexMax = indexSize;
		this.countAtIndex = new int[this.indexMax];
		Arrays.fill(this.countAtIndex, 0);
		HashPointer<HashPack<?>> nullElement = new HashPointer<HashPack<?>>(null, null);
		this.hash = (HashPointer<HashPack<?>>[]) Array.newInstance(nullElement.getClass(), this.indexMax);
		this.startHash = 0;
		this.endHash = indexSize - 1;
	}

	public Hashmap(int indexExponent, HashPack... elements) {
		this.indexMax = (int) Math.pow(10, indexExponent);
		this.countAtIndex = new int[this.indexMax];
		Arrays.fill(this.countAtIndex, 0);
		HashPointer<HashPack<?>> nullElement = new HashPointer<HashPack<?>>(null, null);
		this.hash = (HashPointer<HashPack<?>>[]) Array.newInstance(nullElement.getClass(), this.indexMax);
		Timer t1 = new Timer("Build Array");
		for (HashPack hp : elements) {
			HashPointer<HashPack<?>> tempPack = new HashPointer<HashPack<?>>(hp, null);
			int hIndex = hashIndex(hp.getIndex());
			assignSelectedIndex(hIndex, tempPack);
		}
		t1.report();
		this.startHash = 0;
		this.endHash = indexMax - 1;
	}

	public void clearHashIndex(int index) {
		if (index >= 0 && index < this.indexMax)
			this.hash[index] = null;
	}

	// HashFunction in Hashmap
	private int hashIndex(int preHash) {
		int Result = preHash % this.indexMax;
		return Result;
	}

	public boolean addHashPack(HashPack hp) {
		HashPointer<HashPack<?>> hpt = new HashPointer(hp, null);
		assignSelectedIndex(hashIndex(hp.getIndex()), hpt);
		if (checkHashBlockForContentsSingle(hashIndex(hp.getIndex()), hp)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean removeHashPack(HashPack hp) {
		if (hp != null) {
			int workingIndex = hashIndex(hp.getIndex());
			HashPointer<HashPack<?>> hpt = null;
			for (int i = 0; i < this.indexMax; i++) {
				hpt = this.searchHashBlockSingle(i, hp);
				if (hpt != null) {
					break;
				}
			}

			if (hpt != null) {
				if (this.countAtIndex[workingIndex] > 0) {
					reassignHashPointers(hpt.getPrevHashPointer(), hpt.getNextHashPointer(), workingIndex);
					this.countAtIndex[workingIndex]--;
				}
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public int getCountAtIndex(int index) {
		int Result = this.countAtIndex[index];
		return Result;
	}

	public int getMaxIndex() {
		int Result = this.indexMax;
		return Result;
	}

	private HashPointer<HashPack<?>> getHashBlock(int hashIndex) {
		HashPointer<HashPack<?>> Result = this.hash[hashIndex];
		return Result;
	}

	public HashPack getHashAtIndex(int hashIndex) {
		// think about reworking this
		if (this.hash[hashIndex] == null) {
			return null;
		}
		HashPack Result = this.hash[hashIndex].getPayload();
		return Result;

	}

	private void assignSelectedIndex(int hashIndex, HashPointer<HashPack<?>> hpt) {
		HashPointer<HashPack<?>> workingHashPack = this.hash[hashIndex];
		if (workingHashPack != null) {
			hpt.setPrevHashPointer(null);
			hpt.setNextHashPointer(workingHashPack);
			this.hash[hashIndex] = hpt;
			this.countAtIndex[hashIndex]++;
	//		sortHashBlockSingle(hashIndex, hpt);
		} else {
			hpt.setPrevHashPointer(null);
			hpt.setNextHashPointer(null);
			this.hash[hashIndex] = hpt;
			this.countAtIndex[hashIndex]++;
		}
	}

	private void reassignHashPointers(HashPointer hpt1, HashPointer hpt2, int hashIndex) {
		if (hpt1 == null && hpt2 == null) {
			this.hash[hashIndex] = null;
		} else if (hpt1 == null && hpt2 != null) {
			hpt2.setPrevHashPointer(null);
			this.hash[hashIndex] = hpt2;
		} else if (hpt2 == null && hpt1 != null) {
			hpt1.setNextHashPointer(null);
		} else {
			hpt1.setNextHashPointer(hpt2);
		}
	}

	public void reassignHashIndex(HashPack hp, int oldHash) {
		int oldHashIndex = hashIndex(oldHash);
		HashPointer<HashPack<?>> hptMoving = searchHashBlockSingle(oldHashIndex, hp);
		reassignHashPointers(hptMoving.getPrevHashPointer(), hptMoving.getNextHashPointer(), oldHashIndex);
		assignSelectedIndex(hashIndex(hp.getIndex()), hptMoving);
		this.countAtIndex[oldHash]--;
	}

	private void fillSelectedIndex(int hashIndex, HashPointer<?> ele) {
		Arrays.fill(this.hash, hashIndex, hashIndex, ele);
	}

	private HashPointer<HashPack<?>> searchHashBlockSingle(int hashIndex, HashPack hp) {
		HashPointer<HashPack<?>> hptTestIndex = this.hash[hashIndex];
		while (hptTestIndex != null) {
			if (hptTestIndex.getPayload().equals(hp)) {
				return hptTestIndex;
			}
			hptTestIndex = hptTestIndex.getNextHashPointer();
		}
		return null;
	}

	public boolean checkHashBlockForContentsSingle(int hashIndex, HashPack hp) {
		boolean Result = (searchHashBlockSingle(hashIndex, hp) != null);
		return Result;
	}
/*
	private void sortHashBlockSingle(int hashIndex, HashPointer<T extends HashPack<?>> hpt) {
		HashPointer<HashPack<T>> hptNextTemp = hpt.getNextHashPointer();
		HashPointer<HashPack<T>> hptPrevTemp = hpt.getPrevHashPointer();
		while (hptNextTemp != null && hpt.getPayload().comparePayload(hptNextTemp.getPayload()) < 1) {
			if (hptPrevTemp != null) {
				hptPrevTemp.setNextHashPointer(hptNextTemp);
			} else {
				hptNextTemp.setPrevHashPointer(null);
				this.hash[hashIndex] = hptNextTemp;
			}

			if (hptNextTemp != null) {
				hpt.setNextHashPointer(hptNextTemp.getNextHashPointer());
				hptNextTemp.setNextHashPointer(hpt);
				hptPrevTemp = hptNextTemp;
				hptNextTemp = hpt.getNextHashPointer();
			} else {
				break;
			}
		}
	}
*/
	private HashPointer<HashPack<?>> findMaxElement(HashPointer<HashPack<?>>[] elements) {
		// O(n) runtime scan each element
		Timer t = new Timer("Find Max Element");
		HashPointer<HashPack<?>> Result = elements[0];
		for (HashPointer<HashPack<?>> ele : elements) {
			if (hashIndex(Result.getPayload().getIndex()) < hashIndex(ele.getPayload().getIndex())) {
				Result = ele;
			}
		}
		t.report();
		return Result;
	}

	public Iterator iterator() {
		Iterator Result = new HashIterator(this, this.startHash, this.endHash);
		this.startHash = 0;
		this.endHash = this.indexMax - 1;
		return Result;
	}

	// When using this for collision
	// Add the largest size of an entity to the range so that we can find any
	// entities that should be hit but are not indexed within the default range
	public void setHashIterationRange(int start, int end) {
		this.startHash = start;
		this.endHash = end;
	}

	protected class HashIterator implements Iterator<HashPack> {
		HashPointer<HashPack<?>>[] hMap;
		HashPointer<HashPack<?>> currentPt;
		int currentIndex = 0;
		int indices = 0;

		public HashIterator(Hashmap hm, int start, int end) {
			this.indices = end - start + 1;
			currentPt = new HashPointer<HashPack<?>>(null, null);
			this.hMap = (HashPointer<HashPack<?>>[]) Array.newInstance(currentPt.getClass(), this.indices);
			fillIterator(hm, start, end);

		}
		
		public void fillIterator(Hashmap hm, int startIndex, int endIndex)
		{
			for (int i = startIndex; i <= endIndex; i++) {
				this.hMap[i - startIndex] = hm.getHashBlock(i);
			}
			this.currentPt = this.hMap[0];
			currentIndex = 0;
		}

		public boolean hasNext() {
			if (this.currentPt != null) {
				return true;
			} else {
				while (this.currentPt == null && this.currentIndex < this.indices) {
					this.currentPt = this.hMap[currentIndex++];
				}
				if (this.currentPt != null) {
					return true;
				} else {
					return false;
				}
			}
		}

		public HashPack next() {
			HashPack Result = this.currentPt.getPayload();
			this.currentPt = this.currentPt.getNextHashPointer();
			return Result;
		}

		@Override
		public void remove() {

		}
	}

	private class HashPointer<T extends HashPack<?>> {
		T payload;
		HashPointer<T> next, last;
		// int hashIndex = -1;
		int indexStrat = 0;

		public HashPointer(T hp, HashPointer<T> nextHash) {
			this.payload = hp;
			// this.indexStrat = indexStrategy;
			this.next = nextHash;
		}

		public T getPayload() {
			T Result = this.payload;
			return Result;
		}

		public boolean setPayload(T load) {
			this.payload = load;
			return true;
		}

		public HashPointer<T> getNextHashPointer() {
			return this.next;
		}

		public HashPointer<T> getPrevHashPointer() {
			return this.last;
		}

		public boolean setNextHashPointer(HashPointer<T> hashPointer) {
			this.next = hashPointer;
			if (hashPointer != null && hashPointer.getPrevHashPointer() != this)
				hashPointer.setPrevHashPointer(this);
			return true;
		}

		public boolean setPrevHashPointer(HashPointer<T> hashPointer) {
			this.last = hashPointer;
			if (hashPointer != null && hashPointer.getNextHashPointer() != this)
				hashPointer.setNextHashPointer(this);
			return true;
		}

		@Override
		public String toString() {
			return this.payload.toString();
		}
	}
}
