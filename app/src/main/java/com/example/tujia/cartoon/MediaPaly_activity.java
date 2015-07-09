package com.example.tujia.cartoon;

import java.io.File;
import java.io.IOException;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.tujia.R;

public class MediaPaly_activity extends SwipeBackActivity implements
		SurfaceHolder.Callback {
	private MediaPlayer mMediaPlayer01;
	private SurfaceView mSurfaceView01;
	private SurfaceHolder mSurfaceHolder01;
	private Button myButton, fullButton;
	private LinearLayout linerLayout;
	private ImageView imageView;
	private AnimationDrawable frameAnimation;
	private SeekBar seekBar;
	private boolean bIsPaused = false;
	private boolean bIsNotStart = true;
	private boolean isDissMissView = false;
	private boolean dissMissing = false;
	private String strVideoPath = "";
	private LayoutParams lp;
	private int screenWidthPixels, screenHightPixels, videoWidth, videoHeight;
	private TextView allTime, currentTime;
	private TextView playTitle, playContent;
	private Runnable disMissRun;
	private View titleBarView;
	private String videoUrl = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setEdgeFromLeftOrRight();
		setContentView(R.layout.activity_palymedia);
		
		setVideUrl();
		
		intiview();

		if (!checkSDCard()) // ���û��SD��
		{
			// mMakeTextToast(getResources().getText(R.string.str_err_nosd)
			// .toString(), true);
		}

		getWindow().setFormat(PixelFormat.UNKNOWN);
		RelativeLayout re = (RelativeLayout) findViewById(R.id.relaout01);
		re.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!bIsPaused && !bIsNotStart) {
					if (!isDissMissView) {
						dissMIssView();
					} else {
						seekBar.setVisibility(View.VISIBLE);
						myButton.setVisibility(View.VISIBLE);
						fullButton.setVisibility(View.VISIBLE);
						allTime.setVisibility(View.VISIBLE);
						currentTime.setVisibility(View.VISIBLE);
						isDissMissView = false;
						dissMIssView();
					}
				}

			}
		});
		allTime = (TextView) findViewById(R.id.alltime);
		currentTime = (TextView) findViewById(R.id.currenttime);
		mSurfaceView01 = (SurfaceView) findViewById(R.id.mSurfaceView1); // ��ʾ�����õ�����
		mSurfaceView01.setBackgroundResource(R.color.black);
		lp = mSurfaceView01.getLayoutParams();
		lp.width = LayoutParams.MATCH_PARENT;
		DisplayMetrics dms = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dms);
		screenHightPixels = dms.heightPixels;
		screenWidthPixels = dms.widthPixels;
		lp.height = screenWidthPixels * 309 / 500;

		mSurfaceView01.setLayoutParams(lp);
		mSurfaceHolder01 = mSurfaceView01.getHolder();
		mSurfaceHolder01.addCallback(this);
		// mSurfaceHolder01.setFixedSize(176, 144);
		mSurfaceHolder01.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				if (mMediaPlayer01 != null) {
					int total = mMediaPlayer01.getDuration();
					int max = seekBar.getMax();
					mMediaPlayer01.seekTo(arg0.getProgress() * total / max);
				}
			}
		});
		strVideoPath = getDir() + "/a.mp4";
		myButton = (Button) findViewById(R.id.mybutton);

		myButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (checkSDCard() && mMediaPlayer01 == null) {
					if (bIsNotStart) {
						dissMIssView();
						isDissMissView = true;
						new Thread(new Runnable() {

							@Override
							public void run() {
								playVideo(strVideoPath);
							}
						}).start();

						bIsNotStart = false;
						myButton.setBackgroundResource(R.drawable.pause);
					}
				} else if (checkSDCard() && mMediaPlayer01 != null) {
					if (!bIsNotStart) {
						if (bIsPaused == false) {
							mMediaPlayer01.pause();
							bIsPaused = true;
							myButton.setBackgroundResource(R.drawable.play);
							handler.removeCallbacks(disMissRun);
						} else {
							mMediaPlayer01.start();
							bIsPaused = false;
							myButton.setBackgroundResource(R.drawable.pause);
							dissMIssView();
						}
					}
				}
			}
		});
		fullButton = (Button) findViewById(R.id.full_screen);
		fullButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
						&& !bIsNotStart) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else if (!bIsNotStart) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
			}
		});

	}

	private void setVideUrl() {
		int index = getIntent().getIntExtra("ITEM_POSITION", 0);
		
		if (index == 0) {
			videoUrl = "http://210.42.151.54:8080/TuJia/xikalanpu.mp4";
		} else if (index == 1) {
			videoUrl = "http://210.42.151.54:8080/TuJia/youfangji.mp4";
		} else if (index == 2) {
			videoUrl = "http://210.42.151.54:8080/TuJia/bicika1.mp4";
		} else {
			videoUrl = "http://210.42.151.54:8080/TuJia/bicika2.mp4";
		}
	}

	private void intiview() {
		titleBarView = this.findViewById(R.id.title_bar);
		linerLayout = (LinearLayout) findViewById(R.id.liner_layout);
		imageView = (ImageView) findViewById(R.id.imgeView);
		imageView.setBackgroundResource(R.anim.loading);
		frameAnimation = (AnimationDrawable) imageView.getBackground();
		this.findViewById(R.id.book_list_title_backbtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(R.anim.open_main, R.anim.close_next);
					}
				});
		playTitle = (TextView) findViewById(R.id.play_title);
		playContent = (TextView) findViewById(R.id.play_content);
		int position = getIntent().getExtras().getInt("ITEM_POSITION");
		switch (position % 4) {
		case 0:
			playTitle.setText("西兰卡普");
			playContent.setText(R.string.playcontent_xilankapu);
			((TextView) this.findViewById(R.id.book_list_title_text))
					.setText("西兰卡普");
			break;
		case 1:
			playTitle.setText("游方记");
			playContent.setText(R.string.playcontent_youfangji);
			((TextView) this.findViewById(R.id.book_list_title_text))
					.setText("游方记");
			break;
		case 2:
			playTitle.setText("毕兹卡的传说一");
			playContent.setText(R.string.playcontent_bizika1);
			((TextView) this.findViewById(R.id.book_list_title_text))
					.setText("毕兹卡的传说一");
			break;
		case 3:
			playTitle.setText("毕兹卡的传说二");
			playContent.setText(R.string.playcontent_bizika2);
			((TextView) this.findViewById(R.id.book_list_title_text))
					.setText("毕兹卡的传说二");
			break;
		}
	}

	private boolean isDestory = false;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMediaPlayer01 != null) {
			mMediaPlayer01.release();
			mMediaPlayer01 = null;
		}
		isDestory = true;
	}
	int total;
	int current;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!isDestory) {
				super.handleMessage(msg);
				int position = mMediaPlayer01.getCurrentPosition();
				 total = mMediaPlayer01.getDuration();
				int max = seekBar.getMax();
				if (position >= 0 && total != 0) {
					seekBar.setProgress(position * max / total);
				}
				allTime.setText(formatLongToTimeStr(total));
				current = mMediaPlayer01.getCurrentPosition();
				currentTime.setText(formatLongToTimeStr(current));
			}
		}
	};

	private String formatLongToTimeStr(int l) {
		int hour = 0;
		int minute = 0;
		int second = 0;

		second = l / 1000;

		if (second > 60) {
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60) {
			hour = minute / 60;
			minute = minute % 60;
		}
		return (getTwoLength(hour) + ":" + getTwoLength(minute) + ":" + getTwoLength(second));
	}

	private String getTwoLength(final int data) {
		if (data < 10) {
			return "0" + data;
		} else {
			return "" + data;
		}
	}

	private void dissMIssView() {
		if (!dissMissing) {
			dissMissing = true;
			disMissRun = new Runnable() {

				@Override
				public void run() {
					seekBar.setVisibility(View.GONE);
					myButton.setVisibility(View.GONE);
					fullButton.setVisibility(View.GONE);
					allTime.setVisibility(View.GONE);
					currentTime.setVisibility(View.GONE);
					isDissMissView = true;
				}
			};
			handler.postDelayed(disMissRun, 3000);
			new onceDelayThread(3000).start();
		}
	}
	private int saveTime;
	private void playVideo(String strPath) {
		mMediaPlayer01 = new MediaPlayer();
		mMediaPlayer01.setAudioStreamType(AudioManager.STREAM_MUSIC);
		/*
		 * ����
		 */
		mMediaPlayer01
				.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent) {
						saveTime=percent/100*total-current;
						if(saveTime==0){
							linerLayout.setVisibility(View.VISIBLE);
							frameAnimation.setOneShot(true);
							frameAnimation.start();
						}else{
							frameAnimation.setOneShot(false);
							frameAnimation.stop();
							linerLayout.setVisibility(View.GONE);
							Log.v("0000", "xiaoshi");
						}
						Log.v("0000", "-----------");
					}
				});

		mMediaPlayer01.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				videoWidth = mMediaPlayer01.getVideoWidth();
				videoHeight = mMediaPlayer01.getVideoHeight();
				Log.v("0000videoWidth", videoWidth + "");
				Log.v("0000videoHeighth", videoHeight + "");
				if (videoHeight != 0 && videoWidth != 0) {
					mp.start();
					DelayThread delaythread = new DelayThread(100);
					delaythread.start();
					mSurfaceView01.setBackgroundDrawable(null);
				}
			}
		});
		mMediaPlayer01.setDisplay(mSurfaceHolder01);

		// try {
		// mMediaPlayer01.setDataSource(strPath);
		// } catch (Exception e) {
		// }

