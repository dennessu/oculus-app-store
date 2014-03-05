package com.goshop.catalog.rest.util

import com.goshop.catalog.spec.model.PageMetadata
import org.springframework.data.domain.Page

/**
 * Created by kevingu on 11/28/13.
 */
class PageMetadataUtil {

    static PageMetadata fromPage(Page page) {
        return new PageMetadata(
                number: page.number,
                size: page.size,
                totalElements: page.totalElements,
                totalPages: page.totalPages)
    }
}
