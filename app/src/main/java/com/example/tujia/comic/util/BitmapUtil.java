package com.example.tujia.comic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtil {

  private final static int BITMAP_SIZE_LIMIT = 600 * 480;

  public static boolean save(Bitmap bitmap, String path) {
    if (bitmap == null || path == null) {
      return false;
    }

    if (!FileUtil.isSDCardReady() || FileUtil.getSDCardFreeSize() < 10 * 1024) {
      return false;
    }

    final int indexFolder = path.lastIndexOf("/");
    if (indexFolder > 0) {
      final String folder = path.substring(0, indexFolder);
      final File folderFile = new File(folder);
      folderFile.mkdirs();
    }

    final File file = new File(path);
    if (file.exists()) {
      final boolean deleted = file.delete();
      if (!deleted) {
        return false;
      }
    }

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      final boolean hasAlpha = bitmap.hasAlpha();
      return bitmap.compress(hasAlpha ? CompressFormat.PNG : CompressFormat.JPEG, 100, fos);
    } catch (final Exception e) {
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (final IOException e) {
        }
        fos = null;
      }
    }

    return false;
  }

//  public static ComicImage loadComicImage(String path) throws OutOfMemoryError {
//    return loadComicImage(path, BITMAP_SIZE_LIMIT, null/* bitmapConfig */, false/* encrypted */);
//  }
//
//  public static ComicImage loadComicImage(String path, int bitmapSizeLimit,
//      Bitmap.Config bitmapConfig, boolean encrypted) throws OutOfMemoryError {
//    if (path == null) {
//      return null;
//    }
//
//    final File file = new File(path);
//    if (!file.exists()) {
//      return null;
//    }
//    //start patch for 暂时无法查看
//    if (file.length() == 5093 ) {
//      file.delete();
//      return null;
//    }
//    //end
//    InputStream is1 = null;
//    InputStream is2 = null;
//    try {
//      is1 = new FileInputStream(path);
//      final BitmapFactory.Options ops = new BitmapFactory.Options();
//      ops.inJustDecodeBounds = true;
//      final byte[] confusedBuffer = new byte[SimpleEncrypter.CONFUSE_DATA_SIZE];
//      if (encrypted) {
//        is1.read(confusedBuffer);
//      }
//      BitmapFactory.decodeStream(is1, null, ops);
//      try {
//        is1.close();
//      } catch (final Exception e) {
//        logUtil.e("loadFile error:" + e);
//      }
//      is1 = null;
//
//      if (ops.outWidth > 0 && ops.outHeight > 0) {
//        ops.inJustDecodeBounds = false;
//        ops.inSampleSize = 1;
//        int size = ops.outWidth * ops.outHeight;
//        while (size > bitmapSizeLimit) {
//          ops.inSampleSize <<= 1;
//          size >>= 2;
//        }
//
//        if (bitmapConfig != null) {
//          ops.inPreferredConfig = bitmapConfig;
//        }
//
//        is2 = new FileInputStream(path);
//        if (encrypted) {
//          is2.read(confusedBuffer);
//        }
//        final Bitmap bitmap = BitmapFactory.decodeStream(is2, null, ops);
//        final ComicImage image = new ComicImage();
//        image.bitmap = bitmap;
//        image.inSampleSize = ops.inSampleSize;
//        return image;
//      }
//    } catch (final Exception e) {
//    } finally {
//      if (is1 != null) {
//        try {
//          is1.close();
//        } catch (final IOException e) {
//        }
//        is1 = null;
//      }
//      if (is2 != null) {
//        try {
//          is2.close();
//        } catch (final IOException e) {
//        }
//        is2 = null;
//      }
//    }
//
//    return null;
//  }

