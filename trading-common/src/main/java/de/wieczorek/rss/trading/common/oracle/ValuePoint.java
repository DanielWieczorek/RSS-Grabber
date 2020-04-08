package de.wieczorek.rss.trading.common.oracle;

import java.util.Objects;

public class ValuePoint {


    private int averageTime;
    private int offset;

    public int getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(int averageTime) {
        this.averageTime = averageTime;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValuePoint that = (ValuePoint) o;
        return averageTime == that.averageTime &&
                offset == that.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(averageTime, offset);
    }
}
