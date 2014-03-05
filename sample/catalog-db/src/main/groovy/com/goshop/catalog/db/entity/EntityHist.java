package com.goshop.catalog.db.entity;

/**
 * Created by kevingu on 11/21/13.
 */
public class EntityHist<E> extends Entity<EntityId> {
    @Override
    public String getEntityType() {
        return "EntityHist";
    }
}
