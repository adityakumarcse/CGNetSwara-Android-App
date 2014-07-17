package com.MSRi.ivr;
 
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.util.Log;
 
public class RssReader {
	private String rssUrl;

	public RssReader(String rssUrl) { 
		this.rssUrl = rssUrl;
	}

	public List<RssItem> getItems() throws Exception {  
		SAXParserFactory factory = SAXParserFactory.newInstance(); 
		SAXParser saxParser = factory.newSAXParser(); 
		RssParseHandler handler = new RssParseHandler(); 
		 
		try { 
			saxParser.parse(rssUrl, handler); 
		} catch(Exception e) { 
			e.printStackTrace();
		}

		return handler.getItems();
	}
}