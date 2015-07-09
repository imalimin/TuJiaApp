package com.example.tujia.comic.view;

import java.util.List;

import com.example.tujia.comic.data.ComicDetail.StoryLine;
import com.example.tujia.comic.reader.ReadingActivity;
import com.google.common.collect.Lists;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;

public class MyImageView extends ImageView {

  public static interface OnStoryLineFinishedListener {

    public void onStoryLineFinished(boolean isNext);
  }

  private enum AnimationType {
    NORMAL, NEXT, PREVIOUS,
  }

  private AnimationType animationType = AnimationType.NORMAL;

  private int touchSlop = 0;

  private final static int ANIMATION_DURATION = 350;

  private static Matrix currentMatrix = new Matrix();
  private float[] lv = new float[9];
  private float[] cv = new float[9];
  private long animationStartTime = 0L;
  private Matrix originMatrix = new Matrix();
  private Matrix targetMatrix = new Matrix();
  private Matrix inversedOriginMatrix = new Matrix();

  private float[] temp = null;
  private float[] temp2 = null;
  private float[] temp3 = null;

  private int drawableWidth = 0;
  private int drawableHeight = 0;

  private static int tempWidth = 0;
  private static int tempHeight = 0;

  private static final int NONE = 0;
  private static final int DRAG = 1;
  private static final int ZOOM = 2;
  private int mode = NONE;

  private static boolean isStoryLineMode;

  // for drag
  private boolean isJustDown = false;
  private PointF downPoint = new PointF();
  private PointF latestPoint = new PointF();

  // for zoom
  private float[] midPoint = new float[2];
  private float downDistance = 1.0f;
  private float latestDistance = 1.0f;

  private GestureDetector doubleClickGestureDetector = null;

  private List<StoryLine> storyLines = null;
  private int currentStoryLineIndex = 0;
  private int requestStoryLineIndex = -1;
  private OnStoryLineFinishedListener onStoryLineFinishedListener = null;

  private RectF tempRectF = new RectF();
  private RectF tempRectF2 = new RectF();

  private boolean isMeasured = false;
  private boolean canDraw = false;
  private boolean canTouch = false;
  private boolean isActivityAnimation = false;

  private int minFlingVelocityX = 0;
  private int minFlingVelocityY = 0;
  private int minScrollDistance = 0;

  public MyImageView(Context context) {
    super(context);
    init();
  }

  public MyImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public MyImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    if (storyLines == null || storyLines.isEmpty()) {
      storyLines = Lists.newArrayList();
      storyLines.add(new StoryLine(0, 0, drawableWidth, drawableHeight));
    }

    final ViewConfiguration configuration = ViewConfiguration.get(getContext());
    // mTouchSlop = configuration.getScaledTouchSlop();
    touchSlop = 6;
    minFlingVelocityX = configuration.getScaledMaximumFlingVelocity() / 6;
    minFlingVelocityY = configuration.getScaledMaximumFlingVelocity() / 12;
    minScrollDistance = touchSlop * 3;

    temp = new float[9];
    temp2 = new float[9];
    temp3 = new float[9];

    doubleClickGestureDetector =
        new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {

          private boolean hasActioned = false;

          @Override
          public boolean onDown(MotionEvent e) {
            hasActioned = false;
            return false;
          }

          @Override
          public void onShowPress(MotionEvent e) {
          }

          @Override
          public boolean onSingleTapUp(MotionEvent e) {
            return false;
          }

          @Override
          public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isMeasured || !canTouch || hasActioned || mode == ZOOM) {
              return true;
            }

            if (Math.abs(distanceX) < minScrollDistance) {
              return true;
            }

            if (Math.abs(distanceX) < Math.abs(distanceY)) {
              return true;
            }

            if (!isInNormalState()) {
              // getTargetMatrixInNormalState();
              // startAnimation(AnimationType.NORMAL);
              // hasActioned = true;
              if (!isInSmallState()) {
                return true;
              }
            }

