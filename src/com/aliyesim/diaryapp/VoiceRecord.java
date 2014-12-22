package com.aliyesim.diaryapp;

import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.util.Log;

public class VoiceRecord {
	private MediaRecorder mRecorder;
	private String mFileName;
	private MediaPlayer player;

	public VoiceRecord() {
		super();
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		// mFileName = Environment.getRootDirectory().getAbsolutePath();
		mFileName += "/record.3gp";
		mRecorder = new MediaRecorder();
		player = new MediaPlayer();

	}
	public String onRecord(boolean start) {
		if (start) {
			startRecording();
		} else
			stopRecording();

		return mFileName;
	}

	private void startRecording() {

		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
			mRecorder.start();

		} catch (Exception e) {
			// TODO: handle exception
			Log.e("Recording---->", "praper failed" + e);
		}
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	public void startPlaying() {
		player = new MediaPlayer();
		player.setVolume(1.0f, 1.0f);
		try {
			player.setDataSource(mFileName);
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
	public boolean deleteRecord(){
		File file = new File(mFileName);
		boolean deleted = file.delete();
		if (deleted) {
			return true;
		}
		return false;
	}
	public void stopPlaying(){
		player.stop();
	}
}
