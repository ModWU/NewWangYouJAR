package cn.yunbee.cn.wangyoujar.views;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import cg.yunbee.cn.wangyoujar.utils.BitmapUtils;

public class CornerDrawable extends Drawable {
	
	private Paint mPaint;
	private RectF rectF;
	
	private int width, height;
	
	public CornerDrawable(Bitmap bmp, int width, int height) {
		bmp = BitmapUtils.resizeImage(bmp, width, height);
		this.width = bmp.getWidth();
		this.height = bmp.getHeight();
		BitmapShader shader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setShader(shader);
	}
	
	public CornerDrawable(Bitmap bmp) {
		this.width = bmp.getWidth();
		this.height = bmp.getHeight();
		BitmapShader shader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setShader(shader);
		
	}
	
	public CornerDrawable(int color, int width, int height) {
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		canvas.drawColor(color);
		this.width = width;
		this.height = height;
		BitmapShader shader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setShader(shader);
	}
	
	private float roundDegree;
	public void setRoundDegree(float degree) {
		roundDegree = degree;
	}
	
	public float getRoundDegree() {
		return roundDegree;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRoundRect(rectF, roundDegree, roundDegree, mPaint);
	}
	
	@Override
	public int getIntrinsicHeight() {
		return height;
	}
	
	@Override
	public int getIntrinsicWidth() {
		return width;
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
	
	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		//super.setBounds(left, top, right, bottom);
		rectF = new RectF(left, top, right, bottom);
	}

}
