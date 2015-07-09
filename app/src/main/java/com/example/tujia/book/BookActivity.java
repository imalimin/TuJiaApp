package com.example.tujia.book;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.tujia.R;

public class BookActivity extends Activity {

	private ScrollView scrollView;
	private TextView textTitleView;
	private TextView textBodyView;
	private String string, filepath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book);

		initView();
	}

	private void initView() {
		scrollView = (ScrollView) findViewById(R.id.text_scroll);
		textTitleView = (TextView) findViewById(R.id.text_title);
		textBodyView = (TextView) findViewById(R.id.text_body);

		Bundle bundle = getIntent().getExtras();
		// String filePath = bundle.getString("FILE");

		int position = bundle.getInt("position");
		switch (position % 4) {
		case 0:
			textTitleView.setText("廪君传");
			string = getString(R.string.bingjunchuan);
			textBodyView.setText(string.substring(string.indexOf("》") + 1));
			filepath = "bingjunzhuan.txt";
			break;
		case 1:
			textTitleView.setText("好吃包");
			string = getString(R.string.haochibao);
			textBodyView.setText(string.substring(string.indexOf("》") + 1));
			filepath = "haochibao.txt";
			break;
		case 2:
			textTitleView.setText("熊娘家婆");
			string = getString(R.string.xiongniangjiapo);
			textBodyView.setText(string.substring(string.indexOf("》") + 1));
			filepath = "xiongliangjiapo.txt";
			break;
		case 3:
			textTitleView.setText("行孝得宝");
			String string = getString(R.string.xiangxiaodebao);
			textBodyView.setText(string.substring(string.indexOf("》") + 1));
			filepath = "xingxiaodebao.txt";
			break;
		default:
			filepath = "";
			break;
		}

		BookFactory bookFactory = null;
		try {
			bookFactory = new BookFactory(filepath, "GB2312",
					getApplicationContext());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// textTitleView.setText(bookFactory.getTitle());
		string = bookFactory.getTextBody();
		textBodyView.setText(string.substring(string.indexOf("\n") + 1));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

}
