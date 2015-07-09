package com.example.tujia.manhua;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tujia.comic.reader.ReadingActivity;
import com.example.tujia.main.MainActivity;
import com.example.tujia.R;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class ManHuaActivity extends Activity {
	private ListView listview;

	// private ArrayList<CartoonListItem> mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cartoon);
		intiview1();
	}

	private void intiview1() {
		listview = (ListView) findViewById(R.id.cartoon_list);

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			list.add("item" + i);
		}

		CartoonAdapter adapter = new CartoonAdapter(list);

		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		swingBottomInAnimationAdapter.setListView(listview);

		listview.setAdapter(swingBottomInAnimationAdapter);

		((TextView) this.findViewById(R.id.book_list_title_text)).setText("漫画");
		this.findViewById(R.id.book_list_title_backbtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(R.anim.open_main,
								R.anim.close_next);
					}
				});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent _intent = new Intent(ManHuaActivity.this,
						ReadingActivity.class);
				String file_name = "";
				int position = arg2 % 3;
				if(position == 0){
					file_name = "shaonianyingyong";
				}
				else if(position ==1){
					file_name = "baihuzhuanshi";
				}
				else if(position == 2){
					file_name = "wuzujiemeng";
				}

				_intent.putExtra("FILE_NAME", file_name);
				startActivity(_intent);
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
			}
		});
	}

	private ArrayList<Integer> getItems() {
		ArrayList<Integer> items = new ArrayList<Integer>();
		for (int i = 0; i < 3; i++) {
			items.add(i);
		}
		return items;
	}

	private class CartoonAdapter extends BaseAdapter {
		private List<String> data;

		public CartoonAdapter(List<String> list) {
			this.data = list;
		}

		@Override
		public int getCount() {
			System.out.println("data.size(): " + data.size());
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(getApplicationContext()).inflate(
						R.layout.view_cartoonlistitem, parent, false);

				viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) view
						.findViewById(R.id.cartoon_item_title);
				view.setTag(viewHolder);
				viewHolder.imageView = (ImageView) view
						.findViewById(R.id.cartoon_item_image);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			setImageView(viewHolder, position);
			return view;
		}

	}

	private void setImageView(ViewHolder viewHolder, int position) {
		int imageResId;
		switch (position % 3) {
		case 0:
			imageResId = R.drawable.comic_bg_shaonianyingxiong;
			viewHolder.textView.setText("少年英雄");
			break;
		case 1:
			imageResId = R.drawable.comic_bg_baihuzhuanshi;
			viewHolder.textView.setText("白虎转世");
			break;
		case 2:
			imageResId = R.drawable.comic_bg_wuzutongmeng;
			viewHolder.textView.setText("五族结盟");
			break;
		default:
			imageResId = R.drawable.comic_bg_shaonianyingxiong;
		}

		viewHolder.imageView.setImageResource(imageResId);
	}

	private static class ViewHolder {
		TextView textView;
		ImageView imageView;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

}
