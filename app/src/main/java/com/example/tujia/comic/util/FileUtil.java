package com.example.tujia.comic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;

/**
 * Utility for file operation.
 *
 * @author wlei 2012-5-12
 */
public class FileUtil implements Comparator<File> {

  private static final String OLD_FOLDER_APP = ".qqac/";
  private static final String FOLDER_APP = ".qqcomic/";
  private static final String CRASH_FILE_PATH = Environment.getExternalStorageDirectory()
      + "/.qqcomic/QQComicCrashInfos";
  private static final String CRASH_FILE_UPLOAD_URL = "http://mobilev3.ac.qq.com/Crash/crashLog/";

  private static String sdCardPath;
  private static String appFolderPath;
  private static long sdCardFreeSize;
  
  private static String[] stackTraceFileList = null;
  
  public static void cleanOldVersionFolder() {
    deleteFolder(sdCardPath + OLD_FOLDER_APP);
  }

  /**
   * Creates a cache folder for application.
   *
   * @see #FOLDER_APP
   */
  private final static void createAppFolder() {
    final String status = Environment.getExternalStorageState();
    if (!status.equals(Environment.MEDIA_MOUNTED)) {
      return;
    }

    final File fileSDCardDir = Environment.getExternalStorageDirectory();
    sdCardPath =
        fileSDCardDir.getParent() + File.separator + fileSDCardDir.getName() + File.separator;
    appFolderPath = sdCardPath + FOLDER_APP;
    final File AppFolderPath = new File(appFolderPath);
    if (!AppFolderPath.exists()) {
      AppFolderPath.mkdirs();
    }
  }

  /**
   * Returns path of application folder on SDCard.
   */
  public final static String getAppFolderPath() {
    return appFolderPath;
  }

  /**
   * Returns <code>true</code> if SD card is mounted. If {@link #APP_FOLDER} has never created
   * before, it will be also created.
   */
  public final static boolean isSDCardReady() {
    final String status = Environment.getExternalStorageState();
    if (status.equals(Environment.MEDIA_MOUNTED)) {
      if (appFolderPath == null) {
        createAppFolder();
      }

      final File sdCardDir = Environment.getExternalStorageDirectory();
      final StatFs sf = new StatFs(sdCardDir.getPath());
      final long blockSize = sf.getBlockSize();
      final long availCount = sf.getAvailableBlocks();

      sdCardFreeSize = availCount * blockSize / 1024;
      return true;
    }
    sdCardFreeSize = 0;
    return false;
  }

  /**
   * KB
   */
  public final static long getSDCardFreeSize() {
    return sdCardFreeSize;
  }

  /**
   * "/sdcard"
   */
  public final static String getSDCardPath() {
    return sdCardPath;
  }

  /**
   * Deletes a folder.
   */
  public static void deleteFolder(String folderPath) {
    try {
      deleteFolderFiles(folderPath);
      String filePath = folderPath;
      filePath = filePath.toString();
      final File myFilePath = new File(filePath);
      myFilePath.delete();
    } catch (final Exception e) {
    }
  }

  /**
   * Deletes all files in the specified folder.
   */
  public static void deleteFolderFiles(String folderPath) {
    if (folderPath == null) {
      return;
    }

    final File file = new File(folderPath);
    if (!file.exists()) {
      return;
    }
    if (!file.isDirectory()) {
      return;
    }
    final String[] tempList = file.list();
    File temp = null;
    for (int i = 0; i < tempList.length; i++) {
      if (folderPath.endsWith(File.separator)) {
        temp = new File(folderPath + tempList[i]);
      } else {
        temp = new File(folderPath + File.separator + tempList[i]);
      }

      if (temp.isFile()) {
        temp.delete();
      }

      if (temp.isDirectory()) {
        deleteFolderFiles(folderPath + "/" + tempList[i]);
        deleteFolder(folderPath + "/" + tempList[i]);
      }
    }
  }

