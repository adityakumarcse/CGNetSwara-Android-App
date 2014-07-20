package com.MSRi.ivr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory; 
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	private ImageButton mStart;
	
	/** Stops recording audio. */
	private ImageButton mStop;
	
	/** Plays back the audio that the user recorded. */
	private ImageButton mPlayback;
	
	/** */
	private ImageButton mBack;
	
	/** Sends audio recording to a central location if there's an Internet 
	 *  connection, if not saves the audio recording in a 
	 *  known folder - to be sent later. */
	private ImageButton mSendAudio;
	  
	/** Displays the amount of time left - each recording can be 3 mins max */
	private TextView mCountdown;

	/** Shows time remaining. */
	private CountDownTimer timer;
	 
    /** The action code we use in our intent, 
     *  this way we know we're looking at the response from our own action.  */
    private static final int SELECT_PICTURE = 1;

	/** Saves logs about the user */
    private SaveAudioInfo mUserLogs;
     
    private boolean mFileToBeSent;
    
    private ImageView mUserImage;
    
    private String mPhoneNumber; 
    
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.record_audio); 
		
		mStart = (ImageButton) findViewById(R.id.start);
		mStop = (ImageButton) findViewById(R.id.stop);
		mPlayback = (ImageButton) findViewById(R.id.playback);
		mSendAudio = (ImageButton) findViewById(R.id.sendAudio);
		mCountdown = (TextView) findViewById(R.id.time); 
		mUserImage = (ImageView) findViewById(R.id.userImage);
		mBack = (ImageButton) findViewById(R.id.backToMain);
		
		mFileToBeSent = false;
		
		// At first, the only option the user has is to record audio
		mStart.setVisibility(View.VISIBLE); 
		mStop.setVisibility(View.GONE);
		mPlayback.setVisibility(View.GONE);
		mSendAudio.setVisibility(View.GONE); 
		mBack.setVisibility(View.GONE);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras(); 
		boolean includePhoto = extras.getBoolean("photo"); 
		mPhoneNumber = extras.getString("phone");
		
		
		if(includePhoto) { 
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(i,
                    "Select Picture"), SELECT_PICTURE); 
		}
		 
		// Create folders for the audio files 
		setupDirectory();
	
		mStart.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 	 
				stopRecording();
	    		mStart.setVisibility(View.GONE);
				mStop.setVisibility(View.VISIBLE);	  
	    		startRecording(); 
			}  
		}); 
 
		mStop.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 
				mStop.setVisibility(View.GONE); 
				mPlayback.setVisibility(View.VISIBLE);
				mSendAudio.setVisibility(View.VISIBLE); 
				mBack.setVisibility(View.VISIBLE);
				timer.cancel();   
				stopRecording();
			}  
		});

		mPlayback.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 
				mStart.setVisibility(View.GONE);
				mStop.setVisibility(View.GONE); 
				mPlayback.setVisibility(View.VISIBLE);
				mPlayback.setEnabled(false);
				mSendAudio.setVisibility(View.VISIBLE); 
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
		
		mBack.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 
		    	Log.e(TAG, "Going back + deleting file: " + mMainDir + mInnerDir + mUniqueAudioRecording);
		    	File file = new File(mMainDir + mInnerDir + mUniqueAudioRecording);
		    	if(file.exists()) {
		    		file.delete();
		    	}
		    	Intent intent = new Intent(RecordAudio.this, MainActivity.class);
		    	startActivity(intent);
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
	        	mPlayback.setEnabled(true);
	        }
	    }; 
	}

	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
            	Toast.makeText(RecordAudio.this,"You have selected an image.", 
                        Toast.LENGTH_SHORT).show();
            	
            	Uri selectedImageUri = data.getData();
                String selectedImagePath = getPath(selectedImageUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                Bitmap bitmap = BitmapFactory.	decodeFile(selectedImagePath);
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                
                if (height > 1280 && width > 960){
                    Bitmap imgbitmap = BitmapFactory.decodeFile(selectedImagePath, options); 
                    Bitmap scaled = Bitmap.createScaledBitmap(imgbitmap,  width/2, height/2, false);    
                    mUserImage.setImageBitmap(scaled); 
                } else { 
                	mUserImage.setImageBitmap(bitmap);
                    System.out.println("WORKS");
                } 
                mUserLogs.setPhotoPath(selectedImagePath); 
            }
        }
    }
	
	public static Bitmap lessResolution (String filePath, int width, int height)
    {   int reqHeight=width;
        int reqWidth=height;
         BitmapFactory.Options options = new BitmapFactory.Options();   

            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;        

            return BitmapFactory.decodeFile(filePath, options); 
    }

  private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    return inSampleSize;
    }
	 
	public static Bitmap ScaleDownBitmap(Bitmap originalImage, float maxImageSize, boolean filter)
    {
        float ratio = Math.min((float)maxImageSize / originalImage.getWidth(), (float)maxImageSize / originalImage.getHeight());
        int width = (int) Math.round(ratio * (float)originalImage.getWidth());
        int height =(int) Math.round(ratio * (float)originalImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(originalImage, width, height, filter);
        return newBitmap;
    }
	   
	  public String getPath(Uri uri) {
		  String[] projection = { MediaStore.Images.Media.DATA };
		  Cursor cursor = managedQuery(uri, projection, null, null, null);
		  int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		  cursor.moveToFirst();
		  return cursor.getString(column_index);
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
		 
		mUserLogs = new SaveAudioInfo(mMainDir, mUniqueAudioRecording, mPhoneNumber); 
		
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
		if(mStop.getVisibility() == View.VISIBLE) { 
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
     
	/** Sends the audio file to a central location. */
	private void sendData() { 
		mFileToBeSent = true;
		Log.e("!!!", "send data");
		mUserLogs.writeToFile();
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
	    	if(file.exists()) {
	    		file.delete();
	    	}
	    }
	} 
}