package com.aliyesim.diaryapp;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Diary> diary_List;

	public MyListAdapter(Activity activity, List<Diary> diaries) {
		
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		diary_List = diaries;
	}

	@Override
	public int getCount() {
		return diary_List.size();
	}

	@Override
	public Object getItem(int position) {
		return diary_List.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.activity_diary_select, null); 

		TextView textView1 = (TextView) vi.findViewById(R.id.diary_date_list_select); 
		TextView textView2 = (TextView) vi.findViewById(R.id.diary_title_list_select);

		Diary diary = diary_List.get(position);
		
		textView1.setText(diary.getDate());
		textView2.setText(diary.getTitle());
		
		return vi;
	}
}