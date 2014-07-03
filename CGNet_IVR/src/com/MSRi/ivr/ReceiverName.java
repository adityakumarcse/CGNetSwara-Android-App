package com.MSRi.ivr;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class ReceiverName extends BroadcastReceiver {
	private static final String TAG = "ReceiverName";
	String filePath;
	
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm == null)
            return;
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
    		String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
    		mFileName += "/CGNetSwara/To_Be_Sent";
        	  File dir = new File(mFileName);
        	  File[] directoryListing = dir.listFiles();
        	  if (directoryListing != null) {
        	    for (File child : directoryListing) {
        	    	filePath = child.toString();
        			SendEmailAsyncTask task = new SendEmailAsyncTask();
        			task.execute(); 
        			if(task.emailSent()) { 
        				child.delete();
        			}
        	    }
        	  } else {
        	    // Handle the case where dir is not really a directory.
        	    // Checking dir.isDirectory() above would not be sufficient
        	    // to avoid race conditions with another process that deletes
        	    // directories.
        	  }
        	Log.e(TAG, "in here");
        } else {
        	Log.e(TAG, "I think this means that the internet changed and now there's no wifi");
        }

    }
    
    
    class SendEmailAsyncTask extends AsyncTask <Void, Void, Boolean> {
		String TAG = "SendEmailAsyncTask";  		
	    Mail m = new Mail("cgnet112358@gmail.com", "cgnetswara");
	    private boolean mEmailSent;
	    
	    public SendEmailAsyncTask() {
	        if (BuildConfig.DEBUG) { 
	        	Log.v(SendEmailAsyncTask.class.getName(), "SendEmailAsyncTask()");
	        }
	        String[] toArr = { "krittika.dsilva@gmail.com"};
	        m.setTo(toArr);
	        m.setFrom("cgnet112358@gmail.com");
	        m.setSubject("Email send on the second or future try");
	        
	     //   TelephonyManager tMgr = (TelephonyManager) RecordAudio.getSystemService(Context.TELEPHONY_SERVICE);
	      //  String mPhoneNumber = tMgr.getLine1Number();
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	        String currentDateandTime = sdf.format(new Date());
	         
	        String body = "Email sent from phone number: TODO" ;
	        body += "Time/data sent at: " + currentDateandTime;
	        
	        m.setBody(body);
	        
	        try {
				m.addAttachment(filePath);
			} catch (Exception e) {
				Log.e(TAG, "" + e);
				e.printStackTrace();
			}
	    }

	    @Override
	    protected Boolean doInBackground(Void... params) {
	        if (BuildConfig.DEBUG) { 
	        	Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
	        }
	        try { 
	        	if (m.send()) { 
	        		mEmailSent = true;
	        		File dir = new File(filePath);
	        		dir.delete();
	        		Log.e(TAG, "email sent");
	        	} else { 
	        		mEmailSent = false;
	        		Log.e(TAG, "no internet, email cannot be sent"); 
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
	    
	    private boolean emailSent() { 
	    	return mEmailSent;
	    }
	    

	}
 
}