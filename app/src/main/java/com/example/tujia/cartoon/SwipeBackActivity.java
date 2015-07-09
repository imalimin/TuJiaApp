package com.example.tujia.cartoon;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public  abstract class SwipeBackActivity extends Activity {

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inWindow();
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().getDecorView().setBackgroundDrawable(null);
        mSwipeBackLayout = new SwipeBackLayout(this);
    }
	public abstract void inWindow();
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackLayout.attachToActivity(this);
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null)
        	return v;
        return mSwipeBackLayout.findViewById(id);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackLayout.setEnableGesture(enable);
    }

    public void setEdgeFromLeft(){
        final int edgeFlag = SwipeBackLayout.EDGE_LEFT;
        mSwipeBackLayout.setEdgeTrackingEnabled(edgeFlag);
    }
    public void setEdgeFromRight(){
        final int edgeFlag = SwipeBackLayout.EDGE_RIGHT;
        mSwipeBackLayout.setEdgeTrackingEnabled(edgeFlag);
    }
    public void setEdgeFromAll(){
        final int edgeFlag = SwipeBackLayout.EDGE_ALL;
        mSwipeBackLayout.setEdgeTrackingEnabled(edgeFlag);
    }
    public void setEdgeFromBottom(){
        final int edgeFlag = SwipeBackLayout.EDGE_BOTTOM;
        mSwipeBackLayout.setEdgeTrackingEnabled(edgeFlag);
    }
    public void setEdgeFromLeftOrRight(){
        final int edgeFlag = SwipeBackLayout.EDGE_LEFT_EDGE_RIGHT;
        mSwipeBackLayout.setEdgeTrackingEnabled(edgeFlag);
    }
	public void scrollToFinishActivity() {
		mSwipeBackLayout.scrollToFinishActivity();
	}
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  {  
        	scrollToFinishActivity();
        }
		return false;  
    }
    
}
