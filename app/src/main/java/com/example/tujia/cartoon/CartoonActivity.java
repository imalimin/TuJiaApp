package com.example.tujia.cartoon;

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
import com.example.tujia.R;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class CartoonActivity extends Activity {
	private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cartoon);
		intiview1();
	}

	private void intiview1() {
		listview = (ListView) findViewById(R.id.cartoon_list);

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
			list.add("item" + i);
		}

		CartoonAdapter adapter = new CartoonAdapter(list);

		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		swingBottomInAnimationAdapter.setListView(listview);

		listview.setAdapter(swingBottomInAnimationAdapter);

		((TextView) this.findViewById(R.id.book_list_title_text)).setText("动漫");
		this.findViewById(R.id.book_list_title_backbtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(R.anim.open_main, R.anim.close_next);
					}
				});
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(CartoonActivity.this, MediaPaly_activity.class);
				intent.putExtra("ITEM_POSITION", position);
				startActivity(intent);
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
			}
		});
	}

	private class CartoonAdapter extends BaseAdapter {
		private List<String> data;

		public CartoonAdapter(List<String> list) {
			this.data = list;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
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
		switch (position % 4) {
		case 0:
			imageResId = R.drawable.cartoon_xilankapu;
			viewHolder.textView.setText("西兰卡普");
			break;
		case 1:
			imageResId = R.drawable.cartoon_youfangji;
			viewHolder.textView.setText("游方记");
			break;
		case 2:
			imageResId = R.drawable.cartoon_bicika_1;
			viewHolder.textView.setText("毕兹卡的传说一");
			break;
		case 3:
			imageResId = R.drawable.cartoon_bicika_2;
			viewHolder.textView.setText("毕兹卡的传说二");
			break;
		default:
			imageResId = R.drawable.cartoon_xilankapu;
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
		overridePendingTransition (R.anim.open_main, R.anim.close_next);
	}

}
