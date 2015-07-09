package com.example.tujia.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.tujia.book.BookListActivity;
import com.example.tujia.cartoon.CartoonActivity;
import com.example.tujia.instruction.InstructionActivity;
import com.example.tujia.manhua.ManHuaActivity;
import com.example.tujia.R;

public class MainActivity extends Activity {

	private Intent intent;
	private OnClickListener click;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		setOnlistener();
		intiview();
	}

	private void intiview() {
		this.findViewById(R.id.Cartoon).setOnClickListener(click);
		this.findViewById(R.id.Manhua).setOnClickListener(click);
		this.findViewById(R.id.Tale).setOnClickListener(click);
		this.findViewById(R.id.Summary).setOnClickListener(click);
	}

	private void setOnlistener() {
		click = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.Cartoon:
					intent = new Intent(MainActivity.this,
							CartoonActivity.class);
					break;
				case R.id.Manhua:
					intent = new Intent(MainActivity.this, ManHuaActivity.class);
					break;
				case R.id.Summary:
					intent = new Intent(MainActivity.this,
							InstructionActivity.class);
					break;
				case R.id.Tale:
					intent = new Intent(MainActivity.this,
							BookListActivity.class);
					break;
				default:
					break;
				}
				startActivity(intent);
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
			}
		};
	}
	// public SlidingMenu Sm;
	// private InstructionFragment mFragment;

	// ��ʼ������
	// private void intiview1() {
	//
	// getSlidingMenu().setMode(SlidingMenu.LEFT);
	// Sm = getSlidingMenu();
	// Sm.setShadowWidthRes(R.dimen.shadow_width);
	// // sm.setShadowDrawable(R.drawable.shadow);
	// Sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	// Sm.setFadeDegree(0.55f);
	// setSlidingActionBarEnabled(true);
	//
	// // Main
	// if (mFragment == null)
	// mFragment = new InstructionFragment(getApplicationContext(), Sm,
	// getSupportFragmentManager());
	// getSupportFragmentManager().beginTransaction()
	// .replace(R.id.MainActivity_Frame, mFragment).commit();
	// OnCampusLeftFragment.mposition = 0;
	//
	// // Left Menu
	// setBehindContentView(R.layout.fragment_on_main_left);
	// getSupportFragmentManager()
	// .beginTransaction()
	// .replace(
	// R.id.on_main_left_frame,
	// new OnCampusLeftFragment(MainActivity.this, Sm,
	// getSupportFragmentManager())).commit();
	//
	// }
}
