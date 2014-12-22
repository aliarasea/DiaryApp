package com.aliyesim.diaryapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    ListView customListView;
	MyListAdapter myListAdapter;
	private List<Diary> diary;

	
	ArrayList<HashMap<String, String>> diary_list;
	String diary_dates[];
	String diary_titles[];
	int diary_id[];

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

	}

	public void onResume() {
		super.onResume();
		Database db = new Database(getApplicationContext());
		diary_list = db.diaries();

		if (diary_list.size() == 0) {
			Toast.makeText(
					getApplicationContext(),
					"Henuz Kayit Eklenmemis.\nYukaridaki + Butonundan Ekleyiniz",
					Toast.LENGTH_LONG).show();
		}

		else {
			
			diary_id = new int[diary_list.size()];

			for (int i = 0; i < diary_list.size(); i++) 
			{
				diary_id[i] = Integer.parseInt(diary_list.get(i).get("id"));
			}

			
			customListView = (ListView) findViewById(R.id.list_view);

			diary = db.getAllDiaries();

			myListAdapter = new MyListAdapter(MainActivity.this,
					diary);

			customListView.setAdapter(myListAdapter);
			customListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					Intent intent = new Intent(getApplicationContext(),
							DiaryDetail.class);
					intent.putExtra("id", (int) diary_id[position]);
					startActivity(intent);
				}
			});

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_diary_main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.ekle:
			AddDiary();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void AddDiary() {
		Intent i = new Intent(MainActivity.this, DiaryInsert.class);
		startActivity(i);
	}
}
