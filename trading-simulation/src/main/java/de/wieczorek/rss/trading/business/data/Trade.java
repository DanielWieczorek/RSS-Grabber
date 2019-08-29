package de.wieczorek.rss.trading.business.data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import de.wieczorek.rss.trading.types.Account;
import de.wieczorek.rss.trading.types.ActionVertexType;

public class Trade {

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;
    private ActionVertexType action;
    private Account before;
    private Account after;
    private double currentRate;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public ActionVertexType getAction() {
        return action;
    }

    public void setAction(ActionVertexType action) {
        this.action = action;
    }

    public Account getBefore() {
        return before;
    }

    public void setBefore(Account before) {
        this.before = before;
    }

    public Account getAfter() {
        return after;
    }

    public void setAfter(Account after) {
        this.after = after;
    }

    public double getCurrentRate() {
        return currentRate;
    }

    public void setCurrentRate(double currentRate) {
        this.currentRate = currentRate;
    }

}
