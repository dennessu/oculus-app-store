/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.utility;

import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.common.AgeRating;
import com.junbo.catalog.spec.model.item.*;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.store.spec.model.browse.AddReviewRequest;
import com.junbo.store.spec.model.browse.Images;
import com.junbo.store.spec.model.browse.ListResponse;
import com.junbo.store.spec.model.browse.SectionLayoutResponse;
import com.junbo.store.spec.model.browse.document.*;
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.casey.CaseyReview;
import com.junbo.test.catalog.enums.PriceType;
import com.junbo.test.common.Validator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.testng.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The StoreBrowseValidationHelper class.
 */
public class StoreBrowseValidationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreBrowseValidationHelper.class);

    private static final String Platform = "ANDROID";

    private static final String locale = "en_US";

    private static final String country = "US";

    private static final Pattern ImageDimensionTextPattern = Pattern.compile("\\s*(\\d+)\\s*[xX]\\s*(\\d+)\\s*");

    private static final TreeMap<Integer, String> lengthToImageSizeGroup = new TreeMap<>();

    private StoreTestDataProvider storeTestDataProvider;

    interface VerifyEqual<T> {
        void verify(T o1, T o2);
    }

    static {
        lengthToImageSizeGroup.put(2560, "large");
        lengthToImageSizeGroup.put(1440, "medium");
        lengthToImageSizeGroup.put(690, "small");
        lengthToImageSizeGroup.put(336, "tiny");
    }

    public StoreBrowseValidationHelper(StoreTestDataProvider storeTestDataProvider) {
        this.storeTestDataProvider = storeTestDataProvider;
    }

    public void verifySectionLayoutBreadcrumbs(SectionLayoutResponse layout, SectionLayoutResponse parentLayout, SectionInfo parentSection) throws Exception {
        if (parentLayout != null) {
            Assert.assertEquals(layout.getBreadcrumbs().size(), parentLayout.getBreadcrumbs().size() + 1, "Breadcrumbs length not corret");
            for (int i = 0;i < parentLayout.getBreadcrumbs().size(); ++i) {
                Validator.Validate("node in breadcrumbs not equal", layout.getBreadcrumbs().get(i), parentLayout.getBreadcrumbs().get(i));
            }
        }

        if (parentSection != null) {
            SectionInfo last = layout.getBreadcrumbs().get(layout.getBreadcrumbs().size() - 1);
            Assert.assertEquals(last.getCriteria(), parentSection.getCriteria());
            Assert.assertEquals(last.getCategory(), parentSection.getCategory());
            Assert.assertEquals(last.getName(), parentSection.getName());
        }
    }

    public void verifyItemsInList(List<String> names, final List<com.junbo.store.spec.model.browse.document.Item> items, boolean verifySameSize) {
        for (final String name : names) {
            Object result = CollectionUtils.find(items, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    com.junbo.store.spec.model.browse.document.Item item = (com.junbo.store.spec.model.browse.document.Item) object;
                    return name.equals(item.getTitle());
                }
            });
            Assert.assertNotNull(result, String.format("Item %s not in the list.", name));
        }

        if (verifySameSize) {
            Assert.assertEquals(names.size(), items.size(), "Size of list not equals.");
        }
    }

    public void verifyReview(Review review, CaseyReview caseyReview, String expectedName) {
        Assert.assertEquals(review.getAuthorName(), expectedName);
        Assert.assertEquals(review.getContent(), caseyReview.getReview());
        Assert.assertEquals(review.getTitle(), caseyReview.getReviewTitle());
        Assert.assertEquals(review.getStarRatings().size(), caseyReview.getRatings().size());
        for (CaseyReview.Rating rating : caseyReview.getRatings()) {
            Assert.assertEquals(review.getStarRatings().get(rating.getType()).intValue(), rating.getScore() / 20, "rating result not correct");
        }
    }

    public void verifyAggregateRatings(Map<String, AggregatedRatings> aggregatedRatings, List<CaseyAggregateRating> caseyAggregatedRatings) {
        for (final Map.Entry<String, AggregatedRatings> entry : aggregatedRatings.entrySet()) {
            CaseyAggregateRating caseyAggregateRating = (CaseyAggregateRating) CollectionUtils.find(caseyAggregatedRatings, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return ObjectUtils.nullSafeEquals(((CaseyAggregateRating) object).getType(), entry.getKey());
                }
            });
            AggregatedRatings rating = entry.getValue();
            Assert.assertEquals(rating.getAverageRating(), caseyAggregateRating.getAverage() / 20, 0.00001, "average rating not correct");
            Assert.assertEquals(rating.getRatingsCount(), caseyAggregateRating.getCount(), "rating count not correct");
            Assert.assertNull(rating.getCommentsCount(), "comments count should be null");

            for (int i = 0;i < caseyAggregateRating.getHistogram().length; i += 2) {
                Assert.assertEquals(rating.getRatingsHistogram().get(i / 2).longValue(),
                        (caseyAggregateRating.getHistogram()[i] + caseyAggregateRating.getHistogram()[i + 1]), "rating histogram not correct");
            }
        }
    }

    public void verifyItem(com.junbo.store.spec.model.browse.document.Item item, boolean serviceClientEnabled) throws Exception {
        OfferRevision currentOfferRevision = null;
        ItemRevision currentItemRevision = null;
        com.junbo.catalog.spec.model.offer.Offer catalogOffer =
                storeTestDataProvider.getOfferByOfferId(item.getOffer().getSelf().getValue());
        OfferRevision offerRevision = storeTestDataProvider.getOfferRevision(item.getOffer().getCurrentRevision().getValue());
        com.junbo.catalog.spec.model.item.Item catalogItem = storeTestDataProvider.getItemByItemId(item.getSelf().getValue());
        ItemRevision itemRevision = storeTestDataProvider.getItemRevision(item.getCurrentRevision().getValue());
        List<OfferAttribute> offerAttributes = new ArrayList<>();
        List<ItemAttribute> itemAttributes = new ArrayList<>();
        List<OfferRevision> offerRevisions = getOfferRevisions(catalogOffer);
        List<ItemRevision> itemRevisions =  Arrays.asList(itemRevision);//getItemRevisions(catalogItem); // todo may return all the item revisions

        if (!org.springframework.util.CollectionUtils.isEmpty(catalogOffer.getCategories())) {
            for (String id : catalogOffer.getCategories()) {
                offerAttributes.add(storeTestDataProvider.getOfferAttribute(id));
            }
        }
        if (!org.springframework.util.CollectionUtils.isEmpty(catalogItem.getGenres())) {
            for (String id: catalogItem.getGenres()) {
                itemAttributes.add(storeTestDataProvider.getItemAttribute(id));
            }
        }
        for (OfferRevision revision : offerRevisions) {
            if (ObjectUtils.nullSafeEquals(revision.getId(), catalogOffer.getCurrentRevisionId())) {
                currentOfferRevision = revision;
                break;
            }
        }
        for (ItemRevision revision : itemRevisions) {
            if (ObjectUtils.nullSafeEquals(revision.getId(), catalogItem.getCurrentRevisionId())) {
                currentItemRevision = revision;
                break;
            }
        }

        ItemRevisionLocaleProperties localeProperties = currentItemRevision.getLocales().get(locale);
        Organization developer = getOrganization(catalogItem.getOwnerId(), serviceClientEnabled);
        Organization publisher = getOrganization(catalogOffer.getOwnerId(), serviceClientEnabled);

        verifyItem(item, catalogItem, currentItemRevision, developer, serviceClientEnabled);
        verifyItemImages(item.getImages(), localeProperties.getImages());
        verifyAppDetails(item.getAppDetails(), offerAttributes, itemAttributes, currentOfferRevision, currentItemRevision, itemRevisions,
                developer, publisher, serviceClientEnabled);
        boolean isFree = PriceType.FREE.name().equals(offerRevision.getPrice().getPriceType());
        verifyItemOffer(item.getOffer(), catalogOffer, offerRevision, isFree);
    }

    public void validateCmsSection(SectionInfoNode sectionInfoNode, String name, String cmsPageName, String slot) {
        Assert.assertEquals(sectionInfoNode.getCriteria(), cmsPageName + "-" + slot);
        Assert.assertEquals(sectionInfoNode.getName(), name);
        Assert.assertNull(sectionInfoNode.getCategory());
    }

    public void validateCmsSection(SectionInfo sectionInfo, String name, String cmsPageName, String slot) {
        Assert.assertEquals(sectionInfo.getCriteria(), cmsPageName + "-" + slot);
        Assert.assertEquals(sectionInfo.getName(), name);
        Assert.assertNull(sectionInfo.getCategory());
    }

    public void validateCmsSection(SectionLayoutResponse sectionLayoutResponse, String name, int numOfItems, boolean hasMoreItems) {
        Assert.assertEquals(sectionLayoutResponse.getName(), name);
    }

    public void getAndValidateItemList(String category, String criteria, String cursor, int pageSize, int expectedOfItems, boolean hasNext) throws Exception {
        ListResponse response = storeTestDataProvider.getList(category, criteria, cursor, pageSize);
        Assert.assertEquals(response.getItems().size(), expectedOfItems);
        if (hasNext) {
            Assert.assertNotNull(response.getNext().getCursor());
            Assert.assertEquals(response.getNext().getCriteria(), criteria);
            Assert.assertEquals(response.getNext().getCategory(), category);
            Assert.assertEquals(response.getNext().getCount().intValue(), pageSize);
        } else {
            Assert.assertNull(response.getNext());
        }
    }


    public void validateCmsTopLevelSectionLayout(SectionLayoutResponse sectionLayoutResponse, int expectedNumOfChild, String expectedName) {
        Assert.assertTrue(sectionLayoutResponse.getBreadcrumbs().isEmpty(), "top level section's breadcrumbs should be empty");
        Assert.assertEquals(sectionLayoutResponse.getChildren().size(), expectedNumOfChild);
        Assert.assertEquals(sectionLayoutResponse.getName(), expectedName);
    }


    public void validateAddReview(AddReviewRequest addReviewRequest, Review review, String nickName) {
        Assert.assertEquals(review.getContent(), addReviewRequest.getContent());
        Assert.assertEquals(review.getTitle(), addReviewRequest.getTitle());
        Assert.assertEquals(review.getAuthorName(), nickName);
        Assert.assertEquals(review.getStarRatings(), addReviewRequest.getStarRatings());
        Assert.assertNotNull(review.getSelf());
        Assert.assertNotNull(review.getTimestamp());
    }

    private void verifyItem(com.junbo.store.spec.model.browse.document.Item item, Item catalogItem, ItemRevision itemRevision,
                            Organization developer, boolean serviceClientEnabled) {
        Assert.assertEquals(item.getSelf(), new ItemId(catalogItem.getItemId()));
        Assert.assertEquals(item.getItemType(), catalogItem.getType());
        ItemRevisionLocaleProperties localeProperties = itemRevision.getLocales().get(locale);
        Assert.assertEquals(item.getTitle(), localeProperties.getName(), "item title not match");
        Assert.assertEquals(item.getDescriptionHtml(), localeProperties.getLongDescription(), "description html not match");
        verifySupportedLocaleEquals(item.getSupportedLocales(), itemRevision.getSupportedLocales());

        if (serviceClientEnabled) {
            Assert.assertEquals(item.getCreator(), developer == null ? null : developer.getName());
        }
    }

    private void verifyItemImages(Images images, com.junbo.catalog.spec.model.common.Images catalogImages) {
        if (catalogImages == null) {
            Assert.assertNull(images);
            return ;
        }
        verifyImageMap(images.getBackground(), catalogImages.getBackground());
        verifyImageMap(images.getFeatured(), catalogImages.getFeatured());
        verifyImageMap(images.getMain(), catalogImages.getMain());
        verifyImageMap(images.getThumbnail(), catalogImages.getThumbnail());
        if (catalogImages.getGallery() == null) {
            Assert.assertNull(images.getGallery());
        } else {
            Assert.assertEquals(images.getGallery().size(), catalogImages.getGallery().size());
            for (int i = 0;i < images.getGallery().size();++i) {
                verifyImageMap(images.getGallery().get(i).getThumbnail(), catalogImages.getGallery().get(i).getThumbnail());
                verifyImageMap(images.getGallery().get(i).getFull(), catalogImages.getGallery().get(i).getFull());
            }
        }
    }

    private void verifyAppDetails(AppDetails appDetails, List<OfferAttribute> categories, List<ItemAttribute> genres,
                                  OfferRevision offerRevision, ItemRevision itemRevision, List<ItemRevision> itemRevisions,
                                  Organization developer, Organization publisher, boolean serviceClientEnabled) {
        // verify categories
        ItemRevisionLocaleProperties localeProperties = itemRevision.getLocales().get(locale);
        if (categories == null) {
            Assert.assertTrue(appDetails.getCategories().isEmpty());
        } else {
            Assert.assertEquals(appDetails.getCategories().size(), categories.size());
            for (int i = 0; i < appDetails.getCategories().size(); ++i) {
                Assert.assertEquals(appDetails.getCategories().get(i).getId(), categories.get(i).getId());
                Assert.assertEquals(appDetails.getCategories().get(i).getName(), categories.get(i).getLocales().get(locale).getName());
            }
        }

        // verify genres
        if (genres == null) {
            Assert.assertTrue(appDetails.getGenres().isEmpty());
        } else {
            Assert.assertEquals(appDetails.getGenres().size(), genres.size());
            for (int i = 0; i < appDetails.getGenres().size(); ++i) {
                Assert.assertEquals(appDetails.getGenres().get(i).getId(), genres.get(i).getId());
                Assert.assertEquals(appDetails.getGenres().get(i).getName(), genres.get(i).getLocales().get(locale).getName());
            }
        }

        if (serviceClientEnabled) {
            Assert.assertEquals(appDetails.getPublisherName(), publisher == null ? null : publisher.getName());
            Assert.assertEquals(appDetails.getDeveloperName(), developer == null ? null : developer.getName());
        }

        Assert.assertEquals(appDetails.getWebsite(), localeProperties.getWebsite());
        Assert.assertEquals(appDetails.getForumUrl(), localeProperties.getCommunityForumLink());
        Assert.assertEquals(appDetails.getDeveloperEmail(), localeProperties.getSupportEmail());
        Assert.assertEquals(appDetails.getPackageName(), itemRevision.getPackageName());

        if (itemRevision.getBinaries() != null && itemRevision.getBinaries().get(Platform) != null) {
            Binary binary = itemRevision.getBinaries().get(Platform);
            Assert.assertEquals(appDetails.getVersionString(), binary.getVersion());
            Assert.assertEquals(appDetails.getInstallationSize(), binary.getSize());
            Assert.assertEquals(appDetails.getVersionCode(), binary.getMetadata() == null
                    || binary.getMetadata().get("versionCode") == null ? null : binary.getMetadata().get("versionCode").asInt());
        }
        if (offerRevision.getCountries() != null && offerRevision.getCountries().get(country) != null) {
            if (offerRevision.getCountries() == null && offerRevision.getCountries().get(country) == null &&
                    offerRevision.getCountries().get(country).getReleaseDate() == null) {
                Assert.assertNull(appDetails.getReleaseDate());
            }
            else {
                Assert.assertEquals(appDetails.getReleaseDate(), offerRevision.getCountries().get(country).getReleaseDate());
            }
        }

        // verify release notes
        verifyRevisionNote(appDetails.getRevisionNotes(), itemRevisions);

        Assert.assertNull(appDetails.getContentRating());
        Assert.assertNull(appDetails.getDeveloperWebsite());
        Assert.assertNull(appDetails.getDeveloperWebsite());
        Assert.assertNull(appDetails.getPublisherEmail());
        Assert.assertNull(appDetails.getPublisherWebsite());
    }

    private void verifyRevisionNote(List<RevisionNote> revisionNotes, List<ItemRevision> itemRevisions) {
        // verify release notes
        List<ItemRevision> revisions = new ArrayList<>(itemRevisions);
        Collections.sort(revisions, new Comparator<ItemRevision>() {
            @Override
            public int compare(ItemRevision o1, ItemRevision o2) {
                return o2.getUpdatedTime().compareTo(o1.getUpdatedTime());
            }
        });
        Assert.assertEquals(revisions.size(), revisionNotes.size());
        for (int i = 0;i < revisionNotes.size(); ++i) {
            RevisionNote revisionNote = revisionNotes.get(i);
            ItemRevision historyItemRevision = revisions.get(i);
            ItemRevisionLocaleProperties historyLocalProperties = historyItemRevision.getLocales().get(locale);
            Binary historyBinary = historyItemRevision.getBinaries() == null ? null : historyItemRevision.getBinaries().get(Platform);

            Assert.assertEquals(revisionNote.getTitle(), historyLocalProperties == null ? null : historyLocalProperties.getReleaseNotes().getShortNotes());
            Assert.assertEquals(revisionNote.getDescription(), historyLocalProperties == null ? null :  historyLocalProperties.getReleaseNotes().getLongNotes());
            Assert.assertEquals(revisionNote.getVersionCode(), historyBinary == null || historyBinary.getMetadata() == null
                    || historyBinary.getMetadata().get("versionCode") == null ? null : historyBinary.getMetadata().get("versionCode").asInt());
            Assert.assertEquals(revisionNote.getVersionString(), historyBinary == null ? null : historyBinary.getVersion());
        }
    }

    private void verifyItemOffer(com.junbo.store.spec.model.browse.document.Offer offer,
                                 com.junbo.catalog.spec.model.offer.Offer catalogOffer, OfferRevision offerRevision, boolean isFree) {
        Assert.assertEquals(offer.getSelf(), new OfferId(catalogOffer.getId()));
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = offerRevision.getLocales() == null ? null : offerRevision.getLocales().get(locale);
        Assert.assertEquals(offer.getFormattedDescription(), offerRevisionLocaleProperties == null ? null : offerRevisionLocaleProperties.getShortDescription());
        Assert.assertEquals(offer.getIsFree().booleanValue(), isFree);
        if (isFree) {
            Assert.assertEquals(offer.getPrice().doubleValue(), BigDecimal.ZERO.doubleValue(), 0.00001);
        }
        Assert.assertEquals(offer.getCurrency().getValue(), "USD");
    }

    private String getImageSizeGroup(String dimensionText) {
        Matcher matcher = ImageDimensionTextPattern.matcher(dimensionText);
        matcher.matches();
        int width = Integer.parseInt(matcher.group(1));
        int height = Integer.parseInt(matcher.group(2));
        int length = Math.max(width, height);
        Map.Entry<Integer, String> entry = lengthToImageSizeGroup.ceilingEntry(length);
        if (entry == null) {
            entry = lengthToImageSizeGroup.lastEntry();
        }
        return entry.getValue();
    }

    private void verifyImageMap(Map<String, Image> storeImageMap, Map<String , com.junbo.catalog.spec.model.common.Image> catalogImageMap) {
        if (catalogImageMap == null) {
            Assert.assertNull(storeImageMap);
            return;
        }
        Map<String , Image> expected = new HashMap<>();
        for (Map.Entry<String , com.junbo.catalog.spec.model.common.Image> entry : catalogImageMap.entrySet()) {
            try {
                String groupName = getImageSizeGroup(entry.getKey());
                Image storeImage = new Image();
                storeImage.setAltText(entry.getValue().getAltText());
                storeImage.setImageUrl(entry.getValue().getHref());
                expected.put(groupName, storeImage);
            } catch (Exception ex) {
                assert ex != null;
            }
        }
        Assert.assertEquals(storeImageMap.keySet(), expected.keySet());
        for (Map.Entry<String, Image> entry : storeImageMap.entrySet()) {
            Assert.assertEquals(entry.getValue().getAltText(), expected.get(entry.getKey()).getAltText(), "altText not equal");
            Assert.assertEquals(entry.getValue().getImageUrl(), expected.get(entry.getKey()).getImageUrl(), "image url not equal");
        }
    }

    private List<ItemRevision> getItemRevisions(Item item) throws Exception {
        HashMap<String, List<String>> params = new HashMap<>();
        params.put("itemId", Collections.singletonList(item.getId()));
        params.put("status", Collections.singletonList("APPROVED"));
        Results<ItemRevision> revisionResults = storeTestDataProvider.itemRevisionClient.getItemRevisions(params);
        return revisionResults.getItems();
    }

    private List<OfferRevision> getOfferRevisions(com.junbo.catalog.spec.model.offer.Offer offer) throws Exception {
        HashMap<String, List<String>> params = new HashMap<>();
        params.put("offerId", Collections.singletonList(offer.getId()));
        params.put("status", Collections.singletonList("APPROVED"));
        Results<OfferRevision> revisionResults = storeTestDataProvider.offerRevisionClient.getOfferRevisions(params);
        return revisionResults.getItems();
    }

    private <T> void verifyEquals(Map<String, T> o1, Map<String, T> o2, VerifyEqual<T> func) {
        if (o2 == null) {
            Assert.assertNull(o1);
            return;
        }
        Assert.assertEquals(o1.size(), o2.size());
        Assert.assertEquals(o1.keySet(), o2.keySet());
        for (Map.Entry<String, T> entry : o1.entrySet()) {
            func.verify(entry.getValue(), o2.get(entry.getKey()));
        }
    }

    private void verifySupportedLocaleEquals(Map<String, SupportedLocale> o1, Map<String, SupportedLocale> o2) {
        verifyEquals(o1, o2, new VerifyEqual<SupportedLocale>() {
            @Override
            public void verify(SupportedLocale o1, SupportedLocale o2) {
                Assert.assertEquals(o1.isAudio(), o2.isAudio());
                Assert.assertEquals(o1.isInterfac3(), o2.isInterfac3());
                Assert.assertEquals(o1.isSubtitles(), o2.isSubtitles());
            }
        });
    }

    private void verifyAgeRatingEquals(Map<String, List<AgeRating>> o1, Map<String, List<AgeRating>> o2) {
        verifyEquals(o1, o2, new VerifyEqual<List<AgeRating>>() {
            @Override
            public void verify(List<AgeRating> o1, List<AgeRating> o2) {
                Assert.assertEquals(o1.size(), o2.size());
                for (int i = 0;i < o1.size();++i) {
                    AgeRating a1 = o1.get(i);
                    AgeRating a2 = o2.get(i);
                    Assert.assertEquals(a1.getCategory(), a2.getCategory());
                    Assert.assertEquals(a1.getCertificate(), a2.getCertificate());
                    Assert.assertEquals(a1.getDescriptors(), a2.getDescriptors());
                    Assert.assertEquals(a1.getOnline(), a2.getOnline());
                    Assert.assertEquals(a1.getProvisional(), a2.getProvisional());
                }
            }
        });
    }

    private Organization getOrganization(OrganizationId organizationId, boolean serviceClientEnabled) {
        if (!serviceClientEnabled) {
            return null;
        }
        try {
            return storeTestDataProvider.getOrganization(organizationId, 0);
        } catch (Exception e) {
            LOGGER.error("name=Get_Organization_Error", e);
            return null;
        }
    }
}
