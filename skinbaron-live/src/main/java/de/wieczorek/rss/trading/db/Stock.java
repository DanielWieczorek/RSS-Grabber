package de.wieczorek.rss.trading.db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    private long metaofferid;

    @Id
    private LocalDateTime time;

    private String name;

    private int amount;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
