package de.wieczorek.rss.insight.business;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rss_entries")
public class RssEntry {

    private String feedUrl;
    private String heading;
    @Column(length = 1024)
    private String description;
    private Date publicationDate;
    @Id
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

}
