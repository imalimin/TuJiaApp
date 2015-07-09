/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tujia.comic.view;

import com.example.tujia.R;
import com.example.tujia.comic.reader.TuJiaApplication;
import com.example.tujia.comic.util.StringUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.RemoteViews.RemoteView;

/**
 * <p>
 * Visual indicator of progress in some operation. Displays a bar to the user
 * representing how far the operation has progressed; the application can change
 * the amount of progress (modifying the length of the bar) as it moves forward.
 * There is also a secondary progress displayable on a progress bar which is
 * useful for displaying intermediate progress, such as the buffer level during
 * a streaming playback progress bar.
 * </p>
 * 
 * <p>
 * A progress bar can also be made indeterminate. In indeterminate mode, the
 * progress bar shows a cyclic animation. This mode is used by applications when
 * the length of the task is unknown.
 * </p>
 * 
 * <p>
 * The following code example shows how a progress bar can be used from a worker
 * thread to update the user interface to notify the user of progress:
 * </p>
 * 
 * <pre class="prettyprint">
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * public class MyActivity extends Activity {
 * 
 * 	private static final int PROGRESS = 0x1;
 * 
 * 	private ProgressBar mProgress;
 * 	private int mProgressStatus = 0;
 * 
 * 	private Handler mHandler = new Handler();
 * 
 * 	protected void onCreate(Bundle icicle) {
 * 		super.onCreate(icicle);
 * 
 * 		setContentView(R.layout.progressbar_activity);
 * 
 * 		mProgress = (ProgressBar) findViewById(R.id.progress_bar);
 * 
 * 		// Start lengthy operation in a background thread
 * 		new Thread(new Runnable() {
 * 
 * 			public void run() {
 * 				while (mProgressStatus &lt; 100) {
 * 					mProgressStatus = doWork();
 * 
 * 					// Update the progress bar
 * 					mHandler.post(new Runnable() {
 * 
 * 						public void run() {
 * 							mProgress.setProgress(mProgressStatus);
 * 						}
 * 					});
 * 				}
 * 			}
 * 		}).start();
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * <strong>XML attributes</b></strong>
 * <p>
 * See {@link android.R.styleable#ProgressBar ProgressBar Attributes},
 * {@link android.R.styleable#View View Attributes}
 * </p>
 * 
 * <p>
 * <strong>Styles</b></strong>
 * <p>
 * 
 * @attr ref android.R.styleable#Theme_progressBarStyle
 * @attr ref android.R.styleable#Theme_progressBarStyleSmall
 * @attr ref android.R.styleable#Theme_progressBarStyleLarge
 * @attr ref android.R.styleable#Theme_progressBarStyleHorizontal
 *       </p>
 */
@RemoteView
public class MyProgressBar extends View {

	private static final int MAX_LEVEL = 10000;
	private static final int ANIMATION_RESOLUTION = 200;

	int mMinWidth;
	int mMaxWidth;
	int mMinHeight;
	int mMaxHeight;

	private int mProgress;
	private int mSecondaryProgress;
	private int mMax;

	private int mBehavior;
	private int mDuration;
	private boolean mIndeterminate;
	private boolean mOnlyIndeterminate;
	private Transformation mTransformation;
	private AlphaAnimation mAnimation;
	private Drawable mIndeterminateDrawable;
	private Drawable mProgressDrawable;
	private Drawable mCurrentDrawable;
	Bitmap mSampleTile;
	private boolean mNoInvalidate;
	private Interpolator mInterpolator;
	private RefreshProgressRunnable mRefreshProgressRunnable;
	private long mUiThreadId;
	private boolean mShouldStartAnimationDrawable;
	private long mLastDrawTime;

	private boolean mInDrawing;
	private final static String NINE_PATCH_FILE_SUFFIX = ".9.png";

	/**
	 * Create a new progress bar with range 0...100 and initial progress of 0.
	 * 
	 * @param context
	 *            the application environment
	 */
	public MyProgressBar(Context context) {
		this(context, null);
	}

	public MyProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mUiThreadId = Thread.currentThread().getId();
		initProgressBar();

		mNoInvalidate = true;

