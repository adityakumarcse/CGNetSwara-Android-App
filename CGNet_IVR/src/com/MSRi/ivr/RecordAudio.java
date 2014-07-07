package com.MSRi.ivr;

import java.io.File;  

import android.util.Log;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.View; 

import java.util.Calendar;
import java.io.IOException;  

import android.app.Activity; 
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Environment;  
import android.media.MediaPlayer; 
import android.media.MediaRecorder; 
import android.view.View.OnClickListener; 

/** This screen allows the user to record an audio message.
 *  They can then chose to send the recording off to a central location. 
 *  The user is able to listen to the recording prior to sending the off.  
 *   
 *  TODO : Add start @ the beep & allow repeated listens to audio recording
 *  
 *  @author Krittika D'Silva (krittika.dsilva@gmail.com) */
public class RecordAudio extends Activity {
	private static final String TAG = "RecordAudio";

	/** CGNet Swara's main directory with audio files. */
	private String mMainDir;
	
	/** Folder containing all audio files that have yet to be sent. */
	private final String mInnerDir = "/ToBeSent";
	
	/** Name of the audio file created.	*/
	private String mUniqueAudioRecording;
	
	/** Plays back the users voice recording. */
	private MediaPlayer mUserAudio; 
  	
	/** Records the users audio recording. */
	private MediaRecorder mRecorder;
	
	/** Plays CG Net Swara audio files instructor the user how to record 
	 *  an audio file. */
	private MediaPlayer mCGNetAudio;
	
	/** Starts recording audio. 
	 *  TODO: Replace this button with an @ the beep mark. */
	private Button mStart;
	
	/** Stops recording audio. */
	private Button mStop;
	
	/** Plays back the audio that the user recorded. */
	private Button mPlayback;
	
	/** Sends audio recording to a central location if there's an Internet 
	 *  connection, if not saves the audio recording in a 
	 *  known folder - to be sent later. */
	private Button mSendAudio;
	
	/** Displays the amount of time left - each recording can be 3 mins max */
	private TextView mCountdown;

