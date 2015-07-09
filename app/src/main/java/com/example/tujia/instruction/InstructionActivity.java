package com.example.tujia.instruction;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.tujia.book.BookFactory;
import com.example.tujia.main.MainActivity;
import com.example.tujia.R;

public class InstructionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instruction);
		try {
			intiview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void intiview() throws IOException {
		this.findViewById(R.id.book_list_title_backbtn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(R.anim.open_main,
								R.anim.close_next);
					}
				});
		((TextView) this.findViewById(R.id.book_list_title_text)).setText("概况");
		((TextView) this.findViewById(R.id.instruction))
				.setText(new BookFactory("gaikuang.txt", "GB2312",
						getApplicationContext()).getTextBody());
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

}
