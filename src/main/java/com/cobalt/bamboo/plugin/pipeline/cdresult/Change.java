package com.cobalt.bamboo.plugin.pipeline.cdresult;

import java.util.Date;
import java.util.List;

public class Change {
	private String authorName;
	private String authorPictureUrl;
	private int buildNumber;
	private String comment;
	private Date date;
	private List<String> files;
	
	
	public Change(String author, String authorPictureUrl, int buildNumber, String comment, Date date, List<String> files) {
		this.authorName = author;
		this.authorPictureUrl = authorPictureUrl;
		this.comment = comment;
		this.date = date;
		this.files = files;
	}
	
	public String getAuthorName() {
		return authorName;
	}
	
	public String getAuthorPictureUrl() {
		return authorPictureUrl;
	}
	
	
	public int getBuildNumber() {
		return buildNumber;
	}
	
	public String getComment() {
		return comment;
	}
	
	public String getDateFormatted() {
		return date.toString();
	}
	
	public List<String> getFiles() {
		return files;
	}


}
