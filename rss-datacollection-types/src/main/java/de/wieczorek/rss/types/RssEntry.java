package de.wieczorek.rss.types;

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
    @Column(name = "CREATEDAT")
    private Date createdAt;
    @Id
    private String URI;

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

    public Date getCreatedAt() {
	return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
	this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((URI == null) ? 0 : URI.hashCode());
	result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
	result = prime * result + ((description == null) ? 0 : description.hashCode());
	result = prime * result + ((feedUrl == null) ? 0 : feedUrl.hashCode());
	result = prime * result + ((heading == null) ? 0 : heading.hashCode());
	result = prime * result + ((publicationDate == null) ? 0 : publicationDate.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	RssEntry other = (RssEntry) obj;
	if (URI == null) {
	    if (other.URI != null) {
		return false;
	    }
	} else if (!URI.equals(other.URI)) {
	    return false;
	}
	if (createdAt == null) {
	    if (other.createdAt != null) {
		return false;
	    }
	} else if (!createdAt.equals(other.createdAt)) {
	    return false;
	}
	if (description == null) {
	    if (other.description != null) {
		return false;
	    }
	} else if (!description.equals(other.description)) {
	    return false;
	}
	if (feedUrl == null) {
	    if (other.feedUrl != null) {
		return false;
	    }
	} else if (!feedUrl.equals(other.feedUrl)) {
	    return false;
	}
	if (heading == null) {
	    if (other.heading != null) {
		return false;
	    }
	} else if (!heading.equals(other.heading)) {
	    return false;
	}
	if (publicationDate == null) {
	    if (other.publicationDate != null) {
		return false;
	    }
	} else if (!publicationDate.equals(other.publicationDate)) {
	    return false;
	}
	return true;
    }

}
