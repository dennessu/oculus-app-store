package com.junbo.store.clientproxy.rating;

import com.junbo.common.id.OfferId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.rating.spec.model.priceRating.RatingItem;
import com.junbo.store.spec.model.ApiContext;

/**
 * The PriceRatingFacade class.
 */
public interface PriceRatingFacade {

    Promise<RatingItem> rateOffer(OfferId offerId, ApiContext apiContext);
}
