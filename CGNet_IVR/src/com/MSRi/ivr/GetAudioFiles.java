package com.MSRi.ivr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public class GetAudioFiles extends Activity {
	private static final String TAG = "GetAudioFiles";
	  
	/** CGNet Swara's main directory with audio files. */
	private String mMainDir;
	
	/** Starts recording audio.  */
	private Button mDownloadMore;
	
	/** */
	ProgressBar progressBar;
	
	private LinearLayout layout;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_audio); 
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setProgress(0);
		progressBar.setVisibility(View.VISIBLE);
		progressBar.setIndeterminate(false);
 
		layout = (LinearLayout)  findViewById(R.id.layout);
		layout.setBackgroundColor(Color.parseColor("#c9c9c9"));
        mDownloadMore = (Button) findViewById(R.id.download_more);
        
		// Create folders for the audio files 
		setupDirectory();
        
		// 
        populateListView();
      //  progressBar.setProgress(100);
        mDownloadMore.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 		
				downloadFiles();
				Log.e(TAG, "CLICKED!");
			}  
		}); 
        
        
    }
    
    
    /** Sets up file structure for audio recordings. **/
	private void setupDirectory() {
		// This folder should have been created in MainActivity
		// This is just in case it wasn't.
		mMainDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		mMainDir += "/CGNetSwaraDownloads";
		File dir = new File(mMainDir);
		if(!dir.exists() || !dir.isDirectory()) {
		    dir.mkdir();
		}  
	}
	 
	private List<String> item = new ArrayList<String>(); 
	 
    private void populateListView() {

        ListView listViewItems = (ListView) findViewById(android.R.id.list);
        

        try {
        	   URL rssUrl = new URL("http://feeds.feedburner.com/Android-er?format=xml");
        	   SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
        	   SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
        	   XMLReader myXMLReader = mySAXParser.getXMLReader();
        	   RSSHandler myRSSHandler = new RSSHandler();
        	   myXMLReader.setContentHandler(myRSSHandler);
        	   InputSource myInputSource = new InputSource(rssUrl.openStream());
        	   myXMLReader.parse(myInputSource);
        	   
        	  } catch (MalformedURLException e) {
        	   // TODO Auto-generated catch block
        	   e.printStackTrace();
        	  } catch (ParserConfigurationException e) {
        	   // TODO Auto-generated catch block
        	   e.printStackTrace();
        	  } catch (SAXException e) {
        	   // TODO Auto-generated catch block
        	   e.printStackTrace();
        	  } catch (IOException e) {
        	   // TODO Auto-generated catch block
        	   e.printStackTrace();
        	  }
        	       
        	  ArrayAdapter<String> itemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
              listViewItems.setAdapter(itemAdapter); 
    
    	//new GetFiles(this, listViewItems).execute();
    	   
	}
    
    
    private class RSSHandler extends DefaultHandler
    {
     final int stateUnknown = 0;
     final int stateTitle = 1;
     int state = stateUnknown;
     
   @Override
   public void startDocument() throws SAXException {
    // TODO Auto-generated method stub
   }

   @Override
   public void endDocument() throws SAXException {
    // TODO Auto-generated method stub
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    // TODO Auto-generated method stub
    if (localName.equalsIgnoreCase("title"))
    {
     state = stateTitle;
    }
    else
    {
     state = stateUnknown;
    }
   }

   @Override
   public void endElement(String uri, String localName, String qName)
     throws SAXException {
    // TODO Auto-generated method stub
    state = stateUnknown;
   }

   @Override
   public void characters(char[] ch, int start, int length)
     throws SAXException {
    // TODO Auto-generated method stub
    String strCharacters = new String(ch, start, length);
    if (state == stateTitle)
    {
     item.add(strCharacters);
     
    }
   }
     
    }
    

	
    private void downloadFiles() { 
    	new DownloadAudioFile().execute(); 
    }


	/** Inflates the menu. Currently, there aren't any meaningful items
     *  to add to the action bar. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }   
    
    
    
    public class DownloadAudioFile extends AsyncTask<Void, Integer, Void> {
    	private static final String TAG = "DownloadAudioFile";
    	 
        protected Void doInBackground(Void... urls) {
        	 
        	String mMainDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    		mMainDir += "/CGNetSwaraDownloads";
        	String path = mMainDir + "/test10.wav";
        	
        	int count = 0;
        	 
         	Log.e(TAG,"?");
    		URL url;
    		try {
    			url = new URL("http://cgnetswara.org/sounds/270.wav");
    			
    	    	
    		  	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	       	int downloaded = 0;
    			if(true){
    	            File file=new File(path);
    	            if(!file.exists()) {
    	            	file.createNewFile();
    	            } 
    	            if(file.exists()){ 
    	                downloaded = (int) file.length(); 
    	                connection.setRequestProperty("Range", "bytes="+(file.length())+"-");
    	            }
    	        } //else{
    	           // connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
    	        //}
    	        connection.setDoInput(true);
    	        connection.setDoOutput(true);
    	        progressBar.setMax(connection.getContentLength()); 
    			 
    			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
    	        FileOutputStream fos = (downloaded==0)? new FileOutputStream(path): new FileOutputStream(path,true);
    	        BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
    	        byte[] data = new byte[1024];
    	        int x = 0;

    	        while ((x = in.read(data, 0, 1024)) >= 0) { 
    	            bout.write(data, 0, x);
    	            downloaded += x;   
    	            publishProgress(downloaded);
    	        }
    	        bout.close();
    		} catch(Exception e) { 
    			e.printStackTrace();
    		}
    		
    		return null; 
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
        	final ProgressBar bar = progressBar;
        	final int size = values[0];
			runOnUiThread(new Runnable() {    
				@Override
				public void run() { 
					bar.setProgress(size);  
				} 
			});  
        }  
    }
}