package de.wieczorek.chart.core.persistence;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "session")
public class Session {

    @Id
    private String username;
    private String token;

    private LocalDateTime expirationDate;

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    public LocalDateTime getExpirationDate() {
	return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
	this.expirationDate = expirationDate;
    }

}