  /**
   * Deletes a file.
   */
  public static void deleteFile(String filePath) {
    if (filePath == null) {
      return;
    }

    final File file = new File(filePath);
    if (!file.exists()) {
      return;
    }
    File temp = null;
    temp = new File(filePath);

    if (temp.isFile()) {
      temp.delete();
    }
  }
  
  public static void copyfile(File fromFile, File toFile, boolean rewrite, boolean deleteSrc)
  {
    if (!fromFile.exists()) {
      return;
    }
    if (!fromFile.isFile()) {
      return;
    }
    if (!fromFile.canRead()) {
      return;
    }
    if (!toFile.getParentFile().exists()) {
      toFile.getParentFile().mkdirs();
    }
    if (toFile.exists() && rewrite) {
      toFile.delete();
    }
    try {
      FileInputStream fosfrom = new FileInputStream(fromFile);
      FileOutputStream fosto = new FileOutputStream(toFile);
      byte bt[] = new byte[1024];
      int c;
      while ((c = fosfrom.read(bt)) > 0) {
        fosto.write(bt, 0, c); // 将内容写到新文件当中
      }
      fosfrom.close();
      fosto.close();
      if (deleteSrc) {
        fromFile.delete();
      }
    } catch (Exception ex) {
//      Log.e("readfile", ex.getMessage());
    }
  }
  
//文件大小转换
 public static String formetFileSize(long fileS) {
   DecimalFormat df = new DecimalFormat("#.00");
   String fileSizeString = "";
   if (fileS < 1024) {
     fileSizeString = df.format((double) fileS) + "B";
   } else if (fileS < 1048576) {
     fileSizeString = df.format((double) fileS / 1024) + "K";
   } else if (fileS < 1073741824) {
     fileSizeString = df.format((double) fileS / 1048576) + "M";
   } else {
     fileSizeString = df.format((double) fileS / 1073741824) + "G";
   }
   if (fileS == 0) {
	 return "0B";
   }
   return fileSizeString;
 }

 // 对文件进行排序
 public static void sortFile(File[] currentFiles) {
   if (currentFiles != null && currentFiles.length != 0)
     Arrays.sort(currentFiles, new FileUtil());
 }

 public int compare(File lhs, File rhs) {
   // 对中文名的文件排序
   Comparator<Object> cmp = Collator.getInstance(java.util.Locale.CHINA);
   return cmp.compare(lhs.getName(), rhs.getName());
 }
 
  // 读文件
  public static String readSDFile(String fileName) throws IOException {
    
    File file = new File(fileName);
    if(!file.exists())
      file.createNewFile();
    
    FileInputStream fis;
    String res;
    try {
      fis = new FileInputStream(file);
      int length = fis.available();
      byte[] buffer = new byte[length];
      fis.read(buffer);
      res = EncodingUtils.getString(buffer, "UTF-8");
      fis.close();
      return res;
    } catch (FileNotFoundException e) {
      return null;
    } catch (IOException e) {
      return null;
    }
  }

