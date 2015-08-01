package com.aerialgames.hashgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
public class GameView extends View {
      private Bitmap bmp;
 
      public GameView(Context context) {
            super(context);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
      }
      @Override
      protected void onDraw(Canvas canvas) {
    	  //Use a bitmap stack
          canvas.drawColor(Color.BLACK);
          canvas.drawBitmap(bmp, 200, 100, null);
      }
}