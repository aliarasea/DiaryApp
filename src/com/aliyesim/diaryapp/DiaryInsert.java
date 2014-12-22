package com.aliyesim.diaryapp;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DiaryInsert extends Activity {
	
	//Location
	private String address;
	private String city;
	private String country;
	private Geocoder geocoder;
	private List<Address> addresses;
	private LocationManager lm;
	private Location location;
	private double longitude;
	private double latitude;
	private TextView lbl_location_insert;
	//Location
	private boolean a = true;
	
	ImageButton btn_img;
	Button btn_save;
	TextView lbl_date_insert;
	EditText txt_title_insert,txt_content_insert;
	
	private MediaRecorder recorder;
	private MediaPlayer player;
	private Button recordButton;
	private TextView rcd;
	
	private final String audio_filepath = Environment.getExternalStorageDirectory() .getAbsolutePath() + "/record.3gp";
	
	private ImageView imageView;
	private Button btnFotoCapture;
	private Button btnFotoPick;
	private Bitmap image;
	private static final int IMAGE_PICK = 1;
	private static final int IMAGE_CAPTURE = 2;

	
	private DateFormat fmtDate = DateFormat.getDateTimeInstance();
	private Calendar myCalendar = Calendar.getInstance();

	private void updateDateLabel() {
		lbl_date_insert.setText(fmtDate.format(myCalendar.getTime()));

	}

	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int month, int day) {
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, month);
			myCalendar.set(Calendar.DAY_OF_MONTH, day);
			updateDateLabel();

		}
	};
	
	
	private void initElement(){
		btn_save = (Button)findViewById(R.id.btn_diary_save);
		btn_img = (ImageButton)findViewById(R.id.img_btn_date_insert);
		txt_title_insert = (EditText)findViewById(R.id.txt_diary_title_insert);
		txt_content_insert = (EditText)findViewById(R.id.txt_diary_content_insert);
		lbl_date_insert = (TextView)findViewById(R.id.lbl_date_insert);
		
		this.imageView = (ImageView) this.findViewById(R.id.imageView_insert);
		
		this.btnFotoCapture = (Button) this.findViewById(R.id.btn_foto_capture);
		
		
		btn_save = (Button)findViewById(R.id.btn_diary_save);
		btn_img = (ImageButton)findViewById(R.id.img_btn_date_insert);
		txt_title_insert = (EditText)findViewById(R.id.txt_diary_title_insert);
		txt_content_insert = (EditText)findViewById(R.id.txt_diary_content_insert);
		lbl_date_insert = (TextView)findViewById(R.id.lbl_date_insert);
		lbl_location_insert = (TextView) findViewById(R.id.lbl_location_insert);
		
		this.imageView = (ImageView) this.findViewById(R.id.imageView_insert);
		
		this.btnFotoCapture = (Button) this.findViewById(R.id.btn_foto_capture);
		this.btnFotoPick = (Button) this.findViewById(R.id.btn_pick_gallery);
		

		rcd = (TextView) findViewById(R.id.txtRecording);
		recordButton = (Button) findViewById(R.id.btnRecordAudio);
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_diary_insert);
		
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Gunluk");
		
		initElement();
		
		this.btnFotoCapture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, IMAGE_CAPTURE);

			}
		});		
		
		this.btnFotoPick.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "Foto"),
						IMAGE_PICK);

			}
		});
		
		btn_save.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Diary diary = new Diary();
				
				String date, title,content;
				date = lbl_date_insert.getText().toString();
				title = txt_title_insert.getText().toString();
				content = txt_content_insert.getText().toString();
				
				diary.setDate(date);
				diary.setTitle(title);
				diary.setContent(content);
				diary.setLongitude(longitude);
				diary.setLatitude(latitude);
				

				if(date.matches("") || title.matches("") || content.matches(""))
				{
				
					Toast.makeText(getApplicationContext(), "ALANLARI DOLDUR", Toast.LENGTH_LONG).show();

				}
				
				else
				{
					Database db = new Database(getApplicationContext());
					//db.diaryInsert(date, title, content);
					db.diaryInsert(diary);
					db.close();
				    Toast.makeText(getApplicationContext(), "Gunluk Basariyla Eklendi.", Toast.LENGTH_LONG).show();
				    
				    lbl_date_insert.setText("Tarih");
				    txt_title_insert.setText("");
				    txt_content_insert.setText("");
				    lbl_location_insert.setText("Konum");
				}
				
				
			}
		});
					
		btn_img.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new DatePickerDialog(DiaryInsert.this, d, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();

			}
		});
		
		recordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String recordString = getResources().getString(R.string.record);
				String stopString = getResources().getString(R.string.stop);
				if (recordString.equals(recordButton.getText().toString())) {
					startRecording();
					rcd.setVisibility(View.VISIBLE);
					recordButton.setText(stopString);
					
				} else {
					stopRecording();
					recordButton.setText(recordString);
					rcd.setVisibility(View.INVISIBLE);
					startPlaying();
				}
			}
		});
		
	
		lbl_location_insert.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					lbl_location_insert.setText("LOKASYON ALINIYOR...");
					updateLocation();				
				} catch (Exception e) {
					e.printStackTrace();
					lbl_location_insert.setText("LOKASYON ALINAMADI");
				}
				
				
			}
		});
		
		
	}
	
	
	private void startRecording() {
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(audio_filepath);
		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		if (recorder != null) {
			recorder.stop();
			recorder.reset();
			recorder.release();
			recorder = null;
		}
	}

	private void startPlaying() {
		player = new MediaPlayer();
		player.setVolume(1.0f, 1.0f);
		try {
			player.setDataSource(audio_filepath);
			player.prepare();
			player.start();
			player.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer arg0) {
					player.stop();
					player.release();
					player = null;
				}
			});
		} catch (Exception e) {

		}
	}
			
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case IMAGE_PICK:
				this.imageFromGallery(resultCode, data);
				break;
			case IMAGE_CAPTURE:
				this.imageFromCamera(resultCode, data);
				break;
			default:
				break;
			}
		}
	}
	
	private void updateImageView(Bitmap newImage) {
		BitmapProcessor bitmapProcessor = new BitmapProcessor(newImage, 1000,
				1000, 90);

		this.image = bitmapProcessor.getBitmap();
		this.imageView.setImageBitmap(this.image);
	}
	
	private void imageFromCamera(int resultCode, Intent data) {
		this.updateImageView((Bitmap) data.getExtras().get("data"));
	}
	
	private void imageFromGallery(int resultCode, Intent data) {
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String filePath = cursor.getString(columnIndex);
		cursor.close();

		this.updateImageView(BitmapFactory.decodeFile(filePath));
	}
	
	
	
	private boolean isInternetAvailable() {
		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			return netInfo != null && netInfo.isConnectedOrConnecting();

		} catch (Exception e) {
			return false;
		}

	}
	
	
	
	private void updateLocation() {
		if (a) {
			if (isInternetAvailable()) {
				longitude = 0;
				latitude = 0;
				try {
					lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					location = lm
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					longitude = location.getLongitude();
					latitude = location.getLatitude();

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

					lbl_location_insert.setText(address + " " + city + " "
							+ country);
					a = false;
				} catch (Exception e) {

				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Lokasyon bilgisi alinamiyor.", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			lbl_location_insert.setText("Lokasyon eklemek icin tiklayiniz...");
			a = true;
		}

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
