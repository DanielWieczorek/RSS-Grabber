package de.wieczorek.rss.insight.business;

import java.util.Date;

public class RssEntry {

    private String feedUrl;
    private String heading;
    private String description;
    private Date publicationDate;
    private Date createdAt;
    private String URI;

    private int classification;

    public String getHeading() {
	return heading;
    }

    public void setHeading(String heading) {
	this.heading = heading;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getURI() {
	return URI;
    }

    public void setURI(String uRI) {
	URI = uRI;
    }

    public String getFeedUrl() {
	return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
	this.feedUrl = feedUrl;
    }

    public Date getPublicationDate() {
	return publicationDate;
    }

    public void setPublicationDate(Date localDateTime) {
	this.publicationDate = localDateTime;
    }

    public int getClassification() {
	return classification;
    }

    public void setClassification(int classification) {
	this.classification = classification;
    }

    public Date getCreatedAt() {
	return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
	this.createdAt = createdAt;
    }

}