            if (distanceX > 0) {
              if (storyLines != null && currentStoryLineIndex < storyLines.size() - 1) {
                ++currentStoryLineIndex;
                animateToCurrentStoryLine(true);
              } else {
                canTouch = false;
                if (onStoryLineFinishedListener != null) {
                  onStoryLineFinishedListener.onStoryLineFinished(true);
                }
              }
            } else {
              if (storyLines != null && currentStoryLineIndex > 0) {
                --currentStoryLineIndex;
                animateToCurrentStoryLine(false);
              } else {
                canTouch = false;
                if (onStoryLineFinishedListener != null) {
                  onStoryLineFinishedListener.onStoryLineFinished(false);
                }
              }
            }

            hasActioned = true;
            return true;
          }

          @Override
          public void onLongPress(MotionEvent e) {
          }

          @Override
          public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isMeasured || !canTouch || hasActioned || mode == ZOOM) {
              return true;
            }

            // if (Math.abs(velocityX) < minFlingVelocity) {
            // return true;
            // }

            float[] v = new float[9];
            currentMatrix.getValues(v);
            boolean isSame = true;
            for (int i = 0; i < v.length; i++) {
              if (lv[i] != v[i]) {
                isSame = false;
                break;
              }
            }
            if (isSame) {

            } else if (Math.abs(velocityX) < Math.abs(velocityY)) {
              return true;
            }

            if (!isInNormalState()) {
              // getTargetMatrixInNormalState();
              // startAnimation(AnimationType.NORMAL);
              /*
               * if (!isInSmallState()) { return true; }
               */
            }

            if (isSame && Math.abs(velocityY) > minFlingVelocityY) {
              if (velocityY < 0) {
                if (storyLines != null && currentStoryLineIndex < storyLines.size() - 1) {
                  ++currentStoryLineIndex;
                  animateToCurrentStoryLine(true);
                } else {
                  canTouch = false;
                  if (onStoryLineFinishedListener != null) {
                    onStoryLineFinishedListener.onStoryLineFinished(true);
                  }
                }
              } else {
                if (storyLines != null && currentStoryLineIndex > 0) {
                  --currentStoryLineIndex;
                  animateToCurrentStoryLine(false);
                } else {
                  canTouch = false;
                  if (onStoryLineFinishedListener != null) {
                    onStoryLineFinishedListener.onStoryLineFinished(false);
                  }
                }
              }
            } else if (Math.abs(velocityX) > minFlingVelocityX) {
              if (velocityX < 0) {
                if (storyLines != null && currentStoryLineIndex < storyLines.size() - 1) {
                  ++currentStoryLineIndex;
                  animateToCurrentStoryLine(true);
                } else {
                  canTouch = false;
                  if (onStoryLineFinishedListener != null) {
                    onStoryLineFinishedListener.onStoryLineFinished(true);
                  }
                }
              } else {
                if (storyLines != null && currentStoryLineIndex > 0) {
                  --currentStoryLineIndex;
                  animateToCurrentStoryLine(false);
                } else {
                  canTouch = false;
                  if (onStoryLineFinishedListener != null) {
                    onStoryLineFinishedListener.onStoryLineFinished(false);
                  }
                }
              }
            }

            hasActioned = true;
            return true;
          }
        });

    doubleClickGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {

      @Override
      public boolean onSingleTapConfirmed(MotionEvent e) {
        if (e.getX() < getWidth() / 3) {
          if (storyLines != null && currentStoryLineIndex > 0) {
            --currentStoryLineIndex;
            animateToCurrentStoryLine(false);
          } else {
            canTouch = false;
            if (onStoryLineFinishedListener != null) {
              onStoryLineFinishedListener.onStoryLineFinished(false);
            }
          }
        } else if (e.getX() > getWidth() / 3 * 2) {
          if (storyLines != null && currentStoryLineIndex < storyLines.size() - 1) {
            ++currentStoryLineIndex;
            animateToCurrentStoryLine(true);
          } else {
            canTouch = false;
            if (onStoryLineFinishedListener != null) {
              onStoryLineFinishedListener.onStoryLineFinished(true);
            }
          }
        } else {
          if (getContext() instanceof ReadingActivity) {
            if (canTouch) {
              final ReadingActivity a = (ReadingActivity) getContext();
              a.toggleMenu();
            }
          }
        }

        return true;
      }

      @Override
      public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
      }

      @Override
      public boolean onDoubleTap(MotionEvent e) {
        if (!isMeasured || !canTouch/* || isActivityAnimation */) {
          return true;
        }

        if (animationStartTime > 0L) {
          return true;
        }

        if (isInSmallState()) {
          getTargetMatrixInNormalState();
          startAnimation(AnimationType.NORMAL);
          return true;
        }

        final StoryLine storyLine = storyLines.get(currentStoryLineIndex);
        float w = storyLine.getWidth();
        float h = storyLine.getHeight();
        if (w == 0) {
          w = drawableWidth;
        }
        if (h == 0) {
          h = drawableHeight;
        }

        final float wRatio = getWidth() / w;
        final float hRatio = getHeight() / h;

        currentMatrix.getValues(temp2);

        if (wRatio < hRatio) {
          final float currentScale = temp2[Matrix.MSCALE_X];
          if (currentScale * w < getWidth() * 1.01f) {
            getTargetMatrixInNormalState();
            targetMatrix.invert(inversedOriginMatrix);
            midPoint[0] = getWidth() / 2;
            midPoint[1] = getHeight() / 2;
            inversedOriginMatrix.mapPoints(midPoint);
            targetMatrix.preScale(2.0f, 2.0f, midPoint[0], midPoint[1]);
          } else {
            getTargetMatrixInNormalState();
          }
        } else {
          final float currentScale = temp2[Matrix.MSCALE_Y];
          if (currentScale * h < getHeight() * 1.01f) {
            getTargetMatrixInNormalState();
            targetMatrix.invert(inversedOriginMatrix);
            midPoint[0] = getWidth() / 2;
            midPoint[1] = getHeight() / 2;
            inversedOriginMatrix.mapPoints(midPoint);
            targetMatrix.preScale(2.0f, 2.0f, midPoint[0], midPoint[1]);
          } else {
            getTargetMatrixInNormalState();
          }
        }

        startAnimation(AnimationType.NORMAL);

        return true;
      }
    });
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    isMeasured = true;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
//    if (animationStartTime > 0L) {
//    	
//    	Logger.i("onTouchEvent", "------------ animationStartTime > 0L ---------------");
//      mode = NONE;
//      return true;
//    }

    doubleClickGestureDetector.onTouchEvent(event);

    // animationStartTime could be changed in GestureDetector's onTouchEvent, so double check here
