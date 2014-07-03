package com.MSRi.ivr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
 

public class SendAudioFile {

	public SendAudioFile(Context context, String outerDir, String innerDir, String fileName) { 
		
		
	
	}
	
	public void sendEmail() { 
		
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
