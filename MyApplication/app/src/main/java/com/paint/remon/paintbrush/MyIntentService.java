package com.paint.remon.paintbrush;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.widget.Toast;

public class MyIntentService extends IntentService {

	public static int counter = 1;

	public MyIntentService() {
		super("MyIntentService");
		Log.d("MyIntentService", "consturctor");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.d("MyIntentService", "onHandleIntent");
		Bitmap bitmap = Bitmap.createBitmap(
				MainActivity.drawingArea.getWidth(),
				MainActivity.drawingArea.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		MainActivity.drawingArea.draw(canvas);

		File file1 = new File(getFilesDir().getAbsolutePath() + "/paint"
				+ counter + ".jpeg");
		File file2 = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"paint");

		Log.d(MainActivity.TAG, file1.getAbsolutePath());
		Log.d(MainActivity.TAG, file2.getAbsolutePath());
		FileOutputStream fos1 = null;
		FileOutputStream fos2 = null;
		try {

			fos1 = new FileOutputStream(file1);
			fos2 = new FileOutputStream(file2);
			Log.d(MainActivity.TAG, "Saved 1");
		} catch (FileNotFoundException e) {
			Toast.makeText(getApplicationContext(), "Cant not save it",
					Toast.LENGTH_LONG).show();
			Log.e(MainActivity.TAG, e.toString());
			Log.d(MainActivity.TAG, "Cant not save it 1");

		}
		if (fos1 != null) {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos1);
			try {
				fos1.flush();
				Log.d(MainActivity.TAG, "fos1 flushed.");
			} catch (IOException e) {
				Log.d(MainActivity.TAG, "Can not flush fos1.");
				e.printStackTrace();
			}
			try {
				fos1.close();
				Log.d(MainActivity.TAG, "fos1 closed.");
			} catch (IOException e) {
				Log.d(MainActivity.TAG, "Can not close fos1.");
				e.printStackTrace();
			}

			String url = Images.Media.insertImage(getContentResolver(), bitmap,
					"Paint" + counter + ".jpg", null);
			Log.d(MainActivity.TAG, "Saved 2 in " + url);
		} else {
			Log.d(MainActivity.TAG, "Cant not save it 2");
			Toast.makeText(getApplicationContext(), "fos1 = null",
					Toast.LENGTH_LONG).show();
		}

		if (fos2 != null) {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
			try {
				fos2.flush();
				Log.d(MainActivity.TAG, "fos 2 flushed.");
			} catch (IOException e) {
				Log.d(MainActivity.TAG, "Can not flush fos2.");
				e.printStackTrace();
			}
			try {
				fos1.close();
				Log.d(MainActivity.TAG, "fos2 closed.");
			} catch (IOException e) {
				Log.d(MainActivity.TAG, "Can not close fos2.");
				e.printStackTrace();
			}
			Log.d(MainActivity.TAG, "Saved 2 in " + file2.getAbsolutePath());
		} else {
			Log.d(MainActivity.TAG, "Cant not save it 2");
			Toast.makeText(getApplicationContext(), "fos2 = null",
					Toast.LENGTH_LONG).show();
		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				Toast.makeText(getApplicationContext(), "The image is saved",
						Toast.LENGTH_LONG).show();
			}
		};
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(runnable);
	}

}
