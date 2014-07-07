package com.MSRi.ivr;

import java.io.File;
import android.util.Log;
import java.io.InputStream;
import android.os.AsyncTask;
import java.io.OutputStream;
import android.content.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream; 
import java.io.FileNotFoundException;
import javax.mail.MessagingException;
import android.telephony.TelephonyManager;
import javax.mail.AuthenticationFailedException;

/** This class allows to perform background operations and
 *  publish results on the UI thread without having to manipulate threads and/or handlers.
 *  @author Krittika D'Silva
 */
class SendEmailAsyncTask extends AsyncTask <Void, Void, Boolean> {
	private static final String TAG = "SendEmailAsyncTask"; 
	
	/** Email sent with the audio recording. */
	private Mail mMail;
	
	/** CGNet Swara's main directory with audio files. */
	private String mMainDir;
	
	/** Folder containing all audio files that have yet to be sent. */
	private String mInnerDir; 
	
	/** Name of the audio file created.	*/
	private String mUniqueAudioRecording;
	
	/** */
	private final String mFromAdddress = "cgnet112358@gmail.com"; 
	
	/** */
	private final String mToAddress = "krittika.dsilva@gmail.com";
	  
	/** */
	private boolean mEmailSent = false;
	
	
	/** 
	 * 
	 * */
    public SendEmailAsyncTask(Context context, String outerDir, String innerDir, String fileName) {
    	Log.e(TAG, "5. Trying to send: " + fileName);
    	mMail = new Mail(mFromAdddress, "cgnetswara"); // TODO 
    	mMainDir = outerDir;
    	mInnerDir = innerDir;
        mUniqueAudioRecording = fileName;
    			 
    	String[] toArr = {mToAddress}; // multiple email addresses can be added here 
        mMail.setTo(toArr);
        mMail.setFrom(mFromAdddress);
        mMail.setSubject("Email send on the first try.");
        
        // Retrieve the user's phone number - used as a form of identification
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number(); //TODO is this right?
        String body = "Email sent from phone number: " + mPhoneNumber;
        mMail.setBody(body);
        Log.e(TAG, "6. Location of file: " + mMainDir + mInnerDir + mUniqueAudioRecording);
        try {
			mMail.addAttachment(mMainDir + mInnerDir + mUniqueAudioRecording);
		} catch (Exception e) { 
			Log.e(TAG, "Problem including an attachment " +  e.toString());
		}
    }
    
    
    /** 
     * 
     * */
    @Override
    protected Boolean doInBackground(Void... params) { 
        try { 
        	if (mMail.send()) {
        		mEmailSent = true;
        		moveFile(mMainDir + mInnerDir, mUniqueAudioRecording, mMainDir);
        		Log.e(TAG, "Email was sent");
        	} else { 
        		mEmailSent = false;
        		Log.e(TAG, "Email not sent"); 
        	}
            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(TAG, "Bad account details: " + e);
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            Log.e(TAG, " " + e);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
        	Log.e(TAG, "" + e);
            e.printStackTrace();
            return false;
        }
    } 
       
    public boolean emailSent() { 
    	return mEmailSent;
    }
    
    
    private void moveFile(String inputPath, String inputFile, String outputPath) {
    	Log.e(TAG, "inputPath: " + inputPath);
    	Log.e(TAG, "inputFile: " + inputFile);
    	Log.e(TAG, "outputPath: " + outputPath);
    	
        InputStream in = null;
        OutputStream out = null;
        try { 
            //create output directory if it doesn't exist
            File dir = new File (outputPath); 
            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + inputFile);        
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();  
        } catch (FileNotFoundException fnfe) {
            Log.e(TAG, fnfe.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
