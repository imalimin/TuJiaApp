package com.example.tujia.comic.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.google.common.collect.Lists;

public class FileUtil2 {
	public static boolean unZip(String zipFile, String targetDir) {
		boolean isSuccess = true;
		FileInputStream fis = null;
		ZipInputStream zis = null;

		try {
			int buffer = 1024;
			String strEntry;
			BufferedOutputStream dest = null;
			fis = new FileInputStream(zipFile);
			zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				byte data[] = new byte[buffer];
				strEntry = entry.getName();
				File entryFile = new File(targetDir + strEntry);
				File entryDir = new File(entryFile.getParent());
				if (!entryDir.exists()) {
					entryDir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(entryFile);
				dest = new BufferedOutputStream(fos, buffer);
				int count;
				while ((count = zis.read(data, 0, buffer)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
		} catch (Exception e) {
			isSuccess = false;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zis != null) {
				try {
					zis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return isSuccess;
	}

	public static boolean unZip2(String zipFile, String unZipDir) {
		File f = new File(unZipDir);
		if (!f.exists()) {
			f.mkdirs();
		}

		BufferedInputStream is = null;
		ZipEntry entry;

		try {
			ZipFile zipfile = new ZipFile(zipFile);
			Enumeration<?> enumeration = zipfile.entries();
			byte data[] = new byte[1024];
			while (enumeration.hasMoreElements()) {
				entry = (ZipEntry) enumeration.nextElement();
				if (entry.isDirectory()) {
					StringBuilder dir = new StringBuilder(unZipDir);
					dir.append("/");
					dir.append(entry.getName());
					File f1 = new File(dir.toString());
					if (!f1.exists()) {
						f1.mkdirs();
					}
				} else {
					is = new BufferedInputStream(zipfile.getInputStream(entry));
					int count;
					StringBuilder imageName = new StringBuilder(unZipDir);
					imageName.append("/").append(entry.getName());
					RandomAccessFile m_randFile = null;
					File file = new File(imageName.toString());
					if (file.exists()) {
						file.delete();
					}

					file.createNewFile();
					m_randFile = new RandomAccessFile(file, "rw");
					int begin = 0;

					while ((count = is.read(data, 0, 1024)) != -1) {
						try {
							m_randFile.seek(begin);
						} catch (Exception ex) {
						}

						m_randFile.write(data, 0, count);
						begin = begin + count;
					}
					// file.delete();
					m_randFile.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		return true;
	}

	public static ArrayList<String> readLines(InputStream is) {
		ArrayList<String> data = new ArrayList<String>();

		InputStreamReader isr;
		try {
			isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr, 2048);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if ("".equals(line))
					continue; // remove blank line
				data.add(line);
			}

			isr.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public static String readTextFile(File file) {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			reader.close();
		} catch (Exception e) {
		}
		String s = builder.toString();
		builder.setLength(0);
		builder = null;
		return s;
	}

	/**
	 * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @param context
	 * @param msg
	 */
	public static void write(Context context, String fileName, String content) {
		if (content == null)
			content = "";

		try {
			FileOutputStream fos = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			fos.write(content.getBytes());

			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文本文件
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String read(Context context, String fileName) {
		try {
			FileInputStream in = context.openFileInput(fileName);
			return readInStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String readInStream(FileInputStream inStream) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			outStream.close();
			inStream.close();
			return outStream.toString();
		} catch (IOException e) {
			Log.i("FileTest", e.getMessage());
		}
		return null;
	}

	// 创建
	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName + fileName);
	}

	/**
	 * 向手机写图片
	 * 
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public static boolean writeFile(byte[] buffer, String folder,
			String fileName) {
		boolean writeSucc = false;

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);

		String folderPath = "";
		if (sdCardExist) {
			folderPath = Environment.getExternalStorageDirectory()
					+ File.separator + folder + File.separator;
		} else {
			writeSucc = false;
		}

		File fileDir = new File(folderPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File file = new File(folderPath + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(buffer);
			writeSucc = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return writeSucc;
	}

	/**
	 * 根据文件目录，获取该目录下所有文件名，包含格式名
	 * 
	 * @throws java.io.IOException
	 */
	public static List<String> getDirFileNames(String dirPath, Context context)
			throws IOException {
		List<String> nameList = Lists.newArrayList();
		String[] name;

		name = context.getAssets().list(dirPath);
		for (int i = 0; i < name.length; i++) {
			nameList.add(name[i]);
			Log.v("name[i]", name[i]);
		}

		// File dir = new File(dirPath);
		// if (dir != null && dir.isDirectory()) {
		// File[] files = dir.listFiles();
		// for (File file : files) {
		// if (file.isFile()) {
		// nameList.add(file.getName());
		// }
		// }
		// }

		Log.v("imagesize", nameList.size() + "");
		return nameList;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (StringUtils.isEmpty(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return "";
		}
		int point = filePath.lastIndexOf('.');
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
				point);
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		if (StringUtils.isEmpty(fileName))
			return "";

		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * 获取文件大小
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param size
	 *            字节
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024 / 1024) + "G";
		} else {
			return df.format(temp / 1024) + "M";
		}
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 转换文件大小,无单位
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize2(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.0");
		return df.format((double) fileS / 1048576);
	}

	/**
	 * 转换文件大小,无单位
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static int formatFileSize3(long fileS) {
		return (int) fileS / 10240;
	}

	/**
	 * 获取目录文件大小
	 * 
	 * @param dir
	 * @return
	 */
	public static int getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		int dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 获取目录文件个数
	 * 
	 * @param f
	 * @return
	 */
	public long getFileList(File dir) {
		long count = 0;
		File[] files = dir.listFiles();
		count = files.length;
		for (File file : files) {
			if (file.isDirectory()) {
				count = count + getFileList(file);// 递归
				count--;
			}
		}
		return count;
	}

	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) {
			out.write(ch);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkFileExists(String path, String name) {
		boolean status;
		if (!name.equals("")) {
			File file = new File(path + name);
			status = file.exists();
		} else {
			status = false;
		}
		return status;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkFileExists(String filePath) {
		boolean status;
		if (!filePath.equals("")) {
			File file = new File(filePath);
			status = file.exists();
		} else {
			status = false;
		}
		return status;
	}

	/**
	 * 获取SD卡总空间大小
	 * 
	 * @return
	 */
	public static long getDiskSpace() {
		String status = Environment.getExternalStorageState();
		long nSDTotalSize = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				// 取得sdcard文件路径
				File pathFile = Environment
						.getExternalStorageDirectory();
				StatFs statfs = new StatFs(
						pathFile.getPath());
				long nTotalBlocks = statfs.getBlockCount();
				long nBlocSize = statfs.getBlockSize();
				nSDTotalSize = nTotalBlocks * nBlocSize;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return nSDTotalSize;
	}

	/**
	 * 计算SD卡的剩余空间
	 * 
	 * @return 返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 新建目录
	 * 
	 * @param directoryName
	 * @return
	 */
	public static boolean createDirectory(String directoryName) {
		boolean status;
		if (!directoryName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);
			status = newPath.mkdir();
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 检查是否安装SD卡
	 * 
	 * @return
	 */
	public static boolean checkSaveLocationExists() {
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status;
		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteDirectory(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then
				// delete the directory
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + "/"
								+ listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					Log.i("DirectoryManager deleteDirectory", fileName);
					status = true;
				} catch (Exception e) {
					e.printStackTrace();
					status = false;
				}

			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String filePath) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (filePath.equals("")) {
			File newPath = new File(filePath);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile()) {
				try {
					Log.i("DirectoryManager deleteFile", filePath);
					newPath.delete();
					status = true;
				} catch (SecurityException se) {
					se.printStackTrace();
					status = false;
				}
			} else
				status = false;
		} else
			status = false;
		return status;
	}

	// 复制文件
	public static void copyFile(InputStream srcFileStream, File destFile)
			throws IOException {
		InputStream fis = srcFileStream;
		FileOutputStream fos = null;
		boolean success = false;
		try {
			File parentFile = destFile.getAbsoluteFile().getParentFile();
			if ((parentFile != null) && (!parentFile.exists())) {
				makeDirs(parentFile);
			}

			fos = new FileOutputStream(destFile);
			int readCount;
			byte[] buffer = new byte[1024];
			while ((readCount = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, readCount);
			}
			closeStream(fis);
			closeStream(fos);
			success = true;
		} catch (IOException e) {
			throw e;
		} finally {
			closeStream(fis);
			closeStream(fos);
			if (!success) {
				destFile.delete();
			}
		}
	}

	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	public static void makeDirs(File dir) throws IOException {
		if (dir.exists()) {
			return;
		}
		boolean success = dir.mkdirs();
		if (!success) {
			throw new IOException("cannot create folder "
					+ dir.getAbsolutePath());
		}
	}

	public static String getFileExtension(String filename) {
		int lastDotIndex = filename.lastIndexOf(".");
		if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
			return null;
		}
		return filename.substring(lastDotIndex + 1);
	}

	public static String getFilePureName(String filename) {
		if (StringUtils.isEmpty(filename))
			return null;
		int lastDotIndex = filename.lastIndexOf(".");
		if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
			return null;
		}
		return filename.substring(0, lastDotIndex);
	}

	public static String getFilePureNameWithFixedExt(String filename, String ext) {
		if (StringUtils.isEmpty(filename))
			return null;
		if (StringUtils.isEmpty(ext))
			return getFilePureName(filename);

		int lastDotIndex = filename.lastIndexOf("." + ext);
		if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
			return null;
		}
		return filename.substring(0, lastDotIndex);
	}

	public static void delete(String path) {
		delete(new File(path));
	}

	public static void delete(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				delete(f);
			}
		}
		if (file.exists()) {
			boolean deleteFlag = file.delete();
			if (!deleteFlag) {
				// Logger.e(TAG, "delete " + file.getAbsolutePath() + " : " +
				// deleteFlag);
			}
		}
	}

	public static void deleteFilesInDir(String path) {
		deleteFilesInDir(new File(path));
	}

	public static void deleteFilesInDir(File file) {
		File[] files = file.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			delete(f);
		}
	}

	public static void createFile(String filename) throws IOException {
		File file = new File(filename);
		File dir = file.getParentFile();
		if (dir != null) {
			dir.mkdirs();
		}
		file.createNewFile();
	}

}
