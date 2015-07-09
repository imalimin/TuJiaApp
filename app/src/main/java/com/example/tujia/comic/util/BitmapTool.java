package com.example.tujia.comic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;

/**
 * 
 */

public class BitmapTool {

	public static final int UNCONSTRAINED = -1;

	public static Options getOptions(String path) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		return options;
	}

	private static Bitmap getBitmapByPath(Context context, String path,
			Options options, int screenWidth, int screenHeight)
			throws FileNotFoundException {
		// File file = new File(path);
		// if (!file.exists()) {
		// throw new FileNotFoundException();
		// }
		InputStream inputStream = null;
		try {
			inputStream = context.getAssets().open(path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// inputStream = new FileInputStream(file);
		if (options != null) {
			Rect r = getScreenRegion(screenWidth, screenHeight);
			// ȡ��ͼƬ�Ŀ�͸�
			int w = r.width();
			int h = r.height();
			int maxSize = w > h ? w : h;
			// �������ű���
			int inSimpleSize = computeSampleSize(options, maxSize, w * h);
			// �������ű���
			options.inSampleSize = inSimpleSize;
			options.inJustDecodeBounds = false;
		}

		// ����ѹ�����ͼƬ
		Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
//		try {
//			inputStream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return bitmap;
	}

	private static Rect getScreenRegion(int width, int height) {
		return new Rect(0, 0, width, height);
	}

	// ��ȡ��Ҫ�������ŵı���options.inSampleSize
	private static int computeSampleSize(Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		return initialSize;
	}

	private static int computeInitialSampleSize(Options options,
			int minSideLength, int maxNumOfPixels) {
		// ���ͼƬ�Ŀ�͸�
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
				.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
				.min(Math.floor(w / minSideLength),
						Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == UNCONSTRAINED)
				&& (minSideLength == UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	// ���ؼ��غ�Ĵ�ͼƬ
	public static Bitmap getBitmap(Context context, String path,
			int screenWidth, int screenHeight) throws FileNotFoundException {
		return BitmapTool.getBitmapByPath(context, path,
				BitmapTool.getOptions(path), screenWidth, screenHeight);
	}

}
