package com.paint.remon.paintbrush;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class MainActivity extends AppCompatActivity {

	public static final String TAG = "com.remon.paintbrush";
	public static MyCanvas drawingArea;
	private int backgroundColor;
	private int penColor;
	private int strokeWidth;

	final int GALLERY_INTENT_CALLED = 3;
	Drawable image;
	private Bitmap bmp;
	private Bitmap alteredBitmap;
	private Paint paint;
	private Canvas canvas;
	private Matrix matrix;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		drawingArea = new MyCanvas(this);
		drawingArea.shapeType = "free";
		setContentView(drawingArea);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode,
		      Intent intent) {
		    super.onActivityResult(requestCode, resultCode, intent);

		    if (resultCode == RESULT_OK) {
		      Uri imageFileUri = intent.getData();
		      try {
		        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		        bmpFactoryOptions.inJustDecodeBounds = true;
		        bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
		                imageFileUri), null, bmpFactoryOptions);

		        bmpFactoryOptions.inJustDecodeBounds = false;
		        bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(
		                imageFileUri), null, bmpFactoryOptions);

		        
		        alteredBitmap = Bitmap.createBitmap(drawingArea.getWidth(), drawingArea.getHeight(), bmp.getConfig());
		        
		        canvas = new Canvas(alteredBitmap);
		        paint = new Paint();
		        paint.setColor(Color.GREEN);
		        paint.setStrokeWidth(5);
		        matrix = new Matrix();
		        canvas.drawBitmap(bmp, matrix, paint);

		        drawingArea.cache=alteredBitmap;
		        
		      } catch (Exception e) {
		        Log.v("ERROR", e.toString());
		      }
		    }
		  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("ShowToast")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.free) {
			MyCanvas.shapeType = "free";
			Toast.makeText(getApplicationContext(), "Free Drawing", Toast.LENGTH_LONG).show();
		} else if (id == R.id.line) {
			MyCanvas.shapeType = "line";
			Toast.makeText(getApplicationContext(), "Line", Toast.LENGTH_LONG).show();
		} else if (id == R.id.rectangle) {
			MyCanvas.shapeType = "rectangle";
			Toast.makeText(getApplicationContext(), "Rectangle", Toast.LENGTH_LONG).show();
		} else if (id == R.id.ellipse) {
			MyCanvas.shapeType = "ellipse";
			Toast.makeText(getApplicationContext(), "Ellipse", Toast.LENGTH_LONG).show();
		} else if (id == R.id.circle) {
			MyCanvas.shapeType = "circle";
			Toast.makeText(getApplicationContext(), "Circle", Toast.LENGTH_LONG).show();
		} else if (id == R.id.square) {
			MyCanvas.shapeType = "square";
			Toast.makeText(getApplicationContext(), "Square", Toast.LENGTH_LONG).show();
		} else if (id == R.id.background_color) {
			AmbilWarnaDialog colorDialog = new AmbilWarnaDialog(this,
					Color.WHITE, new OnAmbilWarnaListener() {
						@Override
						public void onOk(AmbilWarnaDialog dialog, int color) {
							drawingArea.setBackgroundColor(color);
							drawingArea.backgroundColor = color;
						}

						@Override
						public void onCancel(AmbilWarnaDialog dialog) {
							// cancel was selected by the user
						}
					});
			colorDialog.show();
		} else if (id == R.id.pen_color) {
			AmbilWarnaDialog colorDialog = new AmbilWarnaDialog(this,
					Color.WHITE, new OnAmbilWarnaListener() {
						@Override
						public void onOk(AmbilWarnaDialog dialog, int color) {
							drawingArea.paint.setColor(color);
							drawingArea.penColor = color;
						}

						@Override
						public void onCancel(AmbilWarnaDialog dialog) {
							// cancel was selected by the user
						}
					});
			colorDialog.show();
		} else if (id == R.id.pen_width) {
			final EditText input = new EditText(MainActivity.this);
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("Stroke Width")
					.setMessage("Enter an integer")
					.setView(input)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									String width = input.getText().toString();
									if (isInteger(width)) {
										Toast.makeText(getApplicationContext(),
												"Stroke Width = " + width,
												Toast.LENGTH_LONG).show();
										strokeWidth = Integer.parseInt(width);
										drawingArea.strokeWidth = strokeWidth;
										drawingArea.paint
												.setStrokeWidth(strokeWidth);
									} else {
										Toast.makeText(
												getApplicationContext(),
												"Invalid input for stroke width",
												Toast.LENGTH_LONG).show();
									}

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// Do nothing.
								}
							}).show();
		} else if (id == R.id.clear) {
			backgroundColor = drawingArea.backgroundColor;
			penColor = drawingArea.penColor;
			strokeWidth = drawingArea.strokeWidth;
			drawingArea = new MyCanvas(this);
			drawingArea.backgroundColor = backgroundColor;
			drawingArea.penColor = penColor;
			drawingArea.strokeWidth = strokeWidth;
			drawingArea.setBackgroundColor(backgroundColor);
			drawingArea.paint.setColor(penColor);
			drawingArea.paint.setStrokeWidth(strokeWidth);
			setContentView(drawingArea);
		} else if (id == R.id.save) {
			startService(new Intent(getBaseContext(), MyIntentService.class));
			MyIntentService.counter++;
		} else if (id == R.id.load) {
			Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		     startActivityForResult(choosePictureIntent, 0);
		}else if(id == R.id.triangle){
			MyCanvas.shapeType = "triangle";
			Toast.makeText(getApplicationContext(), "Triangle", Toast.LENGTH_LONG).show();
		}else if(id == R.id.filled){
			drawingArea.paint.setStyle(Paint.Style.FILL_AND_STROKE);
			drawingArea.filled=true;
			Toast.makeText(getApplicationContext(), "Filled Shapes", Toast.LENGTH_LONG).show();
		}else if(id == R.id.not_filled){
			drawingArea.paint.setStyle(Paint.Style.STROKE);
			drawingArea.filled=false;
			Toast.makeText(getApplicationContext(), "Unfilled Shapes", Toast.LENGTH_LONG).show();
		}

		return super.onOptionsItemSelected(item);
	}

	public static boolean isInteger(String num) {
		for (int i = 0; i < num.length(); i++) {
			if (num.charAt(i) < '0' || num.charAt(i) > '9')
				return false;
		}
		return true;
	}
}
