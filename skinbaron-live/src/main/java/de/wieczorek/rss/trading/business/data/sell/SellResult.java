package de.wieczorek.rss.trading.business.data.sell;

import java.util.List;

public class SellResult {
    private long id;
    private String securityKey;
    private boolean extendedInfoProcessed;
    private String message;
    private List<SellError> errors;
    private String flashErrorMessage;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFlashErrorMessage() {
        return flashErrorMessage;
    }

    public void setFlashErrorMessage(String flashErrorMessage) {
        this.flashErrorMessage = flashErrorMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SellError> getErrors() {
        return errors;
    }

    public void setErrors(List<SellError> errors) {
        this.errors = errors;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public boolean isExtendedInfoProcessed() {
        return extendedInfoProcessed;
    }

    public void setExtendedInfoProcessed(boolean extendedInfoProcessed) {
        this.extendedInfoProcessed = extendedInfoProcessed;
    }
}
