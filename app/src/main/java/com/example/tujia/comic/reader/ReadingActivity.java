package com.example.tujia.comic.reader;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tujia.R;
import com.example.tujia.comic.data.ComicDetail.ImageInfo;
import com.example.tujia.comic.data.ComicDetail.StoryLine;
import com.example.tujia.comic.data.LocalDownloadChapter;
import com.example.tujia.comic.data.LocalDownloadChapter.FileSaveLocation;
import com.example.tujia.comic.data.ShareImageInfoEntry;
import com.example.tujia.comic.util.BitmapTool;
import com.example.tujia.comic.util.FileUtil2;
import com.example.tujia.comic.util.ScreenTool;
import com.example.tujia.comic.view.MyImageView;
import com.example.tujia.comic.view.MySeekBar;

public class ReadingActivity extends Activity {
	private MyImageView myImageView = null;

	private RelativeLayout topFrame;
	private ImageButton back;
	private TextView title;
	private LinearLayout bottomFrame;
	private TextView index;
	private MySeekBar readingSeekBar;
	private ImageView screenorient_imageview;
	private MyImageView.OnStoryLineFinishedListener listener = null;

	private LinearLayout rightFrame;
	private ImageView brightnessButton;

	private Animation inFromTop;
	private Animation outToTop;
	private Animation inFromBottom;
	private Animation outToBottom;
	@SuppressWarnings("unused")
	private Animation inFromRight;
	private Animation outToRight;

	private boolean isMenuAnimating;
	private boolean fromBack = false;

	private int currentImageIndex = 0;
	private int currentStoryLineIndex = 0;

	// for change Screen Orientation
	private boolean isNext;
	private boolean islandscap;