  // 写文件
  public static void writeSDFile(String fileName, String write_str) throws IOException {
    File file = new File(fileName);
    if(!file.exists())
      file.createNewFile();
    
    FileOutputStream fos;
    try {
      fos = new FileOutputStream(file);
      byte[] bytes = write_str.getBytes();
      fos.write(bytes);
      fos.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static File creatSDDir(String dirName) {
    File dir = new File(dirName);
    dir.mkdir();
    return dir;
  }

  public static File creatSDFile(String fileName) throws IOException {
    File file = new File(fileName);
    file.createNewFile();
    return file;
  }

  public static File write2SDFromInput(String path, String fileName, InputStream input) {
    File file = null;
    OutputStream output = null;
    try {
      creatSDDir(path);
      file = creatSDFile(path + File.separator + fileName);
      output = new FileOutputStream(file);
      byte buffer[] = new byte[2 * 1024];
      int read = input.read(buffer);
      while (read != -1) {
        output.write(buffer, 0, read);
        read = input.read(buffer);
      }
      output.flush();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        output.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return file;
  }

  public static Drawable readFromSD2Output(String filePath) {
    String path = filePath;
    File file = new File(path);
    Drawable drawable = null;
    drawable = Drawable.createFromPath(file.toString());
    return drawable;
  }

//得到网上下载的图片数据流
  public static InputStream getInputStream(String url)
  {
    URL m;
    InputStream i = null;
    try {
      m = new URL(url);
      i = (InputStream) m.getContent();
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return i;
  }
  
  /**
   * Search for stack trace files.
   * @return
   */
  private static String[] searchForStackTraces() {
    if ( stackTraceFileList != null ) {
      return stackTraceFileList;
    }
    File dir = new File(CRASH_FILE_PATH + "/");
    // Try to create the files folder if it doesn't exist
    dir.mkdir();
    // Filter for ".stacktrace" files
    FilenameFilter filter = new FilenameFilter() { 
      public boolean accept(File dir, String name) {
        return name.endsWith(".log"); 
      } 
    }; 
    return (stackTraceFileList = dir.list(filter)); 
  }
  
  /**
   * Look into the files folder to see if there are any "*.stacktrace" files.
   * If any are present, submit them to the trace server.
   */
  public static void submitStackTraces() {
    try {
      String[] list = searchForStackTraces();
      if (list != null && list.length > 0) {
        for (int i = 0; i < list.length; i++) {
          String filePath = CRASH_FILE_PATH + "/" + list[i];
          // Extract the version from the filename: "packagename-version-...."
          String version = list[i].split("-")[0];
          // Read contents of stacktrace
          StringBuilder contents = new StringBuilder();
          BufferedReader input = new BufferedReader(new FileReader(filePath));
          String line = null;
          String androidVersion = null;
          String phoneModel = null;
          while ((line = input.readLine()) != null) {
            if (androidVersion == null) {
              androidVersion = line;
              continue;
            } else if (phoneModel == null) {
              phoneModel = line;
              continue;
            }
            contents.append(line);
            contents.append(System.getProperty("line.separator"));
          }
          input.close();
          String stacktrace;
          stacktrace = contents.toString();
          // Transmit stack trace with POST request
          DefaultHttpClient httpClient = new DefaultHttpClient();
          HttpPost httpPost = new HttpPost(CRASH_FILE_UPLOAD_URL);
          List<NameValuePair> nvps = new ArrayList<NameValuePair>();
          nvps.add(new BasicNameValuePair("local_version", DeviceManager.getInstance().getVersionName()));
          nvps.add(new BasicNameValuePair("crash_file_name", list[i]));
          nvps.add(new BasicNameValuePair("crash_file_content", contents.toString()));
          httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
          // We don't care about the response, so we just hope it went well and on with it
          HttpResponse response = httpClient.execute(httpPost);
          HttpEntity entity = response.getEntity();    
          if (entity != null) {    
              InputStream instreams = entity.getContent();    
              String str = convertStreamToString(instreams);  
              //System.out.println("Do something");   
              //System.out.println(str);  
              // Do not need the rest    
              httpPost.abort();    
          }  
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        String[] list = searchForStackTraces();
        for (int i = 0; i < list.length; i++) {
          File file = new File(CRASH_FILE_PATH + "/" + list[i]);
          file.delete();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public static String convertStreamToString(InputStream is) {      
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));      
    StringBuilder sb = new StringBuilder();      
   
    String line = null;      
    try {      
        while ((line = reader.readLine()) != null) {  
            sb.append(line + "\n");      
        }      
    } catch (IOException e) {      
        e.printStackTrace();      
    } finally {      
        try {      
            is.close();      
        } catch (IOException e) {      
           e.printStackTrace();      
        }      
    }      
    return sb.toString();      
}  
}
