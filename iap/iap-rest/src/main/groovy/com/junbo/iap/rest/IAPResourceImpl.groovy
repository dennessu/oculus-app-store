package com.junbo.iap.rest

import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.iap.db.repo.ConsumptionRepository
import com.junbo.iap.spec.model.*
import com.junbo.iap.spec.resource.IAPResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.ws.rs.BeanParam
import javax.ws.rs.QueryParam
import javax.ws.rs.ext.Provider
/**
 * The IAPResourceImpl class.
 */
@Provider
@Component
@Scope('prototype')
@CompileStatic
class IAPResourceImpl implements IAPResource {

    private static final int PAGE_SIZE = 100

    private ConsumptionRepository consumptionRepository

    private ItemResource itemResource

    void setConsumptionRepository(ConsumptionRepository consumptionRepository) {
        this.consumptionRepository = consumptionRepository
    }

    @Override
    Promise<Results<Offer>> getOffers(@QueryParam("packageName") String packageName, @QueryParam("type") String type) {
        // todo validation

        return getItemByPackageName(packageName) { Item hostItem ->
            Results<Item> items = restClient.getItems(inAppConfiguration.getHostItemId());

            List<InAppOffer> inAppOffers = new ArrayList<InAppOffer>();
            for (Item item : items.getResults()) {
                Results<Offer> offers = restClient.getOffers(item.getId().getId());
                for (Offer offer : offers.getResults()) {
                    OfferRevision offerRevision = restClient.getOfferRevision(offer.getCurrentRevision().getId());
                    if (offerRevision != null) {
                        InAppOffer inAppOffer = ModelConverter.convertOffer(offerRevision, inAppConfiguration);
                        inAppOffers.add(inAppOffer);
                    }
                }
            }

            return inAppOffers;
        }

    }

    @Override
    Promise<Results<Entitlement>> getEntitlements(@QueryParam("packageName") String packageName,
                                                  @QueryParam("userId") UserId userId,
                                                  @BeanParam PageParam pageParam) {

    }

    @Override
    Promise<Results<Entitlement>> postPurchase(Purchase purchase) {

    }

    @Override
    Promise<Consumption> postConsumption(Consumption consumption) {
        consumptionRepository.get(consumption.trackingGuid).then { Consumption existed ->
            if (existed != null) {
                return Promise.pure(existed)
            }
            return consumptionRepository.create(consumption)
        }
    }

    Promise<Item> getItemByPackageName(String packageName) {

    }


    Promise<List<Offer>> getInAppOffers(Item hostItem, String type) {
        def itemOption = new ItemsGetOptions(
                hostItemId: hostItem.id,
                type: type,
                size: PAGE_SIZE,
                start: 0
        )

        List<Offer> offers = new ArrayList<>()
        return iteratePageRead {
            itemResource.getItems(itemOption).then { Results<Item> itemResults
                boolean hasMore = itemResults.items.size() >= itemOption.size
                itemOption.start += itemOption.size

            }
        }.syncThen {
            return offers
        }
        itemResource.getItems(itemOption).then { Results<Item> itemResults

        }
    }

    private Promise<Void> iteratePageRead(Closure<Promise<Boolean>> pageReadFunc) {
        pageReadFunc.call().then { Boolean end ->
            if (end) {
                return Promise.pure(null)
            }
            return iteratePageRead(pageReadFunc)
        }
    }

    private Promise<Offer> convertOffer(com.junbo.catalog.spec.model.offer.Offer offer, Item item) {
        Offer result = new Offer()
       // Oitem.currentRevisionId
    }
}
