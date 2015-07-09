package com.example.tujia.book;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class BookFactory {

	private String path;
	private String encoding;
	private String textBody;
	private String title;

	public BookFactory(String path, String encoding, Context context)
			throws IOException {
		this.path = path;
		this.encoding = encoding;
		this.textBody = "";
		this.title = "";
		InputStream is = context.getAssets().open(path);
		int size = is.available();
		byte[] buffer = new byte[size];
		is.read(buffer);
		is.close();
		textBody = new String(buffer, "GB2312");

		// File file = new File(path);
		// FileInputStream fis = null;
		// if (file.exists()) {
		// try {
		// fis = new FileInputStream(file);
		// byte[] readBytes = new byte[fis.available()];
		// while (fis.read(readBytes) != -1 && fis.available() != 0) {
		// }
		// this.textBody = new String(readBytes, encoding);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// this.title = file.getName();
		// }
	}

	public String getTextBody() {
		return textBody;
	}

	public String getTitle() {
		return title;
	}

	private String readStream(InputStream is) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int i = is.read();
			while (i != -1) {
				bo.write(i);
				i = is.read();
			}
			return bo.toString();
		} catch (IOException e) {
			return "";
		}
	}
}
