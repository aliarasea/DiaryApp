package com.aliyesim.diaryapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DiaryDetail extends Activity {
	
	private String address;
	private String city;
	private String country;
	private Geocoder geocoder;
	private List<Address> addresses;
	private double longitude;
	private double latitude;
	
	Button btn_update_diary,btn_delete_diary;
	TextView lbl_date,lbl_title,lbl_content,lbl_location;
	
	int id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_detail);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Gunluk Listesi");
		
		btn_update_diary = (Button)findViewById(R.id.btn_update);
		btn_delete_diary = (Button)findViewById(R.id.btn_delete);
		
		lbl_date = (TextView)findViewById(R.id.lbl_date_detail);
		lbl_title = (TextView)findViewById(R.id.lbl_title_detail);
		lbl_content = (TextView)findViewById(R.id.lbl_content_detail);
		lbl_location =(TextView)findViewById(R.id.lbl_location_detail);

		

		Intent intent=getIntent();
		id = intent.getIntExtra("id", 0);
		
		Database db = new Database(getApplicationContext());
		HashMap<String, String> map = db.diaryDetail(id);
		
		
		lbl_date.setText(map.get("tarih"));
		lbl_title.setText(map.get("baslik"));
		lbl_content.setText(map.get("icerik"));	
		longitude = Double.parseDouble(map.get("longitude"));	
		latitude = Double.parseDouble(map.get("latitude"));
		
		geocoder = new Geocoder(this, Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(latitude,
					longitude, 1);
		} catch (IOException e) {
			e.toString();
		}

		address = addresses.get(0).getAddressLine(0);
		city = addresses.get(0).getAddressLine(1);
		country = addresses.get(0).getAddressLine(2);

		lbl_location.setText(address + " " + city + " "
				+ country);
		
		
		btn_update_diary.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				 Intent intent = new Intent(getApplicationContext(), DiaryUpdate.class);
				 intent.putExtra("id", (int)id);
                 startActivity(intent);
			}
		});
		
		btn_delete_diary.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(DiaryDetail.this);
    	        alertDialog.setTitle("Uyari");
    	        alertDialog.setMessage("Gunluk Silinsin mi?");
    	        alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog,int which) {
    	            	Database db = new Database(getApplicationContext());
    	            	db.diaryDelete(id);
    	            	Toast.makeText(getApplicationContext(), "Gunluk Basariyla Silindi", Toast.LENGTH_LONG).show();
    	            	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    	                startActivity(intent);
    	                finish();
    	                
    	            }
    	        });
    	        
    	        
    	        alertDialog.setNegativeButton("Hayir", new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog,int which) {
    	            	
    	            }
    	        });
    	        alertDialog.show();     
				
			}
		});
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	        return true;
	    default: return super.onOptionsItemSelected(item);  
	    }
	}
}