//  public static Bitmap loadUri(Uri imageUri) throws OutOfMemoryError {
//    if (imageUri == null) {
//      return null;
//    }
//
//    final ContentResolver cr = ComicApplication.getInstance().getContentResolver();
//
//    InputStream is1 = null;
//    InputStream is2 = null;
//    try {
//      is1 = cr.openInputStream(imageUri);
//
//      final BitmapFactory.Options ops = new BitmapFactory.Options();
//      ops.inJustDecodeBounds = true;
//      BitmapFactory.decodeStream(is1, null, ops);
//      try {
//        is1.close();
//      } catch (final IOException e) {
//      }
//      is1 = null;
//
//      if (ops.outWidth > 0 && ops.outHeight > 0) {
//        ops.inJustDecodeBounds = false;
//        ops.inSampleSize = 1;
//        int size = ops.outWidth * ops.outHeight;
//        while (size > BITMAP_SIZE_LIMIT) {
//          ops.inSampleSize <<= 1;
//          size >>= 2;
//        }
//
//        is2 = cr.openInputStream(imageUri);
//        return BitmapFactory.decodeStream(is2, null, ops);
//      }
//    } catch (final Exception e) {
//    } finally {
//      if (is1 != null) {
//        try {
//          is1.close();
//        } catch (final IOException e) {
//        }
//        is1 = null;
//      }
//      if (is2 != null) {
//        try {
//          is2.close();
//        } catch (final IOException e) {
//        }
//        is2 = null;
//      }
//    }
//
//    return null;
//  }

  public static Bitmap loadFileFast(String path) throws OutOfMemoryError {
    if (path == null) {
      return null;
    }

    InputStream is = null;
    try {
      is = new FileInputStream(path);
      return BitmapFactory.decodeStream(is, null, null);
    } catch (final Exception e) {
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (final IOException e) {
        }
        is = null;
      }
    }

    return null;
  }

  public static Bitmap scale(Bitmap bitmap, float ratio) {
    if (bitmap == null || ratio <= 0.0f) {
      return null;
    }

    final int width = bitmap.getWidth();
    final int height = bitmap.getHeight();

    final Matrix matrix = new Matrix();
    matrix.setScale(ratio, ratio);
    final Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

    return result;
  }

  public static Bitmap crop(Bitmap bitmap) {
    if (bitmap == null) {
      return null;
    }

    final int height = bitmap.getHeight();
    final int width = bitmap.getWidth();
    if (height == width) {
      return bitmap;
    }

    final int cropSize = Math.min(width, height);
    final Bitmap result =
        Bitmap.createBitmap(bitmap, (width - cropSize) >> 1, (height - cropSize) >> 1, cropSize,
            cropSize);

    return result;
  }
  
  public static Drawable bitmap2Drawable(Bitmap bitmap){  
    return new BitmapDrawable(bitmap) ;  
  }
  
  /**
   * 转换图片成圆形
   * 
   * @param bitmap 传入Bitmap对象
   * @return
   */
  public static Bitmap toRoundBitmap(Bitmap bitmap) {
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    float roundPx;
    float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
    if (width <= height) {
      roundPx = width / 2;
      top = 0;
      bottom = width;
      left = 0;
      right = width;
      height = width;
      dst_left = 0;
      dst_top = 0;
      dst_right = width;
      dst_bottom = width;
    } else {
      roundPx = height / 2;
      float clip = (width - height) / 2;
      left = clip;
      right = width - clip;
      top = 0;
      bottom = height;
      width = height;
      dst_left = 0;
      dst_top = 0;
      dst_right = height;
      dst_bottom = height;
    }

    Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
    final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
    final RectF rectF = new RectF(dst);

    paint.setAntiAlias(true);

    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    canvas.drawBitmap(bitmap, src, dst, paint);
    return output;
  }

  public static Bitmap getHttpBitmap(String url) {
    URL myFileURL;
    Bitmap bitmap = null;
    try {
      myFileURL = new URL(url);
      HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();

      // 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制

      conn.setConnectTimeout(6000);

      // 连接设置获得数据流

      conn.setDoInput(true);

      // 不使用缓存

      conn.setUseCaches(false);

      // 这句可有可无，没有影响

      // conn.connect();

      InputStream is = conn.getInputStream();
      bitmap = BitmapFactory.decodeStream(is);

      // 关闭数据流

      is.close();

    } catch (Exception e) {

      e.printStackTrace();

    }

    return bitmap;

  }

}
