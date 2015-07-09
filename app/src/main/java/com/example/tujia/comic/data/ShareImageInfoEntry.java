package com.example.tujia.comic.data;

import com.example.tujia.comic.data.ComicDetail.ImageInfo;

import android.graphics.Bitmap;

public class ShareImageInfoEntry {
	private int imageCount;
	private Bitmap bitmap;
	private ImageInfo imageInfo;
	private String imagePath;
	private String imageFullName;

	public ShareImageInfoEntry(int imageCount, Bitmap bitmap, String imageFullName, String imagePath) {
		super();
		this.imageCount = imageCount;
		this.bitmap = bitmap;
		this.imageFullName = imageFullName;
		this.imagePath = imagePath;
	}
	
	public ShareImageInfoEntry(Bitmap bitmap, String imagePath) {
		this.bitmap = bitmap;
		this.imagePath = imagePath;
	}

	public String getImageFullName() {
		return imageFullName;
	}

	public void setImageFullName(String imageFullName) {
		this.imageFullName = imageFullName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public ShareImageInfoEntry(int imageCount) {
		this.imageCount = imageCount;
	}

	public int getImageCount() {
		return imageCount;
	}

	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public ImageInfo getImageInfo() {
		return imageInfo;
	}

	public void setImageInfo(ImageInfo imageInfo) {
		this.imageInfo = imageInfo;
	}
}
