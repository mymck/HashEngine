package com.aerialgames.hashgame.assets;

public interface HashPack<T extends Object> {
	//A HashPack getIndex provides data to hash.
	//Allows specific material to remain out of key
	public int getIndex();
	public T getPayload();
	public int comparePayload(HashPack<T> hp);
	public Class<?> getType();
}
