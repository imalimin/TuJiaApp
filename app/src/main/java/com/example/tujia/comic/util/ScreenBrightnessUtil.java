package com.example.tujia.comic.util;


public class ScreenBrightnessUtil {

  private final static String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
  private final static int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
  private final static int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;

//  public static boolean isAutoBrightness() {
//    boolean isAutoBrightness = false;
//
//    try {
//      isAutoBrightness =
//          Settings.System.getInt(ComicApplication.getInstance().getContentResolver(),
//              SCREEN_BRIGHTNESS_MODE) == SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
//    } catch (final Exception e) {
//      e.printStackTrace();
//    }
//
//    return isAutoBrightness;
//  }
//
//  public static int getScreenBrightness() {
//    int brightness = 200;
//
//    try {
//      brightness =
//          Settings.System.getInt(ComicApplication.getInstance().getContentResolver(),
//              Settings.System.SCREEN_BRIGHTNESS);
//    } catch (final Exception e) {
//      e.printStackTrace();
//    }
//
//    return brightness;
//  }
//
//  public static void setBrightness(Activity activity, int brightness) {
//    try {
//      final WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
//      lp.screenBrightness = Float.valueOf(brightness) / 255.0f;
//      activity.getWindow().setAttributes(lp);
//    } catch (final Exception e) {
//      e.printStackTrace();
//    }
//  }
//
//  public static void disableAutoBrightness() {
//    try {
//      Settings.System.putInt(ComicApplication.getInstance().getContentResolver(),
//          SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);
//    } catch (final Exception e) {
//      e.printStackTrace();
//    }
//  }
//
//  public static void enableAutoBrightness() {
//    try {
//      Settings.System.putInt(ComicApplication.getInstance().getContentResolver(),
//          SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
//    } catch (final Exception e) {
//      e.printStackTrace();
//    }
//  }
//
//  public static void saveBrightness(int brightness) {
//    try {
//      final Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
//      Settings.System.putInt(ComicApplication.getInstance().getContentResolver(),
//          Settings.System.SCREEN_BRIGHTNESS, brightness);
//      ComicApplication.getInstance().getContentResolver().notifyChange(uri, null);
//    } catch (final Exception e) {
//      e.printStackTrace();
//    }
//  }
}
