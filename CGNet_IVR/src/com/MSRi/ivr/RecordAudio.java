package com.MSRi.ivr;

import java.io.File;  
import android.util.Log;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View; 
import java.util.Calendar;
import java.io.IOException;  
import android.app.Activity;  
import android.widget.Toast;
import android.widget.Button; 
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.widget.TextView;  
import android.media.MediaPlayer;
import android.os.CountDownTimer; 
import android.provider.MediaStore;
import android.media.MediaRecorder; 
import android.view.View.OnClickListener; 

/** This screen allows the user to record an audio message.
 *  They can then chose to send the recording off to a central location. 
 *  The user is able to listen to the recording prior to sending the off.  
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
	  
	/** Starts recording audio.  */
	private Button mBack;
	
	/** Stops recording audio. */
	private Button mStop;
	
	/** Plays back the audio that the user recorded. */
	private Button mPlayback;
	
	/** Sends audio recording to a central location if there's an Internet 
	 *  connection, if not saves the audio recording in a 
	 *  known folder - to be sent later. */
	private Button mSendAudio;
	
	private Button mCamera;
	
	/** Displays the amount of time left - each recording can be 3 mins max */
	private TextView mCountdown;

	/** Shows time remaining. */
	private CountDownTimer timer;
	 
    /** The action code we use in our intent, 
     *  this way we know we're looking at the response from our own action.  */
    private static final int SELECT_PICTURE = 1;

	/** Saves logs about the user */
    private SaveUserLogs mUserLogs;
     
    private boolean mFileToBeSent;
    
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.record_audio); 
 
		mBack = (Button) findViewById(R.id.start);
		mStop = (Button) findViewById(R.id.stop);
		mPlayback = (Button) findViewById(R.id.playback);
		mSendAudio = (Button) findViewById(R.id.sendAudio);
		mCountdown = (TextView) findViewById(R.id.time); 
		mCamera = (Button) findViewById(R.id.camera);
		
		// At first, the only option the user has is to record audio
		mBack.setEnabled(true);
		mStop.setEnabled(false);
		mPlayback.setEnabled(false);
		mSendAudio.setEnabled(false);
		mCamera.setEnabled(false);
		 
		
		mFileToBeSent = false;
		
		// Create folders for the audio files 
		setupDirectory();
	
		mBack.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 	 
				stopRecording();
		    	Intent intent = new Intent(RecordAudio.this, MainActivity.class);
		    	startActivity(intent); 
			}  
		}); 


		mStop.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 
				mStop.setEnabled(false); 
				mPlayback.setEnabled(true);
				mSendAudio.setEnabled(true);
				mCamera.setEnabled(true);
				timer.cancel();   
				stopRecording();
			}  
		});

		mPlayback.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 
				mStop.setEnabled(false);
				mBack.setEnabled(false); 
				mPlayback.setEnabled(false); 
				mSendAudio.setEnabled(true);
				 
				startPlaying();
			}
		});

		mSendAudio.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {  
				sendData(); 
				Toast.makeText(RecordAudio.this,"Your file has been sent.", 
		                Toast.LENGTH_SHORT).show();
		    	Intent intent = new Intent(RecordAudio.this, MainActivity.class);
		    	startActivity(intent);
			}
		});
		
		mCamera.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 
				 
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
			}
		});
		
		
		timer =  new CountDownTimer(3*60*1000, 1000) {
	        public void onTick(long millis) {
	            int seconds = (int) (millis / 1000) % 60 ;
	            int minutes = (int) ((millis / (1000*60)) % 60); 
	            String text = String.format("%2d minutes and %02d seconds remaining", minutes, seconds);
	            mCountdown.setText(text);
	        }
	        public void onFinish() {
	        	mStop.performClick(); 
	        }
	    };
	    
	    Runnable runnable = new Runnable() {
	    	@Override
	    	public void run() {  
	    		mStop.setEnabled(true);	  
	    		startRecording(); 
	    	}
	    };
	    // Waits 11 ms - for recording to finish
	    Handler handler = new Handler();
	    handler.postDelayed(runnable, 11000);
	}

	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
            	Toast.makeText(RecordAudio.this,"You have selected an image.", 
                        Toast.LENGTH_SHORT).show();
                Uri selectedImageUri = data.getData();
                
                String selectedImagePath = getPath(selectedImageUri);	
                
                mUserLogs.setPhotoPat(selectedImagePath); 
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
            // just some safety built in 
            if( uri == null ) { 
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            if( cursor != null ){
                int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            // this is our fallback here
            return uri.getPath();
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
		Calendar c = Calendar.getInstance();
		String date = c.get(Calendar.YEAR) + "_"+ c.get(Calendar.MONTH)
					  + "_" + c.get(Calendar.DAY_OF_MONTH);
		String time = c.get(Calendar.HOUR_OF_DAY) + "_" 
					  + c.get(Calendar.MINUTE) + "_" + c.get(Calendar.SECOND);
		 
		mUniqueAudioRecording = "/" + date + "__" + time;
		 
		mUserLogs = new SaveUserLogs(mMainDir, mUniqueAudioRecording);
		
		mUniqueAudioRecording += ".wav"; 
	}

	/** Releases resources back to the system.  */
	private void stopPlayingAudio(MediaPlayer mp) {
		if(mp != null) {
			mp.stop();
			mp.reset();   
			mp = null;	
		}
	}
	
    /** Releases resources back to the system.  */
	private void stopRecording() {   
		if (mRecorder != null) { 
			
			mRecorder.reset();
			mRecorder = null;
		} 
	}
	
    /** Called when the activity is paused; releases resources back to the 
     *  system and stops audio recordings that may be playing. */
	@Override
	public void onPause() {
		super.onPause();
		
		// If the user pauses the app when they're recording a message 
		// we're going to treat it like they paused the recording before 
		// pausing the app
		if(!mStop.isEnabled()) { 
			timer.cancel(); 
			stopRecording(); 
		}  
		stopPlayingAudio(mUserAudio);
 
		if (mRecorder != null) {
			mRecorder.reset(); 
			mRecorder = null;
		}
 	}

	/** Called when the activity is paused; begins playing the audio recording
	 *  for the user. */
	@Override
	public void onResume() {
		super.onResume();
		
		if(mRecorder != null) {
			mRecorder = null;
		} 
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
		} catch (Exception e) { 
			Log.e("TAG", e.toString());
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
			
			mUserAudio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			    public void onCompletion(MediaPlayer mp) { 
			    	mPlayback.setEnabled(true);
			    }
			});
			
		} catch (IOException e) {
			Log.e(TAG, "StartPlaying() : prepare() failed");
		} catch (Exception e) { 
			Log.e(TAG, e.toString());
		}
	}
 
    /** Inflates the menu. Currently, there aren't any meaningful items
     *  to add to the action bar. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
     
	/** Sends the audio file to a central location 
	 *  TODO: I don't think this is working  */
	private void sendData() { 
		mFileToBeSent = true;
		Log.e(TAG, "2. Sending Data: Should iterate through files in the dir now");
		Intent intent = new Intent(); 
		intent.setAction("com.android.CUSTOM_INTENT");
		sendBroadcast(intent);  
	} 
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if(!mFileToBeSent) {
	    	Log.e(TAG, "Deleting file: " + mMainDir + mInnerDir + mUniqueAudioRecording);
	    	File file = new File(mMainDir + mInnerDir + mUniqueAudioRecording);
	    	file.delete();
	    }
	} 
}