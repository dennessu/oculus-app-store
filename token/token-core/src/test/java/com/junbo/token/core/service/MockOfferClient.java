package com.junbo.token.core.service;

import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import com.junbo.token.common.exception.AppClientExceptions;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 14-7-2.
 */
public class MockOfferClient implements OfferResource {
    public static List<String> validOffers = Arrays.asList("123","1234", "12345");
    @Override
    public Promise<Results<Offer>> getOffers(@BeanParam OffersGetOptions options) {
        return null;
    }

    @Override
    public Promise<Offer> getOffer(String offerId) {
        if(validOffers.contains(offerId)){
            return Promise.pure(new Offer());
        }else{
            throw AppClientExceptions.INSTANCE.offerNotFound(offerId).exception();
        }
    }

    @Override
    public Promise<Offer> create(Offer offer) {
        return null;
    }

    @Override
    public Promise<Offer> update(String offerId, Offer offer) {
        return null;
    }

    @Override
    public Promise<Response> delete(String offerId) {
        return null;
    }
}
