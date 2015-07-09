package com.example.tujia.comic.data;

import java.io.Serializable;

public class LocalDownDetailId implements Serializable {
	private static final long serialVersionUID = 1L;
	private String ip;
	private int port;
	private String downloadFileName;// 下载时使用的文件名称
	private String fileName;		// 文件名称，保存到数据库中的名称，不包含文件格式
	private String fileFullName;	// 包含格式
	private String filePath;	
	private String md5;
	
	public LocalDownDetailId(String fileName) {
		this.fileName = fileName;
	}
	
	public LocalDownDetailId(String fileName, String md5) {
		this.fileName = fileName;
		this.md5 = md5;
	}
	
	public LocalDownDetailId(String ip, int port, String downloadFileName, String fileName, String md5) {
		super();
		this.ip = ip;
		this.port = port;
		this.downloadFileName = downloadFileName;
		this.fileName = fileName;
		this.fileFullName = fileName + LocalDownloadUtil.META_FORMAT;
		this.md5 = md5;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileFullName() {
		return fileFullName;
	}
	public void setFileFullName(String fileFullName) {
		this.fileFullName = fileFullName;
	}

	public String getDownloadFileName() {
		return downloadFileName;
	}

	public void setDownloadFileName(String downloadFileName) {
		this.downloadFileName = downloadFileName;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
