package com.aliyesim.diaryapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "diary_db";

	private static final String DIARY_TABLE = "gunlukler";
	private static String DIARY_TITLE = "baslik";
	private static String DIARY_ID = "id";
	private static String DIARY_DATE = "tarih";
	private static String DIARY_CONTENT = "icerik";
	public static String DIARY_LONGITUDE = "longitude";
	public static String DIARY_LATITUDE = "latitude";
	private static String DIARY_PHOTO_PATH = "photoPath";
	private static String DIARY_AUDIO_PATH = "audioPath";

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_DIARY_TABLE = "CREATE TABLE " + DIARY_TABLE + "("
				+ DIARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ DIARY_DATE + " TEXT," 
				+ DIARY_TITLE + " TEXT," 
				+ DIARY_CONTENT + " TEXT,"
				+ DIARY_LONGITUDE + " REAL," 
				+ DIARY_LATITUDE + " REAL,"
				+ DIARY_PHOTO_PATH + " TEXT," 
				+ DIARY_AUDIO_PATH + " TEXT"
				+ ")";
		db.execSQL(CREATE_DIARY_TABLE);

	}

	public void diaryDelete(int id) {

		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DIARY_TABLE, DIARY_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	public void diaryInsert(String diary_date, String diary_title,
			String diary_content) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DIARY_DATE, diary_date);
		values.put(DIARY_TITLE, diary_title);
		values.put(DIARY_CONTENT, diary_content);

		db.insert(DIARY_TABLE, null, values);
		db.close();
	}

	public void diaryInsert(Diary diary) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DIARY_DATE, diary.getDate());
		values.put(DIARY_TITLE, diary.getTitle());
		values.put(DIARY_CONTENT, diary.getContent());
		values.put(DIARY_LONGITUDE, diary.getLongitude());
		values.put(DIARY_LATITUDE, diary.getLatitude());

		db.insert(DIARY_TABLE, null, values);
		db.close();
	}

	public void diaryUpdate(String diary_date, String diary_title,
			String diary_content, int id) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(DIARY_DATE, diary_date);
		values.put(DIARY_TITLE, diary_title);
		values.put(DIARY_CONTENT, diary_content);

		db.update(DIARY_TABLE, values, DIARY_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public void diaryUpdate(Diary diary) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DIARY_DATE, diary.getDate());
		values.put(DIARY_TITLE, diary.getTitle());
		values.put(DIARY_CONTENT, diary.getContent());
		values.put(DIARY_LONGITUDE, diary.getLongitude());
		values.put(DIARY_LATITUDE, diary.getLatitude());

		db.update(DIARY_TABLE, values, DIARY_ID + " = ?",
				new String[] { String.valueOf(diary.getId()) });
		db.close();
	}

	public HashMap<String, String> diaryDetail(int id) {

		HashMap<String, String> diary = new HashMap<String, String>();
		String selectQuery = "SELECT * FROM " + DIARY_TABLE + " WHERE id=" + id;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			diary.put(DIARY_DATE, cursor.getString(1));
			diary.put(DIARY_TITLE, cursor.getString(2));
			diary.put(DIARY_CONTENT, cursor.getString(3));
			diary.put(DIARY_LONGITUDE, cursor.getString(4));
			diary.put(DIARY_LATITUDE, cursor.getString(5));
		}
		cursor.close();
		db.close();
		// return diary
		return diary;
	}

	public ArrayList<HashMap<String, String>> diaries() {

		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + DIARY_TABLE;
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<HashMap<String, String>> diaryList = new ArrayList<HashMap<String, String>>();

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					map.put(cursor.getColumnName(i), cursor.getString(i));
				}

				diaryList.add(map);
			} while (cursor.moveToNext());
		}
		db.close();
		return diaryList;
	}

	public List<Diary> getAllDiaries() {
		List<Diary> diaries = new ArrayList<Diary>();
		SQLiteDatabase db = this.getWritableDatabase();

		// String sqlQuery = "SELECT  * FROM " + TABLE_COUNTRIES;
		// Cursor cursor = db.rawQuery(sqlQuery, null);

		Cursor cursor = db.query(DIARY_TABLE, new String[] { DIARY_DATE,
				DIARY_TITLE, DIARY_CONTENT}, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Diary diary = new Diary();
			diary.setDate(cursor.getString(0));
			diary.setTitle(cursor.getString(1));
			diary.setContent(cursor.getString(2));

			diaries.add(diary);
		}

		return diaries;
	}

	public Diary getDiaryWithId(int id) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.getWritableDatabase();
		Diary diary = new Diary();
		Cursor cursor = db.query(DIARY_TABLE, new String[] { DIARY_ID,
				DIARY_DATE, DIARY_CONTENT, DIARY_TITLE, DIARY_PHOTO_PATH,
				DIARY_AUDIO_PATH }, "id=" + id, null, null, null, null);

		System.out.println("Tarih-<>>>>>>>>>>>>>>>>" + id);
		if (!cursor.moveToFirst()) {
			return null;
		}
		diary.setId(cursor.getInt(0));
		diary.setDate(cursor.getString(1));
		diary.setTitle(cursor.getString(2));
		diary.setContent(cursor.getString(3));
		diary.setLongitude(cursor.getDouble(4));
		diary.setLatitude(cursor.getDouble(5));
		diary.setPhotoPath(cursor.getString(6));
		diary.setAudioPath(cursor.getString(7));

		cursor.close();
		return diary;
	}

	public int getRowCount() {

		String countQuery = "SELECT  * FROM " + DIARY_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		return rowCount;
	}

	public void resetTables() {

		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(DIARY_TABLE, null, null);
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
