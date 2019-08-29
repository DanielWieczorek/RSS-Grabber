package de.wieczorek.chart.advisor.types;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TradingEvaluationResultId implements Serializable {

    private LocalDateTime targetTime;

    private LocalDateTime currentTime;

    public LocalDateTime getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(LocalDateTime targetTime) {
        this.targetTime = targetTime;
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currentTime == null) ? 0 : currentTime.hashCode());
        result = prime * result + ((targetTime == null) ? 0 : targetTime.hashCode());
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
        TradingEvaluationResultId other = (TradingEvaluationResultId) obj;
        if (currentTime == null) {
            if (other.currentTime != null) {
                return false;
            }
        } else if (!currentTime.equals(other.currentTime)) {
            return false;
        }
        if (targetTime == null) {
            if (other.targetTime != null) {
                return false;
            }
        } else if (!targetTime.equals(other.targetTime)) {
            return false;
        }
        return true;
    }

}
