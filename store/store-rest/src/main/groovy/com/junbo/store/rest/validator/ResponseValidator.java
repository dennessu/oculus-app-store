/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.validator;

import com.junbo.store.spec.model.browse.*;

/**
 * The ResponseValidator class.
 */
public interface ResponseValidator {

    void validateLibraryResponse(LibraryResponse libraryResponse);

    void validateTocResponse(TocResponse tocResponse);

    void validateSectionLayoutResponse(SectionLayoutResponse sectionLayoutResponse);

    void validateListResponse(ListResponse listResponse);

    void validateItemDetailsResponse(DetailsResponse detailsResponse);
}