		// TODO
		Drawable drawable = null;
//				getDrawAbleByResName(getResources().getString(
//				R.string.reading_progress));
		if (drawable != null) {
			drawable = tileify(drawable, false);
			setProgressDrawable(drawable);
		}

		mDuration = 500;

		mMinWidth = 16;
		mMaxWidth = 48;
		mMinHeight = 16;
		mMaxHeight = 48;

		mBehavior = 0;

		setMax(100);

		setProgress(0);

		setSecondaryProgress(0);

		mOnlyIndeterminate = false;

		mNoInvalidate = false;

		setIndeterminate(false);
	}

//	public Drawable getDrawAbleByResName(String oldName) {
//		String appPackageName = TuJiaApplication.getInstance().getPackageName();
//		// String filePath =
//		// ComicApplication.getInstance().getFilesDir().getPath();
//		String skinResourceFolder = "/data/data/" + appPackageName + "/skin/";
//
//		if (StringUtil.isNullOrEmpty(oldName)) {
//			return null;
//		}
//
//		final String name = TuJiaApplication.getInstance().getString(
//				R.string.default_nodownload_xhdpi);
//
//		final SkinBitmapCacheManager bcm = SkinBitmapCacheManager.getInstance();
//		Bitmap bitmap = bcm.getBitmap(name);
//		if (bitmap == null) {
//			bitmap = BitmapFactory.decodeFile(skinResourceFolder + name);
//			if (bitmap == null) {
//				return null;
//			}
//			bcm.putBitmap(name, bitmap);
//		}
//
//		if (name.endsWith(NINE_PATCH_FILE_SUFFIX)) {
//			final byte[] chunk = bitmap.getNinePatchChunk();
//			if (NinePatch.isNinePatchChunk(chunk)) {
//				return new NinePatchDrawable(TuJiaApplication.getInstance()
//						.getResources(), bitmap, chunk, new Rect(), null/* srcName */);
//			} else {
//				return new BitmapDrawable(bitmap);
//			}
//		} else {
//			return new BitmapDrawable(TuJiaApplication.getInstance()
//					.getResources(), bitmap);
//		}
//	}

	/**
	 * Converts a drawable to a tiled version of itself. It will recursively
	 * traverse layer and state list drawables.
	 */
	private Drawable tileify(Drawable drawable, boolean clip) {

		if (drawable instanceof LayerDrawable) {
			final LayerDrawable background = (LayerDrawable) drawable;
			final int N = background.getNumberOfLayers();
			final Drawable[] outDrawables = new Drawable[N];

			for (int i = 0; i < N; i++) {
				outDrawables[i] = tileify(background.getDrawable(i), false);
			}

			final LayerDrawable newBg = new LayerDrawable(outDrawables);

			for (int i = 0; i < N; i++) {
				newBg.setId(i, background.getId(i));
			}

			return newBg;

		} else if (drawable instanceof StateListDrawable) {
			// StateListDrawable in = (StateListDrawable) drawable;
			// StateListDrawable out = new StateListDrawable();
			// int numStates = in.getStateCount();
			// for (int i = 0; i < numStates; i++) {
			// out.addState(in.getStateSet(i), tileify(in.getStateDrawable(i),
			// clip));
			// }
			// return out;

		} else if (drawable instanceof BitmapDrawable) {
			final Bitmap tileBitmap = ((BitmapDrawable) drawable).getBitmap();
			if (mSampleTile == null) {
				mSampleTile = tileBitmap;
			}

			final ShapeDrawable shapeDrawable = new ShapeDrawable(
					getDrawableShape());

			final BitmapShader bitmapShader = new BitmapShader(tileBitmap,
					Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
			shapeDrawable.getPaint().setShader(bitmapShader);

			return (clip) ? new ClipDrawable(shapeDrawable, Gravity.LEFT,
					ClipDrawable.HORIZONTAL) : shapeDrawable;
		} else if (drawable instanceof NinePatchDrawable) {
			return (clip) ? new ClipDrawable(drawable, Gravity.LEFT,
					ClipDrawable.HORIZONTAL) : drawable;
		}

		return drawable;
	}

	Shape getDrawableShape() {
		final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
		return new RoundRectShape(roundedCorners, null, null);
	}

	/**
	 * <p>
	 * Initialize the progress bar's default values:
	 * </p>
	 * <ul>
	 * <li>progress = 0</li>
	 * <li>max = 100</li>
	 * <li>animation duration = 4000 ms</li>
	 * <li>indeterminate = false</li>
	 * <li>behavior = repeat</li>
	 * </ul>
	 */
	private void initProgressBar() {
		mMax = 100;
		mProgress = 0;
		mSecondaryProgress = 0;
		mIndeterminate = false;
		mOnlyIndeterminate = false;
		mDuration = 4000;
		mBehavior = AlphaAnimation.RESTART;
		mMinWidth = 24;
		mMaxWidth = 48;
		mMinHeight = 24;
		mMaxHeight = 48;
	}

	/**
	 * <p>
	 * Indicate whether this progress bar is in indeterminate mode.
	 * </p>
	 * 
	 * @return true if the progress bar is in indeterminate mode
	 */
	public synchronized boolean isIndeterminate() {
		return mIndeterminate;
	}

	/**
	 * <p>
	 * Change the indeterminate mode for this progress bar. In indeterminate
	 * mode, the progress is ignored and the progress bar shows an infinite
	 * animation instead.
	 * </p>
	 * 
	 * If this progress bar's style only supports indeterminate mode (such as
	 * the circular progress bars), then this will be ignored.
	 * 
	 * @param indeterminate
	 *            true to enable the indeterminate mode
	 */
	public synchronized void setIndeterminate(boolean indeterminate) {
		if ((!mOnlyIndeterminate || !mIndeterminate)
				&& indeterminate != mIndeterminate) {
			mIndeterminate = indeterminate;

			if (indeterminate) {
				// swap between indeterminate and regular backgrounds
				mCurrentDrawable = mIndeterminateDrawable;
				startAnimation();
			} else {
				mCurrentDrawable = mProgressDrawable;
				stopAnimation();
			}
		}
	}

	/**
	 * <p>
	 * Get the drawable used to draw the progress bar in indeterminate mode.
	 * </p>
	 * 
	 * @return a {@link android.graphics.drawable.Drawable} instance
	 * 
	 * @see #setIndeterminateDrawable(android.graphics.drawable.Drawable)
	 * @see #setIndeterminate(boolean)
	 */
	public Drawable getIndeterminateDrawable() {
		return mIndeterminateDrawable;
	}

	/**
	 * <p>
	 * Define the drawable used to draw the progress bar in indeterminate mode.
	 * </p>
	 * 
	 * @param d
	 *            the new drawable
	 * 
	 * @see #getIndeterminateDrawable()
	 * @see #setIndeterminate(boolean)
	 */
	public void setIndeterminateDrawable(Drawable d) {
		if (d != null) {
			d.setCallback(this);
		}
		mIndeterminateDrawable = d;
		if (mIndeterminate) {
			mCurrentDrawable = d;
			postInvalidate();
		}
	}

	/**
	 * <p>
	 * Get the drawable used to draw the progress bar in progress mode.
	 * </p>
	 * 
	 * @return a {@link android.graphics.drawable.Drawable} instance
	 * 
	 * @see #setProgressDrawable(android.graphics.drawable.Drawable)
	 * @see #setIndeterminate(boolean)
	 */
	public Drawable getProgressDrawable() {
		return mProgressDrawable;
	}

	/**
	 * <p>
	 * Define the drawable used to draw the progress bar in progress mode.
	 * </p>
	 * 
	 * @param d
	 *            the new drawable
	 * 
	 * @see #getProgressDrawable()
	 * @see #setIndeterminate(boolean)
	 */
	public void setProgressDrawable(Drawable d) {
		if (d != null) {
			d.setCallback(this);
		}
		mProgressDrawable = d;
		if (!mIndeterminate) {
			mCurrentDrawable = d;
			postInvalidate();
		}
	}

	/**
	 * @return The drawable currently used to draw the progress bar
	 */
	Drawable getCurrentDrawable() {
		return mCurrentDrawable;
	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		return who == mProgressDrawable || who == mIndeterminateDrawable
				|| super.verifyDrawable(who);
	}

	@Override
	public void postInvalidate() {
		if (!mNoInvalidate) {
			super.postInvalidate();
		}
	}

	private class RefreshProgressRunnable implements Runnable {

		private int mId;
		private int mProgress;
		private boolean mFromUser;

		RefreshProgressRunnable(int id, int progress, boolean fromUser) {
			mId = id;
			mProgress = progress;
			mFromUser = fromUser;
		}

		@Override
		public void run() {
			doRefreshProgress(mId, mProgress, mFromUser);
			// Put ourselves back in the cache when we are done
			mRefreshProgressRunnable = this;
		}

		public void setup(int id, int progress, boolean fromUser) {
			mId = id;
			mProgress = progress;
			mFromUser = fromUser;
		}

	}

	private synchronized void doRefreshProgress(int id, int progress,
			boolean fromUser) {
		final float scale = mMax > 0 ? (float) progress / (float) mMax : 0;
		invalidate();

		onProgressRefresh(scale, fromUser);
	}

	void onProgressRefresh(float scale, boolean fromUser) {
	}

	private synchronized void refreshProgress(int id, int progress,
			boolean fromUser) {
		if (mUiThreadId == Thread.currentThread().getId()) {
			doRefreshProgress(id, progress, fromUser);
		} else {
			RefreshProgressRunnable r;
			if (mRefreshProgressRunnable != null) {
				// Use cached RefreshProgressRunnable if available
				r = mRefreshProgressRunnable;
				// Uncache it
				mRefreshProgressRunnable = null;
				r.setup(id, progress, fromUser);
			} else {
				// Make a new one
				r = new RefreshProgressRunnable(id, progress, fromUser);
			}
			post(r);
		}
	}

	/**
	 * <p>
	 * Set the current progress to the specified value. Does not do anything if
	 * the progress bar is in indeterminate mode.
	 * </p>
	 * 
	 * @param progress
	 *            the new progress, between 0 and {@link #getMax()}
	 * 
	 * @see #setIndeterminate(boolean)
	 * @see #isIndeterminate()
	 * @see #getProgress()
	 * @see #incrementProgressBy(int)
	 */
	public synchronized void setProgress(int progress) {
		setProgress(progress, false);
	}

	synchronized void setProgress(int progress, boolean fromUser) {
		if (mIndeterminate) {
			return;
		}

		if (progress < 0) {
			progress = 0;
		}

		if (progress > mMax) {
			progress = mMax;
		}

		if (progress != mProgress) {
			mProgress = progress;
			refreshProgress(0, mProgress, fromUser);
		}
	}

	/**
	 * <p>
	 * Set the current secondary progress to the specified value. Does not do
	 * anything if the progress bar is in indeterminate mode.
	 * </p>
	 * 
	 * @param secondaryProgress
	 *            the new secondary progress, between 0 and {@link #getMax()}
	 * @see #setIndeterminate(boolean)
	 * @see #isIndeterminate()
	 * @see #getSecondaryProgress()
	 * @see #incrementSecondaryProgressBy(int)
	 */
	public synchronized void setSecondaryProgress(int secondaryProgress) {
		if (mIndeterminate) {
			return;
		}

		if (secondaryProgress < 0) {
			secondaryProgress = 0;
		}

		if (secondaryProgress > mMax) {
			secondaryProgress = mMax;
		}

		if (secondaryProgress != mSecondaryProgress) {
			mSecondaryProgress = secondaryProgress;
			// refreshProgress(R.id.secondaryProgress, mSecondaryProgress,
			// false);
		}
	}

	/**
	 * <p>
	 * Get the progress bar's current level of progress. Return 0 when the
	 * progress bar is in indeterminate mode.
	 * </p>
	 * 
	 * @return the current progress, between 0 and {@link #getMax()}
	 * 
	 * @see #setIndeterminate(boolean)
	 * @see #isIndeterminate()
	 * @see #setProgress(int)
	 * @see #setMax(int)
	 * @see #getMax()
	 */
	public synchronized int getProgress() {
		return mIndeterminate ? 0 : mProgress;
	}

	/**
	 * <p>
	 * Get the progress bar's current level of secondary progress. Return 0 when
	 * the progress bar is in indeterminate mode.
	 * </p>
	 * 
	 * @return the current secondary progress, between 0 and {@link #getMax()}
	 * 
	 * @see #setIndeterminate(boolean)
	 * @see #isIndeterminate()
	 * @see #setSecondaryProgress(int)
	 * @see #setMax(int)
	 * @see #getMax()
	 */
	public synchronized int getSecondaryProgress() {
		return mIndeterminate ? 0 : mSecondaryProgress;
	}

	/**
	 * <p>
	 * Return the upper limit of this progress bar's range.
	 * </p>
	 * 
	 * @return a positive integer
	 * 
	 * @see #setMax(int)
	 * @see #getProgress()
	 * @see #getSecondaryProgress()
	 */
	public synchronized int getMax() {
		return mMax;
	}

	/**
	 * <p>
	 * Set the range of the progress bar to 0...<tt>max</tt>.
	 * </p>
	 * 
	 * @param max
	 *            the upper range of this progress bar
	 * 
	 * @see #getMax()
	 * @see #setProgress(int)
	 * @see #setSecondaryProgress(int)
	 */
	public synchronized void setMax(int max) {
		if (max < 0) {
			max = 0;
		}
		if (max != mMax) {
			mMax = max;
			postInvalidate();

			if (mProgress > max) {
				mProgress = max;
				refreshProgress(0, mProgress, false);
			}
		}
	}

	/**
	 * <p>
	 * Increase the progress bar's progress by the specified amount.
	 * </p>
	 * 
	 * @param diff
	 *            the amount by which the progress must be increased
	 * 
	 * @see #setProgress(int)
	 */
	public synchronized final void incrementProgressBy(int diff) {
		setProgress(mProgress + diff);
	}

	/**
	 * <p>
	 * Increase the progress bar's secondary progress by the specified amount.
	 * </p>
	 * 
	 * @param diff
	 *            the amount by which the secondary progress must be increased
	 * 
	 * @see #setSecondaryProgress(int)
	 */
	public synchronized final void incrementSecondaryProgressBy(int diff) {
		setSecondaryProgress(mSecondaryProgress + diff);
	}

	/**
	 * <p>
	 * Start the indeterminate progress animation.
	 * </p>
	 */
	void startAnimation() {
		final int visibility = getVisibility();
		if (visibility != VISIBLE) {
			return;
		}

		if (mIndeterminateDrawable instanceof Animatable) {
			mShouldStartAnimationDrawable = true;
			mAnimation = null;
		} else {
			if (mInterpolator == null) {
				mInterpolator = new LinearInterpolator();
			}

			mTransformation = new Transformation();
			mAnimation = new AlphaAnimation(0.0f, 1.0f);
			mAnimation.setRepeatMode(mBehavior);
			mAnimation.setRepeatCount(Animation.INFINITE);
			mAnimation.setDuration(mDuration);
			mAnimation.setInterpolator(mInterpolator);
			mAnimation.setStartTime(Animation.START_ON_FIRST_FRAME);
			postInvalidate();
		}
	}

	/**
	 * <p>
	 * Stop the indeterminate progress animation.
	 * </p>
	 */
	void stopAnimation() {
		mAnimation = null;
		mTransformation = null;
		if (mIndeterminateDrawable instanceof Animatable) {
			((Animatable) mIndeterminateDrawable).stop();
			mShouldStartAnimationDrawable = false;
		}
	}

	/**
	 * Sets the acceleration curve for the indeterminate animation. The
	 * interpolator is loaded as a resource from the specified context.
	 * 
	 * @param context
	 *            The application environment
	 * @param resID
	 *            The resource identifier of the interpolator to load
	 */
	public void setInterpolator(Context context, int resID) {
		setInterpolator(AnimationUtils.loadInterpolator(context, resID));
	}

	/**
	 * Sets the acceleration curve for the indeterminate animation. Defaults to
	 * a linear interpolation.
	 * 
	 * @param interpolator
	 *            The interpolator which defines the acceleration curve
	 */
	public void setInterpolator(Interpolator interpolator) {
		mInterpolator = interpolator;
	}

	/**
	 * Gets the acceleration curve type for the indeterminate animation.
	 * 
	 * @return the {@link android.view.animation.Interpolator} associated to this animation
	 */
	public Interpolator getInterpolator() {
		return mInterpolator;
	}

	@Override
	public void setVisibility(int v) {
		if (getVisibility() != v) {
			super.setVisibility(v);

			if (mIndeterminate) {
				// let's be nice with the UI thread
				if (v == GONE || v == INVISIBLE) {
					stopAnimation();
				} else if (v == VISIBLE) {
					startAnimation();
				}
			}
		}
	}

	@Override
	public void invalidateDrawable(Drawable dr) {
		if (!mInDrawing) {
			if (verifyDrawable(dr)) {
				final Rect dirty = dr.getBounds();
				final int scrollX = getScrollX() + getPaddingLeft();
				final int scrollY = getScrollY() + getPaddingTop();

				invalidate(dirty.left + scrollX, dirty.top + scrollY,
						dirty.right + scrollX, dirty.bottom + scrollY);
			} else {
				super.invalidateDrawable(dr);
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// onDraw will translate the canvas so we draw starting at 0,0
		final int right = w - getPaddingRight() - getPaddingLeft();
		final int bottom = h - getPaddingBottom() - getPaddingTop();

		if (mIndeterminateDrawable != null) {
			mIndeterminateDrawable.setBounds(0, 0, right, bottom);
		}

		if (mProgressDrawable != null) {
			mProgressDrawable.setBounds(0, 0, right, bottom);
		}
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final Drawable d = mCurrentDrawable;
		if (d != null) {
			// Translate canvas so a indeterminate circular progress bar with
			// padding
			// rotates properly in its animation
			canvas.save();
			canvas.translate(getPaddingLeft(), getPaddingTop());
			final long time = getDrawingTime();
			if (mAnimation != null) {
				mAnimation.getTransformation(time, mTransformation);
				final float scale = mTransformation.getAlpha();
				try {
					mInDrawing = true;
					d.setLevel((int) (scale * MAX_LEVEL));
				} finally {
					mInDrawing = false;
				}
				if (SystemClock.uptimeMillis() - mLastDrawTime >= ANIMATION_RESOLUTION) {
					mLastDrawTime = SystemClock.uptimeMillis();
					postInvalidateDelayed(ANIMATION_RESOLUTION);
				}
			}
			d.draw(canvas);
			canvas.restore();
			if (mShouldStartAnimationDrawable && d instanceof Animatable) {
				((Animatable) d).start();
				mShouldStartAnimationDrawable = false;
			}
		}
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		final Drawable d = mCurrentDrawable;

		int dw = 0;
		int dh = 0;
		if (d != null) {
			dw = Math
					.max(mMinWidth, Math.min(mMaxWidth, d.getIntrinsicWidth()));
			dh = Math.max(mMinHeight,
					Math.min(mMaxHeight, d.getIntrinsicHeight()));
		}
		dw += getPaddingLeft() + getPaddingRight();
		dh += getPaddingTop() + getPaddingBottom();

		setMeasuredDimension(resolveSize(dw, widthMeasureSpec),
				resolveSize(dh, heightMeasureSpec));
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();

		final int[] state = getDrawableState();

		if (mProgressDrawable != null && mProgressDrawable.isStateful()) {
			mProgressDrawable.setState(state);
		}

		if (mIndeterminateDrawable != null
				&& mIndeterminateDrawable.isStateful()) {
			mIndeterminateDrawable.setState(state);
		}
	}

	static class SavedState extends BaseSavedState {

		int progress;
		int secondaryProgress;

		/**
		 * Constructor called from {@link android.widget.ProgressBar#onSaveInstanceState()}
		 */
		SavedState(Parcelable superState) {
			super(superState);
		}

		/**
		 * Constructor called from {@link #CREATOR}
		 */
		private SavedState(Parcel in) {
			super(in);
			progress = in.readInt();
			secondaryProgress = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(progress);
			out.writeInt(secondaryProgress);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	@Override
	public Parcelable onSaveInstanceState() {
		// Force our ancestor class to save its state
		final Parcelable superState = super.onSaveInstanceState();
		final SavedState ss = new SavedState(superState);

		ss.progress = mProgress;
		ss.secondaryProgress = mSecondaryProgress;

		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		final SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		setProgress(ss.progress);
		setSecondaryProgress(ss.secondaryProgress);
	}
}
