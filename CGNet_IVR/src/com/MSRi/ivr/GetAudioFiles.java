package com.MSRi.ivr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.MSRi.ivr.LoadFiles.LoadFeed;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
  
	private void populateListView() {
		ListView listViewItems = (ListView) findViewById(android.R.id.list);
		LoadFiles lf = new LoadFiles(listViewItems, this);
		lf.getFiles(); 
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