package com.example.tujia.comic.view;

import java.lang.ref.SoftReference;
import java.util.Map;

import android.graphics.Bitmap;

import com.google.common.collect.Maps;

public class SkinBitmapCacheManager {

  private static class SkinBitmapCacheManagerContainer {

    private static SkinBitmapCacheManager instance = new SkinBitmapCacheManager();
  }

  public static SkinBitmapCacheManager getInstance() {
    return SkinBitmapCacheManagerContainer.instance;
  }

  private SkinBitmapCacheManager() {
  }

  private Map<String, SoftReference<Bitmap>> cachedBitmaps = Maps.newHashMap();

  public void clear() {
    cachedBitmaps.clear();
  }

  public boolean isCached(String key) {
    final SoftReference<Bitmap> sb = cachedBitmaps.get(key);
    if (sb == null) {
      return false;
    }
    final Bitmap b = sb.get();
    if (b == null) {
      cachedBitmaps.remove(key);
    }
    return b != null;
  }

  public Bitmap getBitmap(String key) {
    final SoftReference<Bitmap> sb = cachedBitmaps.get(key);
    if (sb == null) {
      return null;
    }
    final Bitmap b = sb.get();
    if (b == null) {
      cachedBitmaps.remove(key);
    }
    return b;
  }

  public Bitmap putBitmap(String key, Bitmap bitmap) {
    final SoftReference<Bitmap> sb = new SoftReference<Bitmap>(bitmap);
    final SoftReference<Bitmap> old = cachedBitmaps.put(key, sb);
    if (old == null) {
      return null;
    }
    return old.get();
  }

  public void remove(String url) {
    cachedBitmaps.remove(url);
  }
}
