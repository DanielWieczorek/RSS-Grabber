package de.wieczorek.rss.trading.db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "price")
public class Price {
    double minimum;
    @Id
    private long metaofferid;
    @Id
    private LocalDateTime time;


    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    public long getMetaofferid() {
        return metaofferid;
    }

    public void setMetaofferid(long metaofferid) {
        this.metaofferid = metaofferid;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
