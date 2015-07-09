package com.example.tujia.comic.view;

import com.example.tujia.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class MyVerticalSeekBar extends MyProgressBar {

  public static interface OnProgressChangedListener {

    public void onProgressChanged(int progress);
  }

  private OnProgressChangedListener mOnProgressChangedListener = null;

  public void setOnProgressChangedListener(OnProgressChangedListener listener) {
    mOnProgressChangedListener = listener;
  }

  private Drawable mThumb;
  private int mThumbOffset;

  /**
   * On touch, this offset plus the scaled value from the position of the touch will form the
   * progress value. Usually 0.
   */
  float mTouchProgressOffset;

  /**
   * Whether this is user seekable.
   */
  boolean mIsUserSeekable = true;

  /**
   * On key presses (right or left), the amount to increment/decrement the progress.
   */
  private int mKeyProgressIncrement = 1;

  private static final int NO_ALPHA = 0xFF;
  private float mDisabledAlpha;

  public MyVerticalSeekBar(Context context) {
    this(context, null);
  }

  public MyVerticalSeekBar(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public MyVerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    final Drawable thumb = getResources().getDrawable(R.drawable.brightness_thumb);
    setThumb(thumb);
  }

  /**
   * Sets the thumb that will be drawn at the end of the progress meter within the SeekBar.
   * <p>
   * If the thumb is a valid drawable (i.e. not null), half its width will be used as the new thumb
   * offset (@see #setThumbOffset(int)).
   *
   * @param thumb Drawable representing the thumb
   */
  public void setThumb(Drawable thumb) {
    if (thumb != null) {
      thumb.setCallback(this);
      mThumbOffset = 0;
    }
    mThumb = thumb;
    invalidate();
  }

  /**
   * @see #setThumbOffset(int)
   */
  public int getThumbOffset() {
    return mThumbOffset;
  }

  /**
   * Sets the thumb offset that allows the thumb to extend out of the range of the track.
   *
   * @param thumbOffset The offset amount in pixels.
   */
  public void setThumbOffset(int thumbOffset) {
    mThumbOffset = thumbOffset;
    invalidate();
  }

  /**
   * Sets the amount of progress changed via the arrow keys.
   *
   * @param increment The amount to increment or decrement when the user presses the arrow keys.
   */
  public void setKeyProgressIncrement(int increment) {
    mKeyProgressIncrement = increment < 0 ? -increment : increment;
  }

  /**
   * Returns the amount of progress changed via the arrow keys.
   * <p>
   * By default, this will be a value that is derived from the max progress.
   *
   * @return The amount to increment or decrement when the user presses the arrow keys. This will be
   *         positive.
   */
  public int getKeyProgressIncrement() {
    return mKeyProgressIncrement;
  }

  @Override
  public synchronized void setMax(int max) {
    super.setMax(max);

    if ((mKeyProgressIncrement == 0) || (getMax() / mKeyProgressIncrement > 20)) {
      // It will take the user too long to change this via keys, change it
      // to something more reasonable
      setKeyProgressIncrement(Math.max(1, Math.round((float) getMax() / 20)));
    }
  }

  @Override
  protected boolean verifyDrawable(Drawable who) {
    return who == mThumb || super.verifyDrawable(who);
  }

  @Override
  protected void drawableStateChanged() {
    super.drawableStateChanged();

    final Drawable progressDrawable = getProgressDrawable();
    if (progressDrawable != null) {
      progressDrawable.setAlpha(isEnabled() ? NO_ALPHA : (int) (NO_ALPHA * mDisabledAlpha));
    }

    if (mThumb != null && mThumb.isStateful()) {
      final int[] state = getDrawableState();
      mThumb.setState(state);
    }
  }

  @Override
  void onProgressRefresh(float scale, boolean fromUser) {
    final Drawable thumb = mThumb;
    if (thumb != null) {
      setThumbPos(getHeight(), thumb, scale, Integer.MIN_VALUE);
      /*
       * Since we draw translated, the drawable's bounds that it signals for invalidation won't be
       * the actual bounds we want invalidated, so just invalidate this whole view.
       */
      invalidate();
    }

    if (fromUser) {
      if (mOnProgressChangedListener != null) {
        mOnProgressChangedListener.onProgressChanged(getProgress());
      }
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    final Drawable d = getCurrentDrawable();
    final Drawable thumb = mThumb;
    final int thumbWidth = thumb == null ? 0 : thumb.getIntrinsicWidth();
    // The max height does not incorporate padding, whereas the height
    // parameter does
    final int trackWidth = d == null ? 0 : d.getIntrinsicWidth();

    final int max = getMax();
    final float scale = max > 0 ? (float) getProgress() / (float) max : 0;

    if (thumb != null) {
      setThumbPos(h, thumb, scale, 0);
    }

    final int gapForCenteringTrack = (thumbWidth - trackWidth) / 2;
    if (d != null) {
      // 10 extra gap in bottom
      d.setBounds(gapForCenteringTrack, 0, w - getPaddingRight() - getPaddingLeft()
          - gapForCenteringTrack, h - getPaddingBottom() - getPaddingTop() - 10);
    }
  }

  /**
   * @param gap If set to {@link Integer#MIN_VALUE}, this will be ignored and
   */
  private void setThumbPos(int h, Drawable thumb, float scale, int gap) {
    int available = h - getPaddingTop() - getPaddingBottom();
    final int thumbWidth = thumb.getIntrinsicWidth();
    final int thumbHeight = thumb.getIntrinsicHeight();
    available -= thumbHeight;

    final int thumbPos = (int) (scale * available);

    int leftBound, rightBound;
    if (gap == Integer.MIN_VALUE) {
      final Rect oldBounds = thumb.getBounds();
      leftBound = oldBounds.left;
      rightBound = oldBounds.right;
    } else {
      leftBound = gap;
      rightBound = gap + thumbWidth;
    }

    // Canvas will be translated, so 0,0 is where we start drawing
    thumb.setBounds(leftBound, thumbPos, rightBound, thumbPos + thumbHeight);
  }

  @Override
  protected synchronized void onDraw(Canvas canvas) {
    // do not call super
    if (mThumb != null) {
      canvas.save();
      canvas.translate(getPaddingLeft(), getPaddingTop());
      mThumb.draw(canvas);
      canvas.restore();
    }
  }

  @Override
  protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final Drawable d = getProgressDrawable();

    final int thumbWidth = mThumb == null ? 0 : mThumb.getIntrinsicWidth();
    int dw = 0;
    int dh = 0;
    if (d != null) {
      dw = Math.max(16, Math.min(48, d.getIntrinsicWidth()));
      dh = Math.max(16, Math.min(48, d.getIntrinsicHeight()));
      dw = Math.max(thumbWidth, dh);
    }
    dw += getPaddingLeft() + getPaddingRight();
    dh += getPaddingTop() + getPaddingBottom();

    setMeasuredDimension(resolveSize(dw, widthMeasureSpec), resolveSize(dh, heightMeasureSpec));
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!mIsUserSeekable || !isEnabled()) {
      return false;
    }

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        setPressed(true);
        onStartTrackingTouch();
        trackTouchEvent(event);
        break;

      case MotionEvent.ACTION_MOVE:
        trackTouchEvent(event);
        attemptClaimDrag();
        break;

      case MotionEvent.ACTION_UP:
        trackTouchEvent(event);
        onStopTrackingTouch();
        setPressed(false);
        // ProgressBar doesn't know to repaint the thumb drawable
        // in its inactive state when the touch stops (because the
        // value has not apparently changed)
        invalidate();
        break;

      case MotionEvent.ACTION_CANCEL:
        onStopTrackingTouch();
        setPressed(false);
        invalidate(); // see above explanation
        break;
    }
    return true;
  }

  private void trackTouchEvent(MotionEvent event) {
    final int height = getHeight();
    final int available = height - getPaddingTop() - getPaddingBottom();
    final int y = (int) event.getY();
    float scale;
    float progress = 0;
    if (y < getPaddingTop()) {
      scale = 0.0f;
    } else if (y > height - getPaddingBottom()) {
      scale = 1.0f;
    } else {
      scale = (float) (y - getPaddingTop()) / (float) available;
      progress = mTouchProgressOffset;
    }

    final int max = getMax();
    progress += scale * max;

    setProgress((int) progress, true);
  }

  /**
   * Tries to claim the user's drag motion, and requests disallowing any ancestors from stealing
   * events in the drag.
   */
  private void attemptClaimDrag() {
    if (getParent() != null) {
      getParent().requestDisallowInterceptTouchEvent(true);
    }
  }

  /**
   * This is called when the user has started touching this widget.
   */
  void onStartTrackingTouch() {
  }

  /**
   * This is called when the user either releases his touch or the touch is canceled.
   */
  void onStopTrackingTouch() {
  }

  /**
   * Called when the user changes the seekbar's progress by using a key event.
   */
  void onKeyChange() {
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    final int progress = getProgress();

    switch (keyCode) {
      case KeyEvent.KEYCODE_DPAD_UP:
        if (progress <= 0)
          break;
        setProgress(progress - mKeyProgressIncrement, true);
        onKeyChange();
        return true;

      case KeyEvent.KEYCODE_DPAD_DOWN:
        if (progress >= getMax())
          break;
        setProgress(progress + mKeyProgressIncrement, true);
        onKeyChange();
        return true;
    }

    return super.onKeyDown(keyCode, event);
  }
}