//    if (animationStartTime > 0L || !canTouch /*|| isActivityAnimation*/) {
//    	
//    	Logger.i("onTouchEvent", "------------ animationStartTime > 0L || !canTouch || isActivityAnimation ---------------");
//      mode = NONE;
//      return true;
//    }

//    Logger.i("onTouchEvent", "------------event.getAction(): "+ event.getAction() +" ---------------");
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        if (!isInNormalState() && !isInSmallState()) {
          mode = DRAG;
        }
        downPoint.set(event.getX(), event.getY());
        latestPoint.set(event.getX(), event.getY());
        isJustDown = true;
        currentMatrix.getValues(cv);
        lv = cv;
        break;
      case MotionEvent.ACTION_POINTER_DOWN:
        downDistance = distance(event);
        latestDistance = downDistance;
        if (downDistance > 10.0f) {
          currentMatrix.invert(inversedOriginMatrix);
          midPoint(event);
          mode = ZOOM;
        }
        isJustDown = false;
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_POINTER_UP:
      case MotionEvent.ACTION_CANCEL:
        if (isInSmallState()) {
          // getTargetMatrixInNormalState();

          // startAnimation(AnimationType.NORMAL);
        }

        mode = NONE;
        isJustDown = false;
        break;
      case MotionEvent.ACTION_MOVE:
        if (mode == DRAG) {
          if (!isJustDown
              || (Math.abs(event.getX() - downPoint.x) > touchSlop || Math.abs(event.getY()
                  - downPoint.y) > touchSlop)) {
            float translateX = 0.0f;
            float translateY = 0.0f;
            if (isJustDown) {
              if (Math.abs(event.getX() - downPoint.x) > touchSlop) {
                if (event.getX() - downPoint.x > 0) {
                  translateX = event.getX() - downPoint.x - touchSlop;
                } else {
                  translateX = event.getX() - downPoint.x + touchSlop;
                }
              }
              if (Math.abs(event.getY() - downPoint.y) > touchSlop) {
                if (event.getY() - downPoint.y > 0) {
                  translateY = event.getY() - downPoint.y - touchSlop;
                } else {
                  translateY = event.getY() - downPoint.y + touchSlop;
                }
              }
            } else {
              translateX = event.getX() - latestPoint.x;
              translateY = event.getY() - latestPoint.y;
            }

            currentMatrix.postTranslate(translateX, translateY);

            checkTranslate(currentMatrix);

            latestPoint.set(event.getX(), event.getY());
            isJustDown = false;
          }
        } else if (mode == ZOOM) {
          final float distance = distance(event);
          if (distance > 10.0f) {
            final float scale = distance / latestDistance;
            currentMatrix.preScale(scale, scale, midPoint[0], midPoint[1]);

            checkTranslate(currentMatrix);

            currentMatrix.invert(inversedOriginMatrix);
            midPoint(event);
          }
          latestDistance = distance;
        }
        break;
    }

    setImageMatrix(currentMatrix);

    return true;
  }

  private void checkTranslate(Matrix matrix) {
    final StoryLine storyLine = storyLines.get(currentStoryLineIndex);
    final float x = storyLine.getX();
    final float y = storyLine.getY();
    float w = storyLine.getWidth();
    float h = storyLine.getHeight();
    if (w == 0) {
      w = drawableWidth;
    }
    if (h == 0) {
      h = drawableHeight;
    }

    matrix.getValues(temp);

    float translateX = temp[Matrix.MTRANS_X];
    final float scaleX = temp[Matrix.MSCALE_X];
    if (w * temp[Matrix.MSCALE_X] > getWidth()) {
      if (translateX > -x * scaleX) {
        translateX = -x * scaleX;
      } else if (translateX < getWidth() - w * scaleX - x * scaleX) {
        translateX = getWidth() - w * scaleX - x * scaleX;
      }
    } else {
      if (translateX < -x * scaleX) {
        translateX = -x * scaleX;
      } else if (translateX + w * scaleX > getWidth() - x * scaleX) {
        translateX = getWidth() - w * scaleX - x * scaleX;
      }
    }

    float translateY = temp[Matrix.MTRANS_Y];
    final float scaleY = temp[Matrix.MSCALE_Y];
    if (h * scaleY > getHeight()) {
      if (translateY > -y * scaleY) {
        translateY = -y * scaleY;
      } else if (translateY < getHeight() - h * scaleY - y * scaleY) {
        translateY = getHeight() - h * scaleY - y * scaleY;
      }
    } else {
      if (translateY < -y * scaleY) {
        translateY = -y * scaleY;
      } else if (translateY + h * scaleY > getHeight() - y * scaleY) {
        translateY = getHeight() - h * scaleY - y * scaleY;
      }
    }

    matrix.postTranslate(translateX - temp[Matrix.MTRANS_X], translateY - temp[Matrix.MTRANS_Y]);
  }

  private float distance(MotionEvent event) {
    final float x = event.getX(0) - event.getX(1);
    final float y = event.getY(0) - event.getY(1);
    return FloatMath.sqrt(x * x + y * y);
  }

  private void midPoint(MotionEvent event) {
    midPoint[0] = (event.getX(0) + event.getX(1)) / 2;
    midPoint[1] = (event.getY(0) + event.getY(1)) / 2;
    inversedOriginMatrix.mapPoints(midPoint);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (!canDraw) {
      return;
    }

    final long currentTime = System.currentTimeMillis();

    if (storyLines != null && currentStoryLineIndex < storyLines.size()) {
      final int saveCount = canvas.save();

      if ((animationType == AnimationType.NEXT && animationStartTime > 0L && currentStoryLineIndex > 0)
          || (animationType == AnimationType.PREVIOUS && animationStartTime > 0L && currentStoryLineIndex < storyLines
              .size() - 1)) {
        final StoryLine storyLine1 =
            animationType == AnimationType.NEXT ? storyLines.get(currentStoryLineIndex - 1)
                : storyLines.get(currentStoryLineIndex + 1);
        tempRectF.set(storyLine1.getX(), storyLine1.getY(),
            storyLine1.getX() + storyLine1.getWidth(), storyLine1.getY() + storyLine1.getHeight());
        originMatrix.mapRect(tempRectF);

        final StoryLine storyLine2 = storyLines.get(currentStoryLineIndex);
        tempRectF2.set(storyLine2.getX(), storyLine2.getY(),
            storyLine2.getX() + storyLine2.getWidth(), storyLine2.getY() + storyLine2.getHeight());
        targetMatrix.mapRect(tempRectF2);

        float percent = (float) (currentTime - animationStartTime) / (float) ANIMATION_DURATION;
        if (percent < 0.0f) {
          percent = 0.0f;
        } else if (percent > 1.0f) {
          percent = 1.0f;
        }
        final float left = tempRectF.left + (tempRectF2.left - tempRectF.left) * percent;
        final float top = tempRectF.top + (tempRectF2.top - tempRectF.top) * percent;
        final float right = tempRectF.right + (tempRectF2.right - tempRectF.right) * percent;
        final float bottom = tempRectF.bottom + (tempRectF2.bottom - tempRectF.bottom) * percent;
        canvas.clipRect(left, top, right, bottom);
      } else {
        final StoryLine storyLine = storyLines.get(currentStoryLineIndex);
        tempRectF.set(storyLine.getX(), storyLine.getY(), storyLine.getX() + storyLine.getWidth(),
            storyLine.getY() + storyLine.getHeight());
        currentMatrix.mapRect(tempRectF);
        canvas.clipRect(tempRectF);
      }

      super.onDraw(canvas);

      canvas.restoreToCount(saveCount);
    }

    if (animationStartTime > 0L) {
      if (currentTime - animationStartTime >= ANIMATION_DURATION) {
        animationStartTime = 0L;
        currentMatrix.set(targetMatrix);
        setImageMatrix(currentMatrix);
      } else {
        final float percent =
            (float) (currentTime - animationStartTime) / (float) ANIMATION_DURATION;

        originMatrix.getValues(temp2);
        targetMatrix.getValues(temp);
        for (int i = 0; i < temp.length; ++i) {
          temp3[i] = temp[i] * percent + temp2[i] * (1.0f - percent);
        }
        currentMatrix.setValues(temp3);
        setImageMatrix(currentMatrix);
        invalidate();
      }
    }
  }

  public void startStoryLine(Bitmap bitmap, List<StoryLine> storyLines,
      OnStoryLineFinishedListener listener, int storyLineIndex) {
    requestStoryLineIndex = storyLineIndex;
    startStoryLine(bitmap, storyLines, listener, false);
  }

  public void startStoryLine(Bitmap bitmap, List<StoryLine> storyLines,
      OnStoryLineFinishedListener listener, boolean fromBack) {

    onStoryLineFinishedListener = listener;
    
    setImageBitmap(bitmap);
    
    if (bitmap == null) {
      return;
    }

    drawableWidth = bitmap.getWidth();
    drawableHeight = bitmap.getHeight();
    if (tempWidth == 0 && tempHeight == 0) {
      tempWidth = drawableWidth;
      tempHeight = drawableHeight;
    }

    canTouch = true;

    if (storyLines == null || storyLines.isEmpty()) {
      this.storyLines = Lists.newArrayList();
      this.storyLines.add(new StoryLine(0, 0, drawableWidth, drawableHeight));
    } else {
      this.storyLines = storyLines;
    }

    if (requestStoryLineIndex >= 0) {
      currentStoryLineIndex = requestStoryLineIndex;
      getTargetMatrixInNormalState();
      startAnimation(AnimationType.NORMAL);
      animateToCurrentStoryLine(true);
    } else if (fromBack) {
      currentStoryLineIndex = this.storyLines.size() - 1;
      animateToCurrentStoryLine(false);
    } else {
      currentStoryLineIndex = 0;
      getTargetMatrixInNormalState();
      startAnimation(AnimationType.NORMAL);
      animateToCurrentStoryLine(true);
    }
  }

  private void animateToCurrentStoryLine(final boolean isNext) {
    if (!isMeasured) {
      postDelayed(new Runnable() {

        @Override
        public void run() {
          animateToCurrentStoryLine(isNext);
        }
      }, 20);
      return;
    }

    canDraw = true;

    getTargetMatrixInNormalState();

    if (requestStoryLineIndex >= 0 || (isNext && currentStoryLineIndex == 0)
        || (!isNext && currentStoryLineIndex == storyLines.size() - 1)) {
      if (tempWidth == drawableWidth && tempHeight == drawableHeight) {
        if (MyImageView.isStoryLineMode)
          currentMatrix.set(targetMatrix);
      } else {
        currentMatrix.set(targetMatrix);
      }
      setImageMatrix(currentMatrix);
      requestStoryLineIndex = -1;
      tempWidth = drawableWidth;
      tempHeight = drawableHeight;
      invalidate();
    } else {
      startAnimation(isNext ? AnimationType.NEXT : AnimationType.PREVIOUS);
    }
  }

  private void getTargetMatrixInNormalState() {
    final StoryLine storyLine = storyLines.get(currentStoryLineIndex);
    final float x = storyLine.getX();
    final float y = storyLine.getY();
    float w = storyLine.getWidth();
    float h = storyLine.getHeight();
    if (w == 0) {
      w = drawableWidth;
    }
    if (h == 0) {
      h = drawableHeight;
    }

    final float wRatio = w / getWidth();
    final float hRatio = h / getHeight();

    int targetX = 0;
    int targetY = 0;
    int targetWidth = 0;
    int targetHeight = 0;
    if (wRatio > hRatio) {
      targetWidth = getWidth();
      targetHeight = (int) (h * targetWidth / w);
      targetY = (getHeight() - targetHeight) / 2;
    } else {
      targetHeight = getHeight();
      targetWidth = (int) (w * targetHeight / h);
      targetX = (getWidth() - targetWidth) / 2;
    }

    targetMatrix.setScale(targetWidth / w, targetHeight / h);
    targetMatrix.preTranslate(-x, -y);
    targetMatrix.postTranslate(targetX, targetY);
  }

  private boolean isInNormalState() {
    currentMatrix.getValues(temp2);

    final StoryLine storyLine = storyLines.get(currentStoryLineIndex);
    float w = storyLine.getWidth();
    float h = storyLine.getHeight();
    if (w == 0) {
      w = drawableWidth;
    }
    if (h == 0) {
      h = drawableHeight;
    }

    final float wRatio = getWidth() / w;
    final float hRatio = getHeight() / h;
    final float ratio = wRatio < hRatio ? wRatio : hRatio;

    final float scale = wRatio < hRatio ? temp2[Matrix.MSCALE_X] : temp2[Matrix.MSCALE_Y];

    return scale < ratio + 0.01 && scale > ratio - 0.01;
  }

  private boolean isInSmallState() {
    currentMatrix.getValues(temp2);

    final StoryLine storyLine = storyLines.get(currentStoryLineIndex);
    float w = storyLine.getWidth();
    float h = storyLine.getHeight();
    if (w == 0) {
      w = drawableWidth;
    }
    if (h == 0) {
      h = drawableHeight;
    }

    final float wRatio = getWidth() / w;
    final float hRatio = getHeight() / h;
    final float ratio = wRatio < hRatio ? wRatio : hRatio;

    return ratio - temp2[Matrix.MSCALE_X] > 0.01;
  }

  private void startAnimation(AnimationType type) {
    animationType = type;
    animationStartTime = System.currentTimeMillis();

    originMatrix.set(currentMatrix);

    invalidate();
  }

  public void setIsActivityAnimation(boolean isActivityAnimation) {
    this.isActivityAnimation = isActivityAnimation;
  }

  public int getStoryLineIndex() {
    return currentStoryLineIndex;
  }

  public void setCanTouch(boolean canTouch) {
    this.canTouch = canTouch;
  }

  public static void setStoryLineMode(boolean isStoryLineMode) {
    MyImageView.isStoryLineMode = isStoryLineMode;
  }

  public boolean isCanTouch() {
    return canTouch;
  }

}
