package com.junbo.token.core.service;

import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import com.junbo.catalog.spec.resource.PromotionResource;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import com.junbo.token.common.exception.AppClientExceptions;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 14-7-9.
 */
public class MockPromotionClient implements PromotionResource {
    public static List<String> validPromotions = Arrays.asList("123", "1234", "12345");
    @Override
    public Promise<Results<Promotion>> getPromotions(@BeanParam PromotionsGetOptions options) {
        return null;
    }

    @Override
    public Promise<Promotion> getPromotion(String promotionId) {
        if(validPromotions.contains(promotionId)){
            return Promise.pure(new Promotion());
        }else{
            throw AppClientExceptions.INSTANCE.resourceNotFound("Promotion").exception();
        }
    }

    @Override
    public Promise<Promotion> create(Promotion promotion) {
        return null;
    }

    @Override
    public Promise<Promotion> update(String promotionId, Promotion promotion) {
        return null;
    }

    @Override
    public Promise<Response> delete(String promotionId) {
        return null;
    }
}
