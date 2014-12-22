package com.aliyesim.diaryapp;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DiaryUpdate extends Activity {

	// Location
	private String address;
	private String city;
	private String country;
	private Geocoder geocoder;
	private List<Address> addresses;
	private LocationManager lm;
	private Location location;
	private double longitude;
	private double latitude;
	private TextView lbl_location_update;
	// Location
	private boolean a = true;

	ImageButton ib;
	Button btn_diary_update;
	TextView lbl_date_update;
	EditText txt_title_update, txt_content_update;
	int id;

	private ImageView imageView;
	private Button btnFotoCapture;
	private Button btnFotoPick;
	private Bitmap image;
	private static final int IMAGE_PICK = 1;
	private static final int IMAGE_CAPTURE = 2;

	private DateFormat fmtDate = DateFormat.getDateTimeInstance();
	private Calendar myCalendar = Calendar.getInstance();

	private void updateDateLabel() {
		lbl_date_update.setText(fmtDate.format(myCalendar.getTime()));

	}

	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int month, int day) {
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, month);
			myCalendar.set(Calendar.DAY_OF_MONTH, day);
			updateDateLabel();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_update);

		lbl_location_update = (TextView) findViewById(R.id.lbl_location_update);

		ib = (ImageButton) findViewById(R.id.img_btn_date_update);

		ib.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				new DatePickerDialog(DiaryUpdate.this, d, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();

			}
		});

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Gunluk Detay");

		btn_diary_update = (Button) findViewById(R.id.btn_diary_update);
		txt_title_update = (EditText) findViewById(R.id.txt_diary_title_update);
		txt_content_update = (EditText) findViewById(R.id.txt_diary_content_update);
		lbl_date_update = (TextView) findViewById(R.id.lbl_date_update);
		Intent intent = getIntent();
		id = intent.getIntExtra("id", 0);

		Database db = new Database(getApplicationContext());
		HashMap<String, String> map = db.diaryDetail(id);

		lbl_date_update.setText(map.get("tarih"));
		txt_title_update.setText(map.get("baslik"));
		txt_content_update.setText(map.get("icerik"));

		btn_diary_update.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String date, title, content;
				date = lbl_date_update.getText().toString();
				title = txt_title_update.getText().toString();
				content = txt_content_update.getText().toString();

				if (date.matches("") || title.matches("")
						|| content.matches("")) {
					Toast.makeText(getApplicationContext(),
							"Tum Bilgileri Eksiksiz Doldurunuz",
							Toast.LENGTH_LONG).show();
				} else {
					Database db = new Database(getApplicationContext());
					db.diaryUpdate(date, title, content, id);
					db.close();
					Toast.makeText(getApplicationContext(),
							"Gunluk Basariyla Duzenlendi.", Toast.LENGTH_LONG)
							.show();
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);
					startActivity(intent);
					finish();
				}

			}
		});

		this.imageView = (ImageView) this.findViewById(R.id.imageView_update);
		this.btnFotoCapture = (Button) this
				.findViewById(R.id.btn_foto_capture_update);

		this.btnFotoCapture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, IMAGE_CAPTURE);

			}
		});

		this.btnFotoPick = (Button) this
				.findViewById(R.id.btn_pick_gallery_update);

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

		lbl_location_update.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					lbl_location_update.setText("LOKASYON ALINIYOR...");
					updateLocation();
				} catch (Exception e) {
					e.printStackTrace();
					lbl_location_update.setText("LOKASYON ALINAMADI");
				}

			}
		});

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
					//
					//
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

					lbl_location_update.setText(address + " " + city + " "+ country);
					a = false;
				} catch (Exception e) {

				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Lokasyon bilgisi alinamiyor.", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			lbl_location_update.setText("Lokasyon eklemek icin tiklayiniz...");
			a = true;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_diary_update, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
