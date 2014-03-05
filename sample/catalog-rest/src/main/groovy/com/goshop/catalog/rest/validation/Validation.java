package com.goshop.catalog.rest.validation;

import com.goshop.catalog.common.error.AppError;
import com.goshop.catalog.db.entity.Entity;

public interface Validation{
    AppError validateCreation(Entity entity);

    AppError validateUpdate(Entity entityToUpdate, Entity entityUpdate);

    AppError validateRelease(Entity entity);

    AppError validateDelete(Entity entity);

    AppError validate(Entity entity);
}
