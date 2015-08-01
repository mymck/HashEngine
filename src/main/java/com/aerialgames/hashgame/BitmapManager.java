package com.aerialgames.hashgame;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.content.Context;

public class BitmapManager {
	private Bitmap bmp;
	private int imageHeight;
	private int imageWidth;
	private String imageType;
	
	public BitmapManager(Context con)
	{		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = true;
		BitmapFactory.decodeResource(con.getResources(), R.drawable.sprite3, options);
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
		String imageType = options.outMimeType;
		//Bitmap.Config bmConfig = options.inPreferredConfig;
		options.inJustDecodeBounds = false;
		this.bmp = BitmapFactory.decodeResource(con.getResources(), R.drawable.sprite3, options);
	}
	public BitmapManager(Context con,int ResourceId)
	{		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inDither = true;
		BitmapFactory.decodeResource(con.getResources(), ResourceId, options);
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
		String imageType = options.outMimeType;
		Bitmap.Config bmConfig = options.inPreferredConfig;
		options.inJustDecodeBounds = false;
		this.bmp = BitmapFactory.decodeResource(con.getResources(), ResourceId, options);
	}
	public Bitmap getBitmap()
	{
		Bitmap Result = this.bmp;
		Result.setHasAlpha(true);
		return Result;
	}
}
