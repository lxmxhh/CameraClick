package com.samxu.cameraclick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

public class MyTextView extends View implements Runnable {

	private Paint mPaint = null;
	Typeface mTypeface = null;

	public MyTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();
		
		new Thread(this).start();
		
		//mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/awesomeco-webfont.ttf");
		mTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO: handle exception
				Thread.currentThread().interrupt();
			}
			postInvalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		mPaint.setAntiAlias(true);
		
		int w = canvas.getWidth();
		int h = canvas.getHeight();
		
		mPaint.setARGB(50, 0, 0, 0);
		canvas.drawRect(0,0,w,h, mPaint);
		
		mPaint.setARGB(255,255, 255, 255);
		mPaint.setTypeface(mTypeface);
		mPaint.setTextSize(40);
		canvas.drawText("i love shanghai 上海", 50, h-100, mPaint);
	}
}