	/** Shows time remaining. */
	private CountDownTimer timer;
	 
	
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.record_audio); 

		mStart = (Button) findViewById(R.id.start);
		mStop = (Button) findViewById(R.id.stop);
		mPlayback = (Button) findViewById(R.id.playback);
		mSendAudio = (Button) findViewById(R.id.sendAudio);
		mCountdown = (TextView) findViewById(R.id.time); 
		
		// At first, the only option the user has is to record audio
		mStart.setEnabled(true);
		mStop.setEnabled(false);
		mPlayback.setEnabled(false);
		mSendAudio.setEnabled(false);
		
		// Create folders for the audio files 
		setupDirectory();
	
		mStart.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 		
				mStop.setEnabled(true);
				mStart.setEnabled(false);
				
				stopPlayingAudio(mCGNetAudio);   
				startRecording();  
			}  
		}); 


		mStop.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {
				mStop.setEnabled(false); 
				mPlayback.setEnabled(true);
				mSendAudio.setEnabled(true);
				timer.cancel();
				stopPlayingAudio(mCGNetAudio);  
				stopRecording();
			}  
		});

		mPlayback.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {
				mStop.setEnabled(false);
				mStart.setEnabled(false);
				// TODO: Change this so that it's no disabled and 
				// people can listen to their audio files more than once
				mPlayback.setEnabled(false); 
				mSendAudio.setEnabled(true);
				
				stopPlayingAudio(mCGNetAudio); 
				startPlaying();
			}
		});

		mSendAudio.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 
				stopPlayingAudio(mCGNetAudio);  // Audio really shouldn't be playing at this point
				sendData(); 
				Toast.makeText(RecordAudio.this,"Your file has been sent.", 
		                Toast.LENGTH_SHORT).show();
		    	Intent intent = new Intent(RecordAudio.this, MainActivity.class);
		    	startActivity(intent);
			}
		});
		
		
		timer =  new CountDownTimer(3*60*1000, 1000) {
	        public void onTick(long millis) {
	            int seconds = (int) (millis / 1000) % 60 ;
	            int minutes = (int) ((millis / (1000*60)) % 60); 
	            String text = String.format("%02d minutes and %02d seconds remaining", minutes, seconds);
	            mCountdown.setText(text);
	        }
	        public void onFinish() {
	        	mStop.performClick(); 
	        }
	    };
	}

	/** Sets up file structure for audio recordings. **/
	private void setupDirectory() {
		// This folder should have been created in MainActivity
		// This is just in case it wasn't.
		mMainDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		mMainDir += "/CGNetSwara";
		File dir = new File(mMainDir);
		if(!dir.exists() || !dir.isDirectory()) {
		    dir.mkdir();
		} 		
		 
		// This folder will be queried when there's Internet - files that 
		// need to be sent should be stored in here 
		File dirInner = new File(mMainDir + mInnerDir);
		if(!dirInner.exists() || !dirInner.isDirectory()) {
			dirInner.mkdir();
		} 
		
		// Name of audio file is the data and then time the audio was created.
		// TODO: Incorporate phone number here to make it truly unique?
		Calendar c = Calendar.getInstance();
		String date = c.get(Calendar.YEAR) + "_"+ c.get(Calendar.MONTH)
					  + "_" + c.get(Calendar.DAY_OF_MONTH);
		String time = c.get(Calendar.HOUR_OF_DAY) + "_" 
					  + c.get(Calendar.MINUTE) + "_" + c.get(Calendar.SECOND);
		
		// TODO: Make sure paths are right 
		mUniqueAudioRecording = "/" + date + "__" + time + ".wav";
	}

	/** Releases resources back to the system.  */
	private void stopPlayingAudio(MediaPlayer mp) {
		if(mp != null) {
			mp.release();   
			mp = null;	
		}
	}
	
    /** Releases resources back to the system.  */
	private void stopRecording() {   
		if (mRecorder != null) { 
			mRecorder.release();
			mRecorder = null;
		} 
	}
	
    /** Called when the activity is paused; releases resources back to the 
     *  system and stops audio recordings that may be playing. */
	@Override
	public void onPause() {
		super.onPause();
		
		stopPlayingAudio(mCGNetAudio);
		stopPlayingAudio(mUserAudio);
		
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
 	}

	/** Called when the activity is paused; begins playing the audio recording
	 *  for the user. */
	@Override
	public void onResume() {
		super.onResume();    
		mCGNetAudio = MediaPlayer.create(this, R.raw.mistake_0_beep_start_finish_1);
		mCGNetAudio.start(); 
	}

	/** Creates an audio recording using the phone mic as the audio source. */
	private void startRecording() { 
		timer.start(); 
		
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mMainDir + mInnerDir + mUniqueAudioRecording); 
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		Log.e(TAG, "1. Create file: " + mMainDir + mInnerDir + mUniqueAudioRecording);
		
		try {
			mRecorder.prepare(); 
			mRecorder.start();
		} catch (IOException e) { 
			Log.e(TAG, "StartRecording() : prepare() failed");
		} 
	}

	/** Plays the generated audio recording. */
	private void startPlaying() {
		mUserAudio = new MediaPlayer();
		try {
			// Saved in the main folder 
			mUserAudio.setDataSource(mMainDir + mInnerDir + mUniqueAudioRecording);
			mUserAudio.prepare();
			mUserAudio.start();
		} catch (IOException e) {
			Log.e(TAG, "StartPlaying() : prepare() failed");
		}		
	}
 
    /** Inflates the menu. Currently, there aren't any meaningful items
     *  to add to the action bar. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
     
	/** Sends the audio file to a central location */
	private void sendData() { 
		Log.e(TAG, "2. Sending Data: Should iterate through files in the dir now");
		Intent intent = new Intent();
		intent.setAction("com.android.CUSTOM_INTENT");
		sendBroadcast(intent); 
	} 
}