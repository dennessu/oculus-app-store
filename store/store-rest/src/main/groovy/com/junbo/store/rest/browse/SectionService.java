package com.junbo.store.rest.browse;

import com.junbo.store.spec.model.browse.document.SectionInfoNode;

import java.util.List;

/**
 * The SectionService class.
 */
public interface SectionService {

    List<SectionInfoNode> getTopLevelSectionInfoNode();

    SectionInfoNode  getSectionInfoNode(String category, String criteria);

}
