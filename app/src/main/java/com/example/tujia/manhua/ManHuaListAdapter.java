package com.example.tujia.manhua;

import java.util.ArrayList;
import com.example.tujia.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ManHuaListAdapter extends BaseAdapter {
	private ArrayList<ManHuaListItem> list;
	private LayoutInflater inflater;

	public ManHuaListAdapter(ArrayList<ManHuaListItem> list, Context context) {
		super();
		this.list = list;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.view_cartoonlistitem,
					parent, false);
			holder = new ViewHolder();
			holder.imageview = (ImageView) convertView
					.findViewById(R.id.cartoon_item_image);
			holder.title = (TextView) convertView
					.findViewById(R.id.cartoon_item_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ManHuaListItem item = list.get(position);
		holder.imageview.setImageResource(item.getBitmap());
		holder.title.setText(item.getContent_title());
		Log.v("title", item.getContent_title() + "");
		return convertView;
	}

	static class ViewHolder {
		private ImageView imageview;
		private TextView title;
	}

}