//		String uuu = "http://210.42.151.54:8080/RZMD/demo_video.mp4";
		
		Log.i("MediaPlayer", "videoUrl: " + videoUrl);
		
		try {
			mMediaPlayer01.setDataSource(videoUrl);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			mMediaPlayer01.prepare();
		} catch (Exception e) {
		}

		mMediaPlayer01
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer arg0) {
					
					}
				});

	}

	private boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	private class DelayThread extends Thread {
		private int delayTime;

		private DelayThread(int delayTime) {
			this.delayTime = delayTime;
		}

		@Override
		public void run() {
			super.run();
			while (true) {
				try {
					sleep(delayTime);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class onceDelayThread extends Thread {
		private int delayTime;

		private onceDelayThread(int delayTime) {
			this.delayTime = delayTime;
		}

		@Override
		public void run() {
			super.run();
			try {
				sleep(delayTime);
				dissMissing = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			lp = mSurfaceView01.getLayoutParams();
			lp.width = screenHightPixels;
			lp.height = screenWidthPixels;
			mSurfaceView01.setLayoutParams(lp);
			fullButton.setBackgroundResource(R.drawable.small_screen);
			titleBarView.setVisibility(View.GONE);
			
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			lp = mSurfaceView01.getLayoutParams();
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = screenWidthPixels * 309 / 500;
			mSurfaceView01.setLayoutParams(lp);
			fullButton.setBackgroundResource(R.drawable.full_screen);
			titleBarView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w,
			int h) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceholder) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
	}

	private File getDir() {
		File temp = Environment.getExternalStorageDirectory();
		String dirPaht = temp.getPath();
		File dir = new File(dirPaht);
		if (dir.exists()) {
			return dir;
		} else {
			dir.mkdirs();
			return dir;
		}
	}

	@Override
	public void inWindow() {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
	}

}