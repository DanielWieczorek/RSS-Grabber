package de.wieczorek.rss.trading.business.data.ownoffer;

import java.util.List;

public class ActiveOffersResult {
    private List<ActiveOfferResultItem> offers;
    private PaginationResult paginationResponse;
    private boolean userWants2faForEditing;

    public boolean isUserWants2faForEditing() {
        return userWants2faForEditing;
    }

    public void setUserWants2faForEditing(boolean userWants2faForEditing) {
        this.userWants2faForEditing = userWants2faForEditing;
    }

    public List<ActiveOfferResultItem> getOffers() {
        return offers;
    }

    public void setOffers(List<ActiveOfferResultItem> offers) {
        this.offers = offers;
    }

    public PaginationResult getPaginationResponse() {
        return paginationResponse;
    }

    public void setPaginationResponse(PaginationResult paginationResponse) {
        this.paginationResponse = paginationResponse;
    }
}
