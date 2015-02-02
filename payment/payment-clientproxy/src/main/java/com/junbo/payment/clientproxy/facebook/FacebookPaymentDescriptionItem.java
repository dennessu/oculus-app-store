package com.junbo.payment.clientproxy.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.QueryParam;

/**
 * Created by wenzhumac on 1/31/15.
 */
public class FacebookPaymentDescriptionItem {
    @QueryParam("offer_id")
    @JsonProperty("offer_id")
    private String offerId;
    @QueryParam("offer_name")
    @JsonProperty("offer_name")
    private String offerName;

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }
}
