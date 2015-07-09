package com.example.tujia.comic.data;

import java.io.Serializable;

public class LocalDownloadChapter implements Serializable {
	private static final long serialVersionUID = 1L;

	public static interface LocalDownStatus {
		public final static int DOWNLOADING = 0;
		public final static int PAUSE = DOWNLOADING + 1; // 暂停
		public final static int DOWNLOADED = PAUSE + 1;	 // 下载完成
		public final static int CAN_NOT_CONNECT = DOWNLOADED + 1; // 无法连接:pc端关闭或索引发生变化
		public final static int UNZIP = CAN_NOT_CONNECT + 1; // 解压
		public final static int DOWNLOAD_FAILED = UNZIP + 1; // 下载失败:SD卡容量不足
		public final static int UPZIP_FAILED = DOWNLOAD_FAILED + 1; // 解压失败 
	}

	public static interface FileSaveLocation {
		public final static int SD_CARD = 0;
		public final static int INTERNAL_STORAGE = SD_CARD + 1;
	}

	public LocalDownDetailId detailId;

	public long totalLength; // 文件总长度
	public long downloadedLength; // 断点位置/已下载位置
	public int status;
	public String createTime;
	public int saveLocation;
	private boolean isNeedReconnect;

	public LocalDownloadChapter() {
		this.isNeedReconnect = true;
	}

	public LocalDownloadChapter(LocalDownDetailId detailId, int status,
			String createTime) {
		this.detailId = detailId;
		this.status = status;
		this.totalLength = 0;
		this.downloadedLength = 0;
		this.createTime = createTime;
		this.isNeedReconnect = true;
	}
	
	public LocalDownloadChapter(LocalDownDetailId detailId, int status, long totalLength) {
		this.detailId = detailId;
		this.status = status;
		this.totalLength = totalLength;
		this.downloadedLength = totalLength;
		this.isNeedReconnect = true;
	}

	public LocalDownloadChapter(LocalDownDetailId detailId, int status, long totalLength, int saveLocation) {
		this.detailId = detailId;
		this.status = status;
		this.totalLength = totalLength;
		this.downloadedLength = totalLength;
		this.saveLocation = saveLocation;
		this.isNeedReconnect = true;
	}

	public LocalDownloadChapter(LocalDownDetailId detailId, long totalLength,
			long downloadedLength, int status, int saveLocation) {
		super();
		this.detailId = detailId;
		this.totalLength = totalLength;
		this.downloadedLength = downloadedLength;
		this.status = status;
		this.saveLocation = saveLocation;
		this.isNeedReconnect = true;
	}

	public LocalDownloadChapter(LocalDownDetailId detailId) {
		this.detailId = detailId;
		this.isNeedReconnect = true;
	}

	public LocalDownDetailId getDetailId() {
		return detailId;
	}

	public void setDetailId(LocalDownDetailId detailId) {
		this.detailId = detailId;
	}

	public long getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}

	public long getDownloadedLength() {
		return downloadedLength;
	}

	public void setDownloadedLength(long downloadedLength) {
		this.downloadedLength = downloadedLength;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getSaveLocation() {
		return saveLocation;
	}

	public void setSaveLocation(int saveLocation) {
		this.saveLocation = saveLocation;
	}

	public boolean isNeedReconnect() {
		return isNeedReconnect;
	}

	public void setNeedReconnect(boolean isNeedReconnect) {
		this.isNeedReconnect = isNeedReconnect;
	}
}
