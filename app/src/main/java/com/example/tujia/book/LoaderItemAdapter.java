package com.example.tujia.book;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.tujia.R;

/**
 * Created by TOSHIBA on 13-11-2.
 */
public class LoaderItemAdapter extends BaseAdapter {

	private Context context;
	private List<HashMap<String, String>> list;

	public LoaderItemAdapter(Context context, List list) {
		this.context = context;
		this.list = list;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int arg0) {
		return arg0;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		int imageId = 0;
		convertView = LayoutInflater.from(context).inflate(R.layout.item_book,
				null);

		TextView title = (TextView) convertView
				.findViewById(R.id.item_book_title);
		ImageView bg = (ImageView) convertView.findViewById(R.id.item_book_bg);

		// title.setText(list.get(position).get("TITLE"));

		switch (position % 4) {
		case 0:
			title.setText("廪君");
			imageId = R.drawable.book_bingjunzhuan;
			break;
		case 1:
			title.setText("好吃包");
			imageId = R.drawable.book_haochibao;
			break;
		case 2:
			title.setText("熊娘家婆");
			imageId = R.drawable.book_xiongniangjiapo;
			break;
		case 3:
			title.setText("行孝得宝");
			imageId = R.drawable.book_xingxiaodebao;
			break;
		default:
			break;
		}
		bg.setBackgroundResource(imageId);

		return convertView;
	}
}
