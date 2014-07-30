/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import com.junbo.common.id.ItemId;
import com.junbo.store.spec.model.Offer;
import com.junbo.store.spec.model.browse.Image;

import java.util.List;

/**
 * The Document class.
 */
public class Document {

    private String docId;
    private String documentType;
    private ContainerMetadata containerMetadata;
    private List<Document> child;
    private String title;
    private String subtitle;
    private String descriptionHtml;
    private String reviewUrl;
    private String author;
    private ItemId itemId;
    private DocumentDetails details;
    private String detailsUrl;
    private List<Image> images;
    private List<Offer> offers;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public ContainerMetadata getContainerMetadata() {
        return containerMetadata;
    }

    public void setContainerMetadata(ContainerMetadata containerMetadata) {
        this.containerMetadata = containerMetadata;
    }

    public List<Document> getChild() {
        return child;
    }

    public void setChild(List<Document> child) {
        this.child = child;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }

    public void setReviewUrl(String reviewUrl) {
        this.reviewUrl = reviewUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }

    public DocumentDetails getDetails() {
        return details;
    }

    public void setDetails(DocumentDetails details) {
        this.details = details;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}
