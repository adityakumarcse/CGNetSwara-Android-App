package com.MSRi.ivr;

public class RssItem {
	
	private String mTitle;
	
	private String mDate;
	
	private String mAuthor;
	
	private String mDescription;
	
	private String mImage;
	
	private String mAudio;

	public String postDate;

	public String postTitle;

	public String postLink;
	
	public void setTitle(String title) { 
		mTitle = title;
	}
	
	public void setDate(String date) { 
		mDate = date;
	}
	
	public void setAuthor(String author) { 
		mAuthor = author;
	}
	
	public void setDescription(String description) { 
		mDescription = description;
	}
	
	public void setImage(String image) { 
		mImage = image;
	}
	
	public void setAudio(String audio) { 
		mAudio = audio;
	}
	
	public String getTitle() { 
		return mTitle;
	}
	
	public String getDate() { 
		return mDate;
	}
	
	public String getAuthor() { 
		return mAuthor;
	}
	
	public String getDescription() { 
		return mDescription;
	}
	
	public String getImage() { 
		return mImage;
	}
	
	public String getAudiO() { 
		return mAudio;
	}
} 