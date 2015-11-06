package com.paint.remon.paintbrush;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

@SuppressLint({ "DrawAllocation", "ClickableViewAccessibility" })
public class MyCanvas extends View implements OnTouchListener {

	Paint paint;
	Bitmap cache;

	
	int backgroundColor = Color.WHITE;
	int penColor = Color.BLACK;
	int strokeWidth = 2;
	Path path = new Path();
	boolean filled = false;
	float startX, startY, stopX, stopY, secondX, secondY;
	int counter = 0;
	float radius;
	float finalX, finalY;
	float prevX = -1, prevY = -1;
	public static String shapeType = "free";
	private boolean isDrawing = false;
	Canvas tempCanvas;

	public MyCanvas(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setBackgroundColor(backgroundColor);
		this.setOnTouchListener(this);

		paint = new Paint();
		paint.setColor(penColor);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Paint.Style.STROKE);
	}

	private void setVariables() {
		startX = startY = stopX = stopY = secondX = secondY = finalX = finalY = radius = counter = 0;
		isDrawing = false;
		path = new Path();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (cache == null) {
			cache = Bitmap.createBitmap(getWidth(), getHeight(),
					Config.ARGB_8888);
		}
		canvas.drawBitmap(cache, 0, 0, paint);

		paint.setColor(penColor);
		paint.setStrokeWidth(strokeWidth);

		if (isDrawing)
			if (shapeType.equalsIgnoreCase("line")) {
				canvas.drawLine(startX, startY, stopX, stopY, paint);
			} else if (shapeType.equalsIgnoreCase("rectangle")) {
				canvas.drawRect(startX, startY, stopX, stopY, paint);
			} else if (shapeType.equalsIgnoreCase("ellipse")) {
				canvas.drawOval(new RectF(startX, startY, stopX, stopY), paint);
			} else if (shapeType.equalsIgnoreCase("circle")) {
				canvas.drawCircle(startX, startY, radius, paint);
			} else if (shapeType.equalsIgnoreCase("square")) {
				canvas.drawRect(startX, startY, finalX, finalY, paint);
			} else if (shapeType.equalsIgnoreCase("triangle")) {
				if (counter == 1 && startX != 0 && secondX != 0) {
					canvas.drawLine(startX, startY, secondX, secondY, paint);
				} else if (counter == 2 && stopX != 0) {
					if (!filled) {
						canvas.drawLine(startX, startY, stopX, stopY, paint);
						canvas.drawLine(secondX, secondY, stopX, stopY, paint);
					} else {
						path = new Path();
						path.setFillType(Path.FillType.EVEN_ODD);
						path.moveTo(startX, startY);
						path.lineTo(stopX, stopY);
						path.lineTo(secondX, secondY);
						path.lineTo(startX, startY);
						path.close();
						canvas.drawPath(path, paint);
					}
				}
			}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Canvas c = new Canvas(cache);
		if (shapeType.equalsIgnoreCase("free")) {
			if (prevX > -1) {
				c.drawLine(prevX, prevY, event.getX(), event.getY(), paint);
			}
			prevX = event.getX();
			prevY = event.getY();

			if (event.getAction() == MotionEvent.ACTION_UP)
				prevX = -1;

			invalidate();
		} else if (shapeType.equalsIgnoreCase("line")) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				startY = event.getY();
				isDrawing = true;
				break;

			case MotionEvent.ACTION_MOVE:
				stopX = event.getX();
				stopY = event.getY();
				invalidate();
				break;

			case MotionEvent.ACTION_UP:
				stopX = event.getX();
				stopY = event.getY();
				isDrawing = false;
				c.drawLine(startX, startY, stopX, stopY, paint);
				invalidate();
				setVariables();
				break;
			default:
				return false;
			}
		} else if (shapeType.equalsIgnoreCase("rectangle")) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				startY = event.getY();
				isDrawing = true;
				break;

			case MotionEvent.ACTION_MOVE:
				stopX = event.getX();
				stopY = event.getY();
				invalidate();
				break;

			case MotionEvent.ACTION_UP:
				stopX = event.getX();
				stopY = event.getY();
				c.drawRect(startX, startY, stopX, stopY, paint);
				invalidate();
				setVariables();
				break;
			default:
				return false;

			}
		} else if (shapeType.equalsIgnoreCase("square")) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				startY = event.getY();
				isDrawing = true;
				break;

			case MotionEvent.ACTION_MOVE:
				stopX = event.getX();
				stopY = event.getY();

				if ((stopX > startX && stopY > startY)
						|| (startX > stopX && startY > stopY)) {
					finalX = stopX;
					finalY = stopX - startX + startY;
				} else {
					finalX = stopX;
					finalY = -stopX + startX + startY;
				}

				invalidate();
				break;

			case MotionEvent.ACTION_UP:
				stopX = event.getX();
				stopY = event.getY();
				isDrawing = false;

				if ((stopX > startX && stopY > startY)
						|| (startX > stopX && startY > stopY)) {
					finalX = stopX;
					finalY = stopX - startX + startY;
				} else {
					finalX = stopX;
					finalY = -stopX + startX + startY;
				}

				c.drawRect(startX, startY, finalX, finalY, paint);
				invalidate();
				setVariables();
				break;
			default:
				return false;

			}
		} else if (shapeType.equalsIgnoreCase("ellipse")) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				startY = event.getY();
				isDrawing = true;
				break;

			case MotionEvent.ACTION_MOVE:
				stopX = event.getX();
				stopY = event.getY();
				invalidate();
				break;

			case MotionEvent.ACTION_UP:
				stopX = event.getX();
				stopY = event.getY();
				isDrawing = false;
				c.drawOval(new RectF(startX, startY, stopX, stopY), paint);
				invalidate();
				setVariables();
				break;
			default:
				return false;
			}
		} else if (shapeType.equalsIgnoreCase("circle")) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = event.getX();
				startY = event.getY();
				isDrawing = true;
				break;

			case MotionEvent.ACTION_MOVE:
				stopX = event.getX();
				stopY = event.getY();
				radius = (float) Math.sqrt(Math.pow(startX - stopX, 2)
						+ Math.pow(startY - stopY, 2));
				invalidate();
				break;

			case MotionEvent.ACTION_UP:
				stopX = event.getX();
				stopY = event.getY();
				isDrawing = false;
				radius = (float) Math.sqrt(Math.pow(startX - stopX, 2)
						+ Math.pow(startY - stopY, 2));
				c.drawCircle(startX, startY, radius, paint);
				invalidate();
				setVariables();
				break;
			default:
				return false;

			}
		} else if (shapeType.equalsIgnoreCase("triangle")) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (counter == 0) {
					startX = event.getX();
					startY = event.getY();
					counter = 1;
				}

				break;
			case MotionEvent.ACTION_MOVE:
				if (counter == 1) {
					secondX = event.getX();
					secondY = event.getY();
					isDrawing = true;
				} else if (counter == 2) {
					stopX = event.getX();
					stopY = event.getY();
				}
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				if (isDrawing)
					if (counter == 1) {
						counter = 2;
						c.drawLine(startX, startY, secondX, secondY, paint);
					} else if (counter == 2) {
						stopX = event.getX();
						stopY = event.getY();
						if (filled) {
							path = new Path();
							path.setFillType(Path.FillType.EVEN_ODD);
							path.moveTo(startX, startY);
							path.lineTo(secondX, secondY);
							path.lineTo(stopX, stopY);
							path.lineTo(startX, startY);
							path.close();
							c.drawPath(path, paint);
						} else {
							c.drawLine(startX, startY, stopX, stopY, paint);
							c.drawLine(secondX, secondY, stopX, stopY, paint);
						}
						setVariables();
					}
				invalidate();
				break;
			default:
				return false;
			}
		}
		return true;
	}
}
