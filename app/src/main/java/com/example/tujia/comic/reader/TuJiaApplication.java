package com.example.tujia.comic.reader;

import android.app.Application;

public class TuJiaApplication extends Application {
	 private static TuJiaApplication instance = null;
	
	@Override
	public void onCreate() {
		this.instance = this;
		super.onCreate();
	}
	
	public static TuJiaApplication getInstance() {
	    return instance;
	  }
}