	private static String readFileName;
	private static int readFileSaveLocation;
	private ShareImageInfoEntry imageInfoEntry;
	private ShareImageInfoEntry currentImageInfoEntry;
	private static List<String> imageNameList;
	private int imageSize;
	LocalDownloadChapter chapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_share_reading);

		if (savedInstanceState != null) {
			currentImageIndex = savedInstanceState.getInt("image index", 0);
			currentStoryLineIndex = savedInstanceState.getInt(
					"story line index", -1);
		} else {
			currentImageIndex = 0;
			currentStoryLineIndex = 0;
		}

		initData();
		initView();
	}

	private void initData() {
		// TODO
		readFileName = this.getIntent().getCharSequenceExtra("FILE_NAME")
				.toString();

		// File fileDirRoot = new
		// File(Environment.getExternalStorageDirectory().getPath() +
		// "/TuJia2");
		// if (!fileDirRoot.exists()) {
		// boolean result = fileDirRoot.mkdir();
		// System.out.println("-------- make root dir --------" + result);
		// }
		// File fileDir = new
		// File(Environment.getExternalStorageDirectory().getPath() +
		// "/TuJia2/cartoon_files");
		// if (!fileDir.exists()) {
		// boolean result = fileDir.mkdir();
		// System.out.println("-------- make dir --------" + result);
		// }

		String imagePath = readFileName;

		// imageNameList =
		// FileUtil2.getDirFileNames(LocalDownloadUtil.getReadFolder(chapter));

		System.out.println("path:  " + imagePath);
		try {
			imageNameList = FileUtil2.getDirFileNames(imagePath,
					getApplicationContext());
		} catch (IOException e) {
			e.printStackTrace();
		}
		readFileSaveLocation = FileSaveLocation.SD_CARD;

		imageSize = imageNameList.size();

		System.out.println("image——size: " + imageSize);
		imageInfoEntry = new ShareImageInfoEntry(imageSize);
	}

	private void initView() {
		topFrame = (RelativeLayout) findViewById(R.id.top_frame);
		back = (ImageButton) findViewById(R.id.back);
		title = (TextView) findViewById(R.id.title);
		bottomFrame = (LinearLayout) findViewById(R.id.bottom_frame);
		screenorient_imageview = (ImageView) findViewById(R.id.screenorient_imageview);
		index = (TextView) findViewById(R.id.index);
		readingSeekBar = (MySeekBar) findViewById(R.id.reading_seekbar);
		rightFrame = (LinearLayout) findViewById(R.id.right_frame);
		if (readFileName.equals("shaonianyingyong")) {
			title.setText("少年英雄");
		} else if (readFileName.equals("baihuzhuanshi")) {
			title.setText("白虎转世");
		} else if (readFileName.equals("wuzujiemeng")) {
			title.setText("五族结盟");
		}

		myImageView = (MyImageView) findViewById(R.id.my_image_view);
		myImageView.setIsActivityAnimation(false);

		myImageView.setVisibility(View.VISIBLE);

		// 返回
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 内存回收
				// TODO
				// ShareImageInfoEntryCacheManager.INSTANCE.clean();
				finish();
				overridePendingTransition(R.anim.open_main, R.anim.close_next);
			}
		});

		// 进度控制
		readingSeekBar.setMax(imageSize - 1);
		readingSeekBar
				.setOnProgressChangedListener(new MySeekBar.OnProgressChangedListener() {

					@Override
					public void onProgressChanged(int progress) {
						index.setText((progress + 1) + "/" + imageSize);
					}

					@Override
					public void onStopTrackingTouch() {
						final int progress = readingSeekBar.getProgress();
						if (currentImageIndex != progress) {
							if (progress > currentImageIndex) {
								isNext = true;
							} else {
								isNext = false;
							}
							currentImageIndex = progress;
							startToRead();
						}
					}
				});

		// brightnessButton.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (isMenuAnimating) {
		// return;
		// }
		// if (rightFrame.getVisibility() == View.VISIBLE) {
		// rightFrame.setVisibility(View.INVISIBLE);
		// } else {
		// rightFrame.setVisibility(View.VISIBLE);
		// }
		// }
		// });

		// final int brightness = ScreenBrightnessUtil.getScreenBrightness();
		// final MyVerticalSeekBar brightnessSeekBar = (MyVerticalSeekBar)
		// findViewById(R.id.brightness_seekbar);
		// brightnessSeekBar.setMax(255 - 1);
		// brightnessSeekBar.setProgress(255 - brightness);
		// brightnessSeekBar
		// .setOnProgressChangedListener(new
		// MyVerticalSeekBar.OnProgressChangedListener() {
		//
		// @Override
		// public void onProgressChanged(int progress) {
		// ScreenBrightnessUtil.setBrightness(ReadingActivity.this, 255 -
		// progress);
		// }
		// });

		inFromTop = AnimationUtils.loadAnimation(this, R.anim.in_from_top);
		outToTop = AnimationUtils.loadAnimation(this, R.anim.out_to_top);
		inFromBottom = AnimationUtils
				.loadAnimation(this, R.anim.in_from_bottom);
		outToBottom = AnimationUtils.loadAnimation(this, R.anim.out_to_bottom);
		inFromRight = AnimationUtils.loadAnimation(this, R.anim.in_from_right);
		outToRight = AnimationUtils.loadAnimation(this, R.anim.out_to_right);

		inFromTop.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isMenuAnimating = true;
				if (islandscap) {
					screenorient_imageview
							.setImageResource(R.drawable.read_mode_portrait);
				} else {
					screenorient_imageview
							.setImageResource(R.drawable.read_mode_landscapel);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isMenuAnimating = false;
			}
		});

		outToTop.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isMenuAnimating = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				topFrame.setVisibility(View.INVISIBLE);
				// bottomFrame.setVisibility(View.INVISIBLE);
				rightFrame.setVisibility(View.INVISIBLE);
				isMenuAnimating = false;
			}
		});

		// toggleMenu();
		ShareImageInfoEntry entry = getImageInfoEntry();
		if (entry != null) {
			startRead(entry);
		}
	}

	private void startRead(final ShareImageInfoEntry imageInfoEntry) {
		currentImageInfoEntry = imageInfoEntry;
		if (currentImageIndex == Integer.MAX_VALUE
				|| currentImageIndex >= currentImageInfoEntry.getImageCount()) {
			currentImageIndex = currentImageInfoEntry.getImageCount() - 1;
		}
		if (currentImageIndex == Integer.MIN_VALUE || currentImageIndex < 0) {
			currentImageIndex = 0;
		}

		readingSeekBar.setMax(imageSize - 1);
		readingSeekBar.setProgress(currentImageIndex);
		index.setText((currentImageIndex + 1) + "/" + imageSize);

		if (listener == null) {
			listener = new MyImageView.OnStoryLineFinishedListener() {

				@Override
				public void onStoryLineFinished(final boolean isNext) {
					ReadingActivity.this.isNext = isNext;

					if (isNext) {
						++currentImageIndex;
						if (currentImageIndex >= imageSize) {
							--currentImageIndex;
							myImageView.setCanTouch(true);
							Toast.makeText(ReadingActivity.this, "后面没有了...",
									Toast.LENGTH_SHORT).show();
							return;
						}
					} else {
						--currentImageIndex;
						if (currentImageIndex < 0) {
							++currentImageIndex;
							myImageView.setCanTouch(true);
							Toast.makeText(ReadingActivity.this, "前面没有了...",
									Toast.LENGTH_SHORT).show();
							return;
						}
					}
					startToRead();
				}
			};
		}

		if (currentStoryLineIndex >= 0) {
			myImageView.startStoryLine(currentImageInfoEntry.getBitmap(),
					getStoryLines(currentImageInfoEntry.getImageInfo()),
					listener, currentStoryLineIndex);
			currentStoryLineIndex = -1;
		} else {
			myImageView.setIsActivityAnimation(true);
			myImageView.startStoryLine(currentImageInfoEntry.getBitmap(),
					getStoryLines(currentImageInfoEntry.getImageInfo()),
					listener, fromBack);
		}
	}

	private void startToRead() {
		ShareImageInfoEntry entry = getImageInfoEntry();

		if (entry != null) {
			startRead(entry);
		}
	}

	private List<StoryLine> getStoryLines(ImageInfo imageInfo) {
		if (imageInfo != null) {
			return imageInfo.getStoryLine();
		}
		return null;
	}

	public void showMenu() {
		if (isMenuAnimating) {
			return;
		}

		topFrame.setVisibility(View.VISIBLE);
		// bottomFrame.setVisibility(View.VISIBLE);

		isMenuAnimating = true;

		topFrame.startAnimation(inFromTop);
		// bottomFrame.startAnimation(inFromBottom);
	}

	public void hideMenu() {
		if (isMenuAnimating) {
			return;
		}

		topFrame.startAnimation(outToTop);
		// bottomFrame.startAnimation(outToBottom);
		if (rightFrame.getVisibility() == View.VISIBLE) {
			rightFrame.startAnimation(outToRight);
		}
	}

	public void toggleMenu() {
		if (topFrame.getVisibility() != View.VISIBLE) {
			showMenu();
		} else {
			hideMenu();
		}
	}

	private ShareImageInfoEntry getImageInfoEntry() {
		if (imageInfoEntry != null && imageSize > 0) {
			if (currentImageIndex >= 0 && currentImageIndex < imageSize) {
				Bitmap image = null;
				String imagePath = readFileName + "/"
						+ imageNameList.get(currentImageIndex);

				try {
					image = BitmapTool
							.getBitmap(
									getApplicationContext(),
									imagePath,
									ScreenTool
											.getScreenPix(getApplicationContext()).widthPixels,
									ScreenTool
											.getScreenPix(getApplicationContext()).heightPixels);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				ImageInfo imageInfo = new ImageInfo();
				imageInfo.setStepCount(2);
				imageInfo.setImageUrl("");

				ShareImageInfoEntry entry = new ShareImageInfoEntry(imageSize,
						image, imageNameList.get(currentImageIndex), imagePath);
				entry.setImageInfo(imageInfo);
				return entry;
			}
		}
		return new ShareImageInfoEntry(imageInfoEntry.getImageCount());
	}

	@Override
	public void onBackPressed() {
		// 内存回收
		// TODO
		// ShareImageInfoEntryCacheManager.INSTANCE.clean();
		super.onBackPressed();
	}

	/**
	 * 获取阅读文件路径信息
	 */
	public static String getFilePath(int currentImageIndex) {
		return readFileName + "/" + imageNameList.get(currentImageIndex);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (myImageView.isCanTouch()) {
			toggleMenu();
		}
		return false;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("image index", currentImageIndex);
		outState.putInt("story line index", myImageView.getStoryLineIndex());
	}

}
