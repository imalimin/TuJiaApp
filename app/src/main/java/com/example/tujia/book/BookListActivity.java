package com.example.tujia.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.tujia.R;

public class BookListActivity extends Activity {

	private ListView bookList;
	private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booklist);
		initView();
	}

	private void initView() {
		this.findViewById(R.id.book_list_title_backbtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(R.anim.open_main, R.anim.close_next);
					}
				});

		bookList = (ListView) findViewById(R.id.book_list);

		fillList();

	}

	private void fillList() {

		for (int i = 0; i < 4; i++) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("TITLE", "title_" + i);
			hashMap.put("FILE", "/sdcard/testone.txt");
			list.add(hashMap);
		}

		LoaderItemAdapter listAdapter = new LoaderItemAdapter(
				BookListActivity.this, list);
		bookList.setAdapter(listAdapter);

		bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				itemClickListenerHandler(position, list);
			}
		});
	}

	private void itemClickListenerHandler(int position,
			List<HashMap<String, String>> list) {
		Intent intent = new Intent(BookListActivity.this, BookActivity.class);
		Bundle bundle = new Bundle();

		bundle.putString("FILE", list.get(position).get("FILE"));
		bundle.putInt("position", position);

		intent.putExtras(bundle);
		startActivity(intent);
		overridePendingTransition(R.anim.open_next, R.anim.close_main);

	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
	}

}
