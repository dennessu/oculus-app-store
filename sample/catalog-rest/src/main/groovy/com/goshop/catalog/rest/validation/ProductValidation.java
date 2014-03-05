package com.goshop.catalog.rest.validation;

import com.goshop.catalog.common.CommonUtil;
import com.goshop.catalog.common.error.AppError;
import com.goshop.catalog.common.error.AppErrors;
import com.goshop.catalog.db.dao.ProductEntityDAO;
import com.goshop.catalog.db.entity.Entity;
import com.goshop.catalog.db.entity.EntityStatus;
import com.goshop.catalog.db.entity.attribute.AttributeEntity;
import com.goshop.catalog.db.entity.category.CategoryEntity;
import com.goshop.catalog.db.entity.product.PriceEntity;
import com.goshop.catalog.db.entity.product.ProductEntity;
import com.goshop.catalog.db.entity.product.SkuEntity;
import com.goshop.catalog.rest.resolver.ProductPredicateResolver;
import com.goshop.catalog.rest.resource.CommonResource;
import com.goshop.catalog.spec.model.product.Price;
import groovy.util.MapEntry;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

public class ProductValidation extends CommonResource implements Validation {
    private ProductEntityDAO productEntityDAO;

    @Required
    public void setProductEntityDAO(ProductEntityDAO productEntityDAO) {
        this.productEntityDAO = productEntityDAO;
    }

    @Override
    public AppError validateCreation(Entity entity) {
        if(entity == null) {
            return AppErrors.INSTANCE.invalidNullInputParam();
        }

        if(entity.getEntityId() != null && !StringUtils.isEmpty(entity.getEntityId().toString())) {
            return AppErrors.INSTANCE.objectIdInvalid(entity.getEntityId().toString(), "EntityId should be empty during creation.");
        }

        return validate(entity);
    }

    @Override
    public AppError validateUpdate(Entity entityToUpdate, Entity entityUpdate) {
        if(entityToUpdate.getEntityId().toString().equals(entityUpdate.getEntityId())) {
            return AppErrors.INSTANCE.objectIdMissMatch(entityToUpdate.getEntityId().toString(), entityUpdate.getEntityId().toString());
        }

        return validate(entityUpdate);
    }

    @Override
    public AppError validateRelease(Entity entity) {
        if(!entity.getStatus().equalsIgnoreCase(EntityStatus.DRAFT)) {
            return AppErrors.INSTANCE.draftNotFound(entity.getEntityId().toString(), "Status : " + EntityStatus.DRAFT);
        }

        // check StoreId & category ID exists
        ProductEntity productEntity = (ProductEntity)entity;
        if(getCategoryByStatus(productEntity.getCategoryId().toString(), EntityStatus.RELEASED) == null) {
            return AppErrors.INSTANCE.objectIdNotFound(productEntity.getCategoryId().toString(), "Category: status : " + EntityStatus.RELEASED);
        }

        // todo:    check StoreId (may be it can be related to user)
        return validate(entity);
    }

    @Override
    public AppError validateDelete(Entity entity) {
        // Do nothing here
        return null;
    }

