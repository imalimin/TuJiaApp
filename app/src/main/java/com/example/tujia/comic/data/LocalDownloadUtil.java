package com.example.tujia.comic.data;

import android.os.Environment;

public class LocalDownloadUtil {
	public static final String SD_SAVE_PATH = Environment.getExternalStorageDirectory().getPath() + "/TuJia/cartonn_download/";	// 下载文件保存位置
	public static final String READ_SAVE_PATH = Environment.getExternalStorageDirectory().getPath() + "/TuJia/cartonn_files/";	// 阅读\解压位置
	
	public static final String INTERNAL_SAVE_PATH = "/data/data/com.qq.ac.android/share_download/" ;		// 内存下载文件保存位置
	public static final String INTERNAL_READ_SAVE_PATH = "/data/data/com.qq.ac.android/share_read/" ;	// 内存下载文件保存位置
	
	public static final String SHARE_DOWNLOAD_DIR = "share_download/";	
	public static final String SHARE_READ_DIR = "share_read/";
	
	public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getPath() + "/qqcomic/";
	
	public static final String META_FORMAT = ".zip";
	public static final int PORT_1 = 8086;
	public static final int PORT_2 = 8085;
	
	public static String getRootDir() {
		return ROOT_DIR;
	}
	public static String getSdSavePath() {
		return SD_SAVE_PATH;
	}
	public static String getReadSavePath() {
		return READ_SAVE_PATH;
	}
	public static String getInternalSavePath() {
		return INTERNAL_SAVE_PATH;
	}
	public static String getInternalReadSavePath() {
		return INTERNAL_READ_SAVE_PATH;
	}
	
	/**
	 * 获取文件保存路径
	 * @param chapter
	 * @return
	 */
	public static String getFileSavePath(final LocalDownloadChapter chapter) {
		if (chapter.getSaveLocation() == LocalDownloadChapter.FileSaveLocation.SD_CARD) {
			return SD_SAVE_PATH;
		} else {
			return INTERNAL_SAVE_PATH;
		}
	}
	
	public static String getChapterFolder(LocalDownloadChapter chapter) {
		if (chapter.getSaveLocation() == LocalDownloadChapter.FileSaveLocation.SD_CARD) {
			return SD_SAVE_PATH + chapter.getDetailId().getFileFullName();
		} else {
			return INTERNAL_SAVE_PATH + chapter.getDetailId().getFileFullName();
		}
	}
	
	public static String getUnzipChapterFolder(LocalDownloadChapter chapter) {
		if (chapter.getSaveLocation() == LocalDownloadChapter.FileSaveLocation.SD_CARD) {
			return READ_SAVE_PATH + chapter.getDetailId().getFileName() + "/";
		} else {
			return INTERNAL_READ_SAVE_PATH + chapter.getDetailId().getFileName() + "/";
		}
	}
	
	public static String getReadFolder(LocalDownloadChapter chapter) {
		if (chapter.getSaveLocation() == LocalDownloadChapter.FileSaveLocation.SD_CARD) {
			return READ_SAVE_PATH + chapter.getDetailId().getFileName();
		} else {
			return INTERNAL_READ_SAVE_PATH + chapter.getDetailId().getFileName();
		}
	}
	
	// TODO
	public static String getChapterRootFolder(LocalDownloadChapter chapter) {
		if (chapter.getSaveLocation() == LocalDownloadChapter.FileSaveLocation.SD_CARD) {
			return ROOT_DIR;
		} else {
			return INTERNAL_READ_SAVE_PATH + chapter.getDetailId().getFileName();
		}
	}
}
