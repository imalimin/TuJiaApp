package com.example.tujia.cartoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.tujia.R;


public class Main extends SwipeBackActivity{

	private LayoutInflater mInflater;
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_cartoon);
		setEdgeFromLeftOrRight();
		mInflater=LayoutInflater.from(getApplicationContext());
		listView=(ListView)findViewById(R.id.lv);
		listView.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = mInflater.inflate(R.layout.peopleinfoitem, null);
				return convertView;
			}
			
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				return 10;
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent =new Intent(getApplicationContext(), MediaPaly_activity.class);
				intent.putExtra("ITEM_POSITION", position);
				startActivity(intent);
			}
		});
	}
	

	@Override
	public void inWindow() {
		
	}

}