    @Override
    public AppError validate(Entity entity) {
        ProductEntity productEntity = (ProductEntity)entity;

        // check category
        // We won't allow the product without category
        if(productEntity.getCategoryId() != null && !StringUtils.isEmpty(productEntity.getCategoryId().toObjectId())) {
            CategoryEntity categoryEntity = getCategoryByStatus(productEntity.getCategoryId().toString(), EntityStatus.RELEASED);
            if(categoryEntity == null) {
                return AppErrors.INSTANCE.objectIdNotFound(productEntity.getCategoryId().toString(), "status : " + EntityStatus.RELEASED);
            }
        } else{
            return AppErrors.INSTANCE.objectIdMissing("categoryId : " + productEntity.getCategoryId());
        }

        // check SKU
        if(!CollectionUtils.isEmpty(productEntity.getSkus())){
            List<String> uniqueSkuEntityIds = new ArrayList<String>();
            List<String> uniqueSkuEntityAttributes = new ArrayList<String>();
            for(SkuEntity skuEntity : productEntity.getSkus()) {
                if(skuEntity == null)   continue;
                // Check Id Unique
                if(skuEntity.getId() != null && !StringUtils.isEmpty(skuEntity.getId().toString())) {
                    if(uniqueSkuEntityIds.contains(skuEntity.getId().toString())) {
                        return AppErrors.INSTANCE.objectIdDuplicate(skuEntity.getId().toString());
                    } else {
                        uniqueSkuEntityIds.add(skuEntity.getId().toString());
                    }
                }
                // Check property unique
                String skuUniquePropertyStr = calcSkuUnique(skuEntity);
                if(uniqueSkuEntityAttributes.contains(skuUniquePropertyStr)) {
                    return AppErrors.INSTANCE.skuPropertyDuplicate(skuUniquePropertyStr);
                } else {
                    uniqueSkuEntityAttributes.add(skuUniquePropertyStr);
                }
                //todo: check Attribute local valid
                //todo: check Attribute currency valid

                // Check prices:
                // 1):  Check the same currency without any overlap time;
                // 2):  The currency without any startDate and endDate will be treated as always useful
                if(!CollectionUtils.isEmpty(skuEntity.getPrices())) {
                    Map<String, List<PriceEntity>> priceMap = CommonUtil.toMapList(skuEntity.getPrices(), new CommonUtil.IGetKey<String, PriceEntity>() {
                        @Override
                        public String getKey(PriceEntity obj) {
                            return obj.getCurrency();
                       }
                    });

                    Iterator it = priceMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry)it.next();
                        List<PriceEntity> priceEntities = (List<PriceEntity>)entry.getValue();
                        Collections.sort(priceEntities, new Comparator<PriceEntity>() {
                            @Override
                            public int compare(PriceEntity o1, PriceEntity o2) {
                                return o1.getStartEffectiveDate().compareTo(o2.getStartEffectiveDate());
                            }
                        });
                        for(PriceEntity priceEntity : priceEntities) {
                            // check each price entry's date validation.
                            if(priceEntity.getStartEffectiveDate().compareTo(priceEntity.getEndEffectiveDate()) > 0) {
                                return AppErrors.INSTANCE.priceDateInvalid(priceEntity.getStartEffectiveDate(), priceEntity.getEndEffectiveDate());
                            }

                            // check each price has no overlap
                            for(PriceEntity newEntity : priceEntities) {
                                if(priceEntity == newEntity)    continue;
                                if((newEntity.getStartEffectiveDate().after(priceEntity.getStartEffectiveDate()) && newEntity.getStartEffectiveDate().before(priceEntity.getEndEffectiveDate()))
                                || (newEntity.getEndEffectiveDate().after(priceEntity.getStartEffectiveDate()) && newEntity.getEndEffectiveDate().before(priceEntity.getEndEffectiveDate()))
                                ) {
                                    return AppErrors.INSTANCE.priceEffectiveDateOverlap(priceEntity.getStartEffectiveDate(), priceEntity.getEndEffectiveDate(),
                                            newEntity.getStartEffectiveDate(), newEntity.getEndEffectiveDate());
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private String calcSkuUnique(SkuEntity skuEntity) {
        if(CollectionUtils.isEmpty(skuEntity.getAttributes()))  return "";
        Collections.sort(skuEntity.getAttributes(), new Comparator<AttributeEntity>() {
            @Override
            public int compare(AttributeEntity o1, AttributeEntity o2) {
                if(compareString(o1.getKey(), o2.getKey()) != 0) {
                    return compareString(o1.getKey(), o2.getKey());
                }
                if(compareString(o1.getValue(), o2.getValue()) != 0) {
                    return compareString(o1.getValue(), o2.getValue());
                }
                if(compareString(o1.getCountry(), o2.getCountry()) != 0) {
                    return compareString(o1.getCountry(), o2.getCountry());
                }
                return compareString(o1.getLocale(), o2.getLocale());
            }
        });

        return skuEntity.getAttributes().toString();
    }

    private int compareString(String s1, String s2) {
        if(StringUtils.isEmpty(s1) && StringUtils.isEmpty(s2))  return 0;

        if(!StringUtils.isEmpty(s1)) return s1.compareToIgnoreCase(s2);
        else return s2.compareToIgnoreCase(s1);
    }
}