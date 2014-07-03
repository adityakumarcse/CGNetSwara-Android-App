package com.MSRi.ivr;

import java.io.File;
import java.util.Date;  
import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import java.util.Calendar;
import java.io.IOException;  
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Button;
import android.os.Environment;
import android.content.Context;
import java.io.FileOutputStream;
import android.media.MediaPlayer;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import android.media.MediaRecorder;
import javax.mail.MessagingException;
import android.view.View.OnClickListener;
import android.telephony.TelephonyManager;
import javax.mail.AuthenticationFailedException;

/** This screen allows the user to record an audio message.
 *  They can then chose to send the recording off to a central location. 
 *  The user is able to listen to the recording prior to sending the off.  
 *  
 *  TODO : Add three minute count down 
 *  TODO : Add start @ the beep & allow repeated listens to audio recording
 *  
 *  @author Krittika D'Silva (krittika.dsilva@gmail.com) */
public class RecordAudio extends Activity {
	private static final String TAG = "RecordAudio";

	/** CGNet Swara's main directory with audio files. */
	private String mMainDir;
	
	/** Folder containing all audio files that have yet to be sent. */
	private final String innerDir = "/To_Be_Sent";
	
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
	private Button start;
	
	/** Stops recording audio. This also saves the audio recording. 
	 *  TODO: Confirm when this happens. */
	private Button stop;
	
	/** Plays back the audio that the user recorded. */
	private Button playback;
	
	/** Sends audio recording to a central location if there's an Internet 
	 *  connection, if not saves the audio recording in a 
	 *  known folder - to be sent later. */
	private Button sendAudio;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.record_audio); 

		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		playback = (Button) findViewById(R.id.playback);
		sendAudio = (Button) findViewById(R.id.sendAudio);

		// At first, the only option the user has is to record audio
		start.setEnabled(true);
		stop.setEnabled(false);
		playback.setEnabled(false);
		sendAudio.setEnabled(false);
		
		// Create folders for the audio files 
		setupDirectory();
	
		start.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 		
				stop.setEnabled(true);
				start.setEnabled(false);
				
				stopPlayingAudio(mCGNetAudio);   
				startRecording();  
			}  
		}); 


		stop.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {
				stop.setEnabled(false); 
				playback.setEnabled(true);
				sendAudio.setEnabled(true);
				
				stopPlayingAudio(mCGNetAudio);  
				stopRecording();
			}  
		});

		playback.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {
				stop.setEnabled(false);
				start.setEnabled(false);
				// TODO: Change this so that it's no disabled and 
				// people can listen to their audio files more than once
				playback.setEnabled(false); 
				sendAudio.setEnabled(true);
				
				stopPlayingAudio(mCGNetAudio); 
				startPlaying();
			}
		});

		sendAudio.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 
				stopPlayingAudio(mCGNetAudio);  // Audio really shouldn't be playing at this point
				sendData(); 
			}
		});
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
		File dirInner = new File(mMainDir + innerDir);
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
		mUniqueAudioRecording = "/" + date + "__" + time + ".mp3";
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
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mMainDir + mUniqueAudioRecording);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
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
			mUserAudio.setDataSource(mMainDir + mUniqueAudioRecording);
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
		SendEmailAsyncTask task = new SendEmailAsyncTask();
		task.execute();  
	} 

	
	class SendEmailAsyncTask extends AsyncTask <Void, Void, Boolean> {
		String TAG = "SendEmailAsyncTask";  		
	    Mail m = new Mail("cgnet112358@gmail.com", "cgnetswara");

	    public SendEmailAsyncTask() { 
	        String[] toArr = { "krittika.dsilva@gmail.com"};
	        m.setTo(toArr);
	        m.setFrom("cgnet112358@gmail.com");
	        m.setSubject("Email send on the first try.");
	        
	        TelephonyManager tMgr = (TelephonyManager) RecordAudio.this.getSystemService(Context.TELEPHONY_SERVICE);
	        String mPhoneNumber = tMgr.getLine1Number();
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	        String currentDateandTime = sdf.format(new Date());
	         
	        String body = "Email sent from phone number: " + mPhoneNumber;
	        body += "Time/data sent at: " + currentDateandTime;
	        
	        m.setBody(body);
	        
	        try {
				m.addAttachment(mMainDir + mUniqueAudioRecording);
			} catch (Exception e) { 
				e.printStackTrace();
			}
	    }

	    @Override
	    protected Boolean doInBackground(Void... params) { 
	        try { 
	        	if (m.send()) { 
	        		Log.e(TAG, "email sent");
	        	} else { 
	        		Log.e(TAG, "no internet, email cannot be sent");
	        		saveEmail(m); // TODO: Is this how we want to do it?
	        	}
	            return true;
	        } catch (AuthenticationFailedException e) {
	            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
	            e.printStackTrace();
	            return false;
	        } catch (MessagingException e) {
	            Log.e(SendEmailAsyncTask.class.getName(), " " + e);
	            e.printStackTrace();
	            return false;
	        } catch (Exception e) {
	        	Log.e(TAG, "" + e);
	            e.printStackTrace();
	            return false;
	        }
	    } 
	}
	
	
	public void saveEmail(Mail m) {  
		String emailInfo = "TO: krittika.dsilva@gmail.com";
 		
		File myFile = new File(mMainDir + innerDir + mUniqueAudioRecording); 
        try {
			myFile.createNewFile();
	        FileOutputStream fOut = new FileOutputStream(myFile);
	        OutputStreamWriter myOutWriter = 
	                                new OutputStreamWriter(fOut);
	        myOutWriter.append(emailInfo);
	        myOutWriter.close();
	        fOut.close();
		} catch (IOException e) { 
			e.printStackTrace();
		} 
	}
	

}